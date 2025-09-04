

package com.chinmay.taskapp.presentation.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinmay.taskapp.data.repository.GeminiRepository
import com.chinmay.taskapp.domain.model.*
import com.chinmay.taskapp.domain.usecase.TaskUseCases
import com.chinmay.taskapp.voice.VoiceRecognizerService
import com.chinmay.taskapp.voice.WakeWordService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    @ApplicationContext private val context: Context,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private var textToSpeech: TextToSpeech? = null
    private lateinit var voiceRecognizer: VoiceRecognizerService
    private var wakeWordService: WakeWordService? = null


    internal val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> get() = _isListening

    internal val _addTaskVisible = mutableStateOf(false)
    val addTaskVisible: State<Boolean> get() = _addTaskVisible

    internal val _recognizedText = mutableStateOf("")
    val recognizedText: State<String> get() = _recognizedText



    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    internal val _action = MutableStateFlow<CommandAction?>(null)

    fun setListening(state: Boolean) {
        _isListening.value = state
    }

    private val sharedPreferences = context.getSharedPreferences("TaskAppPrefs", Context.MODE_PRIVATE)

    private val _userName =  MutableStateFlow(sharedPreferences.getString("username", "") ?: "")

    val userName: StateFlow<String> = _userName

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            _userName.value = newName
            sharedPreferences.edit().putString("username", newName).apply()
        }
    }

    fun isFirstTimeUser(): Boolean {
        return _userName.value.isEmpty()
    }


    init {
        viewModelScope.launch(Dispatchers.IO) { getAllTasks() }
        initVoiceRecognizer()
        initTextToSpeech()
    }

    internal fun startWakeWordDetection() {
        if (wakeWordService == null) {
            viewModelScope.launch(Dispatchers.IO) {
                wakeWordService = WakeWordService(context) {
                    Log.d("WakeWordService", "Wake word detected!")
                    setListening(true)
                    stopWakeWordDetection()
                    viewModelScope.launch(Dispatchers.Main) {
                        startVoiceCommand("Hello. Choose whether you want to add, delete, or update a task.", true)
                        _action.value = null
                    }
                }
                viewModelScope.launch(Dispatchers.Main) { wakeWordService?.startListening() } // Move to Main thread
            }

        }
    }


    private fun stopWakeWordDetection() {
        wakeWordService?.stopListening()
        wakeWordService = null
    }


    fun initVoiceRecognizer() {
        voiceRecognizer = VoiceRecognizerService(context, onResult = { command ->
            handleVoiceCommand(command)
        }, onError = {
            startVoiceCommand("Try again later", false)
            voiceRecognizer.destroy()
            setListening(false)
        })

    }


    fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            } else {
                Log.e("TaskViewModel", "Text-to-Speech initialization failed")
            }
        }
    }

    private fun handleVoiceCommand(command: String) {

        _recognizedText.value = command

        if (_action.value == null) {
            when {
                command.contains("add", ignoreCase = true) -> {
                    _action.value = CommandAction.ADD
                    startVoiceCommand("What is the task?", true)
                }
                command.contains("delete", ignoreCase = true) -> {
                    _action.value = CommandAction.DELETE
                    startVoiceCommand("Which task do you want to delete?", true)
                }
                command.contains("update", ignoreCase = true) -> {
                    _action.value = CommandAction.UPDATE
                    startVoiceCommand("Which task do you want to update?", true)
                }

                else -> {
                    startVoiceCommand("Invalid Command ,Please Try Again", false)
                    dismissDialog()
                }
            }
        } else {
            processVoiceInput(command)

        }
    }


    fun startVoiceCommand(query: String, listen: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("TTS", "Speaking started...")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d("TTS", "Speaking completed. Now starting voice recognition.")
                    if (listen) {
                        Log.d("VoiceRecognizer", "Starting SpeechService...")
                        viewModelScope.launch(Dispatchers.Main) { voiceRecognizer.startListening() }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.e("TTS", "Error occurred while speaking.")
                }
            })

            textToSpeech?.speak(query, TextToSpeech.QUEUE_FLUSH, null, "TaskManagerPrompt")
        }
    }

    private fun processVoiceInput(command: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val success = geminiRepository.processVoiceCommand(command, _action.value)
            if (success) {
                viewModelScope.launch(Dispatchers.Main) {
                    startVoiceCommand("Task updated successfully.", false)
                    setListening(false)
                    getAllTasks()
                }
            }else{
                startVoiceCommand("Ohh Sorry !! , Please Try Again..", false)
                setListening(false)
                getAllTasks()
            }
        }

    }

    fun updateTaskStatus(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = if (task.status == TaskStatus.PENDING) TaskStatus.COMPLETED else TaskStatus.PENDING
            taskUseCases.updateTaskUseCase(task.copy(status = newStatus))
            getAllTasks()
        }
    }

    fun  pushTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            geminiRepository.pushTask(task)
            getAllTasks()
        }

    }

    fun  deleteTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            geminiRepository.deleteTask(task)
            getAllTasks()
        }

    }

    private fun getAllTasks() {
        viewModelScope.launch {
            taskUseCases.getTasksUseCase().collect { taskList ->
                _tasks.update { taskList }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopWakeWordDetection()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    fun dismissDialog() {
        CoroutineScope(Dispatchers.Unconfined)
        viewModelScope.launch(Dispatchers.Main) {
            _recognizedText.value = "Listening...."
            _action.value = null
            setListening(false)
            voiceRecognizer.let {
                it.stopListening()
                it.destroy()
            }
            initVoiceRecognizer()
            startWakeWordDetection()
        }
    }


}