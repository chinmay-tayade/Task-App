package com.chinmay.taskapp.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        internal  fun Long.toDate(): Date {
            return Date(this)
        }

        internal fun Date.toTimestamp(): Long {
            return this.time
        }


        internal fun generateTimeStampId(): String{
            val dateTimeFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = Date()
            val dateTime = dateTimeFormatter.format(currentDate)
            return dateTime
        }

        internal fun extractJson(responseText: String): String {
            return try {

                responseText.substringAfter("```json", responseText).substringBefore("```", responseText).trim()

            } catch (e: Exception) {

                Log.e("GeminiAPI", "Response is not a valid JSON format: $responseText", e)
                ""
            }
        }
    }
}