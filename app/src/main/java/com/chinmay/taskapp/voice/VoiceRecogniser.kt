package com.chinmay.taskapp.voice

import android.app.PendingIntent.getActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceRecognizerService(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isEndOfSpeech = false

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {


            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("VoiceRecognizer", "Ready for Speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d("VoiceRecognizer", "Speech started")
                        isEndOfSpeech = false
                    }

                    override fun onResults(results: Bundle?) {
                        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                        if (text != null) {
                            onResult(text)
                        } else {
                            onError("No speech recognized.")
                        }
                    }

                    override fun onError(error: Int) {
                        val errorMessage = getErrorText(error)
                        Log.e("VoiceRecognizer", "Error: $errorMessage")

                        if (!isEndOfSpeech) return

                        onError(errorMessage)


                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            restartListening()
                        }
                    }

                    override fun onEndOfSpeech() {
                        isEndOfSpeech = true
                        Log.d("VoiceRecognizer", "End of Speech")
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                    override fun onRmsChanged(rmsdB: Float) {}
                })
            }

        }else{

            onError("Recognition not available")

        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.EXTRA_LANGUAGE_MODEL)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun restartListening() {
        stopListening()
        startListening()
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission error"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected"
            else -> "Unknown error"
        }
    }
}

