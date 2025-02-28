package com.chinmay.taskapp.data.repository

import android.util.Log
import com.chinmay.taskapp.data.remote.GeminiApiService
import com.chinmay.taskapp.domain.model.CommandAction
import com.chinmay.taskapp.domain.model.CommandAction.*
import com.chinmay.taskapp.domain.model.GeminiRequest
import com.chinmay.taskapp.domain.model.PartRequest
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.util.Utility
import com.chinmay.taskapp.util.Utility.Companion.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.TimeZone
import javax.inject.Inject

class GeminiRepository @Inject constructor(
    private val apiService: GeminiApiService
) {
    private val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    suspend fun processVoiceCommand(command: String,action : CommandAction?): Task? {
        return withContext(Dispatchers.IO) {
            try {
                when (action){

                    ADD -> processAddTask(command)
                    UPDATE -> processUpdateTask(command)
                    DELETE -> processDeleteTask(command)
                    else -> {
                        Log.e("GeminiAPI", "Unknown command format: $command")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Exception in processVoiceCommand", e)
                null
            }
        }
    }

    private suspend fun dispatchGeminiRequest(prompt: String): Task? {
        return withContext(Dispatchers.IO) {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        com.chinmay.taskapp.domain.model.ContentRequest(
                            parts = listOf(PartRequest(prompt))
                        )
                    )
                )

                val response = apiService.generateTaskDetails(GEMINI_API_URL, request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        Log.e("GeminiAPI", "Response body is NULL")
                        return@withContext null
                    }

                    val responseText = responseBody.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text

                    if (responseText.isNullOrEmpty()) {
                        Log.e("GeminiAPI", "Extracted text is NULL or empty")
                        return@withContext null
                    }

                    Log.d("GeminiAPI", "Extracted Response Text: $responseText")

                    parseTaskDetails(responseText)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("GeminiAPI", "Error Response: $errorBody")
                    null
                }
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Exception in dispatchGeminiRequest", e)
                null
            }
        }
    }

    private fun parseTaskDetails(responseText: String): Task? {
        return try {

            Log.d("GeminiAPI", "Raw Response: $responseText")  // ✅ Debugging


            val jsonString = Utility.extractJson(responseText)
            if (jsonString.isEmpty()) {
                Log.e("GeminiAPI", "Response is not valid JSON, ignoring: $responseText")
                return null
            }


            val json = JSONObject(jsonString)
            val title = json.optString("title", "Untitled Task")
            val dueDate = json.optLong("due_date", Utility.getTodayDateInLong())
            val statusString = json.optString("status", "PENDING")

            val status = when {
                statusString.contains("complete", ignoreCase = true) -> TaskStatus.COMPLETED
                else -> TaskStatus.PENDING
            }

            Task(title = title, dueDate =  dueDate.toDate(), status =  status)
        } catch (e: Exception) {

            Log.e("GeminiAPI", "Error parsing task details", e)
            null

        }
    }


    private suspend fun processAddTask(command: String): Task? {
        val timeZone = TimeZone.getDefault() // Get the user's current timezone
        val currentTimeMillis = System.currentTimeMillis() // Get the current timestamp in milliseconds

        val prompt = """
        Extract structured task details for adding a new task. Ensure `due_date` is converted to a **Long** (milliseconds since epoch) based on the user's timezone.

        **User Timezone:** `${timeZone.id}`
        **Current Timestamp (milliseconds):** `$currentTimeMillis`
        
        **Task Fields:**
        - `title`: Extract the task title.
        - `due_date`: Convert to **Long** (milliseconds since epoch) using the timezone `${timeZone.id}`. 
          - If no date is provided, use today's date in this timezone.
        - `status`: Default **PENDING**, but can be **PENDING** or **COMPLETED**.
        
        **Date Handling:**
        - Convert phrases like **"tomorrow"**, **"next Monday"**, **"in two weeks"** to a timestamp using the timezone `${timeZone.id}`.
        - If an exact date is mentioned (e.g., "March 5, 2025"), convert it correctly based on this timezone.
        - If no date is provided, use the current date in this timezone.

        **Example Input & Output:**
        **User:** `"Add a task to complete my math assignment by next Monday."`
        **Current Timestamp in `${timeZone.id}` (milliseconds):** `$currentTimeMillis`
        **Output:** 
        ```json
        { "title": "Complete my math assignment", "due_date": 1711843200000, "status": "PENDING" }
        ```

        Now, process this command: "$command"
    """.trimIndent()

        return dispatchGeminiRequest(prompt)
    }



    private suspend fun processUpdateTask(command: String): Task? {
        val prompt = """
        Extract task details for updating an existing task:
        - `due_date`: Update if mentioned; otherwise, keep the same.
        - `status`: **PENDING/COMPLETED**.

        Example:
        **User:** "Update the math assignment to completed."
        **Output:** {"title": "Complete my math assignment", "status": "COMPLETED"}

        Now, process this command: $command
    """.trimIndent()

        return dispatchGeminiRequest(prompt)
    }

    private suspend fun processDeleteTask(command: String): Task? {
        val prompt = """
        Extract task details for deleting a task:
        - `title`: Extract task title.
        - `action`: Always return `"DELETE"`.

        Example:
        **User:** "Delete the math assignment task."
        **Output:** {"title": "Complete my math assignment", "action": "DELETE"}

        Now, process this command: $command
    """.trimIndent()

        return dispatchGeminiRequest(prompt)
    }



}
