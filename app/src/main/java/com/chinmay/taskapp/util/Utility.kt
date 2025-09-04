package com.chinmay.taskapp.util

import android.content.Context
import android.util.Log
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.domain.model.UpdatedPair
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.TimeZone

class Utility {
    companion object {

        internal  fun getTodayDateInLong(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }


        internal fun getPpnFilePath(context: Context, resId: Int): String {
            val rawFile = context.resources.openRawResource(resId)
            val file = File(context.filesDir, "task_manager.ppn")

            rawFile.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        }

        internal fun extractJson(responseText: String): String {
            return try {
                responseText.substringAfter("```json", responseText).substringBefore("```", responseText).trim()
            } catch (e: Exception) {

                Log.e("GeminiAPI", "Response is not a valid JSON format: $responseText", e)
                ""
            }
        }


        /** ✅ Parse Gemini’s JSON Response Safely */
        internal fun parseTaskDetails(responseText: String): Task? {
            return try {
                val json = JSONObject(Utility.extractJson(responseText))
                Task(
                    title = json.optString("title", "Untitled Task"),
                    dueDate = json.optLong("due_date", Utility.getTodayDateInLong()),
                    status = if (json.optString("status", "PENDING").equals("COMPLETED", true))
                        TaskStatus.COMPLETED else TaskStatus.PENDING
                )
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Error parsing task details: ${e.message}")
                null
            }
        }

        internal fun parseTaskPair(responseText: String): UpdatedPair? {
            return try {
                val json = JSONObject(Utility.extractJson(responseText))

                val oldTaskJson = json.getJSONObject("old")
                val updatedTaskJson = json.getJSONObject("updated")

                val oldTask = Task(
                    id = oldTaskJson.optLong("id"),
                    title = oldTaskJson.optString("title", "Untitled Task"),
                    dueDate = oldTaskJson.optLong("due_date", Utility.getTodayDateInLong()),
                    status = if (oldTaskJson.optString("status", "PENDING").equals("COMPLETED", true))
                        TaskStatus.COMPLETED else TaskStatus.PENDING
                )

                val updatedTask = Task(
                    id = updatedTaskJson.optLong("id"),
                    title = updatedTaskJson.optString("title", oldTask.title),  // Default to old title if missing
                    dueDate = updatedTaskJson.optLong("due_date", oldTask.dueDate),  // Default to old date if missing
                    status = if (updatedTaskJson.optString("status", oldTask.status.toString()).equals("COMPLETED", true))
                        TaskStatus.COMPLETED else TaskStatus.PENDING
                )

                UpdatedPair(old = oldTask, updated = updatedTask)

            } catch (e: Exception) {
                Log.e("GeminiAPI", "Error parsing task details: ${e.message}")
                null
            }
        }

        internal fun validateTaskDetails(task: Task): Task {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
            val dueDateMillis = task.dueDate.takeIf { it > 0 } ?: calendar.timeInMillis

            return task.copy(
                title = task.title.ifEmpty { "Untitled Task" },
                dueDate = dueDateMillis
            )
        }

        internal fun parseTaskId(responseText: String): Long? {
            return try {
                val json = JSONObject(Utility.extractJson(responseText))
                val taskId = json.optLong("id", 0L)  // ✅ Default to `0L` if no match found
                if (taskId > 0) taskId else null
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Error parsing task ID: ${e.message}")
                null
            }
        }


    }
}