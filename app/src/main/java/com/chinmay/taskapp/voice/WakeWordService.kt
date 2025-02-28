package com.chinmay.taskapp.voice


import com.chinmay.taskapp.BuildConfig
import android.content.Context
import ai.picovoice.porcupine.PorcupineManager
import android.util.Log
import com.chinmay.taskapp.R
import com.chinmay.taskapp.util.Utility


class WakeWordService(
    private val context: Context,
    private val onWakeWordDetected: () -> Unit
) {
    private var porcupineManager: PorcupineManager? = null

    fun startListening() {
        try {
            val ppnFile = Utility.getPpnFilePath(context, R.raw.task_manager)

            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(BuildConfig.PICO_VOICE_TOKEN)
                .setKeywordPaths(arrayOf(ppnFile))
                .setSensitivity(0.7f)
                .build(context) { keywordIndex ->
                    if (keywordIndex == 0) {
                        Log.d("WakeWordService", "Wake word detected: Hello Task Manager")
                        onWakeWordDetected()
                    }
                }

            porcupineManager?.start()
            Log.d("WakeWordService", "Listening for 'Hello Task Manager'...")
        } catch (e: Exception) {
            Log.e("WakeWordService", "Error initializing wake word detection", e)
        }
    }

    fun stopListening() {
        porcupineManager?.stop()
        porcupineManager?.delete()
        porcupineManager = null
    }
}


