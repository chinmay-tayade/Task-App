package com.chinmay.taskapp.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinmay.taskapp.data.repository.GeminiRepository
import com.chinmay.taskapp.domain.model.*
import com.chinmay.taskapp.domain.usecase.TaskUseCases
import com.chinmay.taskapp.voice.VoiceRecognizer
import com.chinmay.taskapp.voice.WakeWordService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    @ApplicationContext private val context: Context,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var voiceRecognizer: VoiceRecognizer

    var isListening = mutableStateOf(false)
    var recognizedText = mutableStateOf("")



    private val _action = MutableStateFlow<CommandAction?>(null)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch(Dispatchers.IO) { getAllTasks() }
        startWakeWordDetection(context)

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.getDefault()
            }
        }
    }


    private var wakeWordService: WakeWordService? = null


    fun dismissDialog() {
        isListening.value = false
        recognizedText.value = ""
    }

    fun startWakeWordDetection(context: Context) {
        if (wakeWordService == null) { // Initialize only if it's null
            wakeWordService = WakeWordService(context) {
                println("Wake word detected! 🎤 Waiting for user command...")
                isListening.value = true // Show listening UI
                val query = "Hello . Choose whether you want to add, delete, or update a task."
                startVoiceCommand(query,true) // Begin voice input
            }
        }

        wakeWordService?.startListening() // Start only after initialization
    }

    fun initVoiceRecognizer(activity: Activity) {
        voiceRecognizer = VoiceRecognizer(activity) { command -> handleVoiceCommand(command) }
    }

    private fun handleVoiceCommand(command: String) {
        if (_action.value == null) {
            when {
                command.contains("add", ignoreCase = true) -> {
                    _action.value = CommandAction.ADD
                    startVoiceCommand("What is the task?",true)
                }
                command.contains("delete", ignoreCase = true) -> {
                    _action.value = CommandAction.DELETE
                    startVoiceCommand("Which task do you want to delete?",true)
                }
                command.contains("update", ignoreCase = true) -> {
                    _action.value = CommandAction.UPDATE
                    startVoiceCommand("Which task do you want to update?",true)
                }
            }
        } else {
            processVoiceInput(command)
        }
    }

    fun startVoiceCommand(query: String, listen: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("TTS", "Speaking started...")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d("TTS", "Speaking completed. Now starting voice recognition.")
                    if (listen) {
                        viewModelScope.launch(Dispatchers.Main) {
                            voiceRecognizer.startListening()
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.e("TTS", "Error occurred while speaking.")
                }
            })

            textToSpeech.speak(query, TextToSpeech.QUEUE_FLUSH, null, "TaskManagerPrompt")
        }
    }



    private fun processVoiceInput(command: String) {
        viewModelScope.launch(Dispatchers.IO) {

            isListening.value  = false

            val taskDetails = geminiRepository.processVoiceCommand(command,_action.value )
            taskDetails?.let {
                handleTaskCommand(it)
            }
        }
    }

    private fun handleTaskCommand(taskDetails: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            when (_action.value) {
                CommandAction.ADD -> {
                    taskUseCases.addTaskUseCase(taskDetails)
                    startVoiceCommand("Task added successfully.", false)
                }
                CommandAction.UPDATE -> {
                    val task = getTaskByTitle(taskDetails.title)
                    task?.let {
                        taskUseCases.updateTaskUseCase(
                            it.copy(dueDate = taskDetails.dueDate, status = taskDetails.status)
                        )
                        startVoiceCommand("Task updated successfully.", false)
                    }
                }
                CommandAction.DELETE -> {
                    val task = getTaskByTitle(taskDetails.title)
                    task?.let {
                        taskUseCases.deleteTaskUseCase(it)
                        startVoiceCommand("Task deleted successfully.", false)
                    }
                }
                else -> return@launch
            }

            _action.value = null
        }
    }


    private fun getTaskByTitle(title: String): Task? {
        return _tasks.value.find { it.title.equals(title, ignoreCase = true) }
    }

    fun filterTasks(status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            taskUseCases.getTasksUseCase().collect { taskList ->
                _tasks.value = taskList.filter { it.status == status }
            }
        }
    }

    fun getAllTasks() {
        viewModelScope.launch {
            taskUseCases.getTasksUseCase().collect { taskList ->
                Log.d("TaskViewModel", "Fetched tasks: ${taskList.size}")
                _tasks.update { taskList }
            }
        }
    }


    fun updateTaskStatus(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(status = TaskStatus.COMPLETED)
            taskUseCases.updateTaskUseCase(updatedTask)
        }
    }
}
