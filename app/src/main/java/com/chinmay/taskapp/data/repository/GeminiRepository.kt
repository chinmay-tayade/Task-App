package com.chinmay.taskapp.data.repository

import android.util.Log
import com.chinmay.taskapp.data.local.TaskDao
import com.chinmay.taskapp.data.local.TaskEntity
import com.chinmay.taskapp.data.remote.GeminiApiService
import com.chinmay.taskapp.domain.model.*
import com.chinmay.taskapp.util.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Inject

class GeminiRepository @Inject constructor(
    private val apiService: GeminiApiService,
    private val taskDao: TaskDao
) {
    private val GEMINI_API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"


    suspend fun processVoiceCommand(command: String, action: CommandAction?): Boolean {
        return when (action) {
            CommandAction.ADD -> processAddTask(command)
            CommandAction.UPDATE -> processUpdateTask(command)
            CommandAction.DELETE -> processDeleteTask(command)
            else -> false
        }
    }

    private suspend fun <T> dispatchGeminiRequestGeneric(
        prompt: String,
        parseResponse: (String) -> T?
    ): T? {
        return withContext(Dispatchers.IO) {
            var attempts = 0
            while (attempts < 2) {
                try {
                    val response = apiService.generateTaskDetails(
                        GEMINI_API_URL,
                        GeminiRequest(listOf(ContentRequest(listOf(PartRequest(prompt)))))
                    )

                    if (response.isSuccessful) {
                        val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        responseText?.let { parsedResult ->
                            val result = parseResponse(parsedResult)
                            if (result != null) return@withContext result  // ✅ Return valid response
                        }
                    } else {
                        Log.e("GeminiAPI", "API Error: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("GeminiAPI", "Exception in Gemini request: ${e.message}")
                }

                attempts++
                delay(500)  // ✅ Wait before retrying
            }

            Log.e("GeminiAPI", "Gemini API failed, returning fallback value")
            return@withContext null  // ✅ Return `null` for generic failure case
        }
    }


    private suspend fun dispatchGeminiRequestUpdate(prompt: String): UpdatedPair? {
        return dispatchGeminiRequestGeneric(prompt) { Utility.parseTaskPair(it) }
    }
    private suspend fun dispatchGeminiRequestAdd(prompt: String): Task? {
        return dispatchGeminiRequestGeneric(prompt) { Utility.parseTaskDetails(it) }
    }
    private suspend fun dispatchGeminiRequestDelete(prompt: String): Long {
        return dispatchGeminiRequestGeneric(prompt) { responseText ->
            Utility.parseTaskId(responseText) ?: 0L
        } ?: 0L
    }




    private suspend fun processAddTask(command: String): Boolean {

        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val currentTimeMillis = System.currentTimeMillis()

        val prompt = """
        Extract structured task details for adding a new task. Ensure robustness by handling various cases.
        $timeZone is as given 
        **Processing Rules:**
        - Extract title (mandatory) and due_date (if available).
        - Convert due_date to **Long (milliseconds since epoch)** using the timezone **IST**.
        - Handle multiple date formats:
            - "March 5, 2025" → Convert to timestamp.
            - "tomorrow at 10 AM" → Convert to **(today's date and 10 AM in IST)**.
        - If no due_date is provided, default to today **($currentTimeMillis)**.
        - Extract time if mentioned, otherwise default to **current time in IST**.
        - If **only time** is provided ("10 AM today"), use **today's date + extracted time**.
        - Handle edge cases like vague descriptions or missing details.
        - Return structured JSON with "title", "due_date", and "status".

        **Examples**
        **User:** "Add a meeting at 10 AM tomorrow."
        **Output:**  
        
json
        { "title": "Meeting", "due_date": due_date, "status": "PENDING" }


        **User:** "Set deadline for March 10 at 5 PM"
        **Output:**  
        
json
        { "title": "Project Deadline", "due_date": due_date, "status": "PENDING" }


        Now, process this command: "$command"
    """.trimIndent()


        val extractedTask = dispatchGeminiRequestAdd(prompt) ?: return false
        val validatedTask = Utility.validateTaskDetails(extractedTask)
        pushTask(validatedTask)
        return true
    }

    private suspend fun processUpdateTask(command: String): Boolean {

        val tasks = taskDao.getAllTasks().firstOrNull() ?: return false

        val tasksJson = tasks.map {
            """
        {
            "id": ${it.id},
            "title": "${it.title}",
            "due_date": ${it.dueDate},
            "status": "${it.status}"
        }
        """
        }.joinToString(separator = ",", prefix = "[", postfix = "]")

        val prompt = """
    Here is the list of current tasks in JSON format:
    ```json
    $tasksJson
    ```

    Based on the following command, identify the correct task that needs to be updated and modify it accordingly:
    "$command"

    Your output must strictly follow this JSON format:
    ```json
    {
        "old": {
            "id": 1,
            "title": "Buy Grocery",
            "due_date": 1711929600000,
            "status": "PENDING"
        },
        "updated": {
            "id": 1, 
            "title": "Buy Milk", 
            "due_date": 1712023600000, 
            "status": "PENDING"
        }
    }
    ```

    - **Find the most relevant task (OLD) from the list based on the command**.
    - **Modify the title, due date, or status as specified in the command**.
    - **The `id` must remain the same** to ensure only the correct task is updated.
    """.trimIndent()


        val extractedPair = dispatchGeminiRequestUpdate(prompt) ?: return false


        val existingTask = tasks.find { it.id == extractedPair.old.id }
            ?: return false


        val updatedTask = TaskEntity(
            id = existingTask.id,
            title = extractedPair.updated.title,
            dueDate = extractedPair.updated.dueDate,
            status = extractedPair.updated.status
        )


        taskDao.updateTask(updatedTask)
        return true
    }



    private suspend fun processDeleteTask(command: String): Boolean {

        val tasks = taskDao.getAllTasks().firstOrNull() ?: return false


        val tasksJson = tasks.map {
            """
        {
            "id": ${it.id},
            "title": "${it.title}",
            "due_date": ${it.dueDate},
            "status": "${it.status}"
        }
        """
        }.joinToString(separator = ",", prefix = "[", postfix = "]")

        val prompt = """
    Here is the list of current tasks in JSON format:
    ```json
    $tasksJson
    ```

    Based on the following command, find the most relevant task that should be deleted:
    "$command"

    Your output must **strictly** follow this JSON format:
    ```json
    {
        "id": 3
    }
    ```
    
    - **Find the best matching task ID from the list based on the command.**
    - **If no relevant task is found, return an empty ID (`0`).**
    """.trimIndent()

        val taskId = dispatchGeminiRequestDelete(prompt)

        if (taskId == 0L) {
            Log.e("DeleteTask", "No relevant task found for deletion.")
            return false
        }

        val existingTask = taskDao.getTaskById(taskId)
        existingTask?.let {
            taskDao.deleteTask(it)
            return true
        }

        return false

    }


    suspend fun pushTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.getTaskByTitle(task.title.trim().lowercase())?.let {
            taskDao.deleteTask(it)
        }
    }

}
