package com.chinmay.taskapp.voice



import android.content.Context
import android.util.Log
import com.chinmay.taskapp.util.CredentialsManager
import com.google.assistant.embedded.v1alpha2.*
import com.google.auth.oauth2.UserCredentials
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

class GoogleAssistantClient(private val context: Context) {

    private val credentials: UserCredentials = CredentialsManager.loadCredentials(context)
    private var channel: ManagedChannel? = null
    private var assistantStub: EmbeddedAssistantGrpc.EmbeddedAssistantStub? = null

    init {
        setupAssistant()
    }

    private fun setupAssistant() {
        try {
            // Create a gRPC channel to communicate with Google Assistant API
            channel = ManagedChannelBuilder.forTarget("embeddedassistant.googleapis.com")
                .enableRetry()
                .build()

            // Create an Assistant Stub for communication
            assistantStub = EmbeddedAssistantGrpc.newStub(channel)
            Log.d("GoogleAssistant", "Assistant setup complete")
        } catch (e: Exception) {
            Log.e("GoogleAssistant", "Error setting up Assistant: ${e.message}")
        }
    }

    fun sendVoiceCommand(voiceData: ByteArray, onResult: (String) -> Unit) {
        val request = AssistRequest.newBuilder()
            .setConfig(AssistConfig.newBuilder()
                .setAudioInConfig(AudioInConfig.newBuilder()
                    .setEncoding(AudioInConfig.Encoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .build())
                .setAudioOutConfig(AudioOutConfig.newBuilder()
                    .setEncoding(AudioOutConfig.Encoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setVolumePercentage(100)
                    .build())
                .build())
            .setAudioIn(voiceData.toByteString())  // Convert ByteArray to ByteString
            .build()

        // Handle response from Assistant
        val responseObserver = object : StreamObserver<AssistResponse> {
            override fun onNext(response: AssistResponse) {
                if (response.hasDialogStateOut()) {
                    val responseText = response.dialogStateOut.supplementalDisplayText
                    Log.d("GoogleAssistant", "Assistant Response: $responseText")
                    onResult(responseText)
                }
            }

            override fun onError(t: Throwable) {
                Log.e("GoogleAssistant", "Error in Assistant request: ${t.message}")
                onResult("Error: ${t.message}")
            }

            override fun onCompleted() {
                Log.d("GoogleAssistant", "Assistant request completed")
            }
        }

        // Send request to Assistant API
        assistantStub?.assist(responseObserver)?.onNext(request)
    }

    fun shutdown() {
        try {
            channel?.shutdown()?.awaitTermination(5, TimeUnit.SECONDS)
            Log.d("GoogleAssistant", "Assistant client shutdown")
        } catch (e: Exception) {
            Log.e("GoogleAssistant", "Error shutting down: ${e.message}")
        }
    }
}

