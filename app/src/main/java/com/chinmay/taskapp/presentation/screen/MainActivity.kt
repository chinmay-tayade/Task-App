package com.chinmay.taskapp.presentation.screen

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chinmay.taskapp.presentation.component.WelcomeDialog
import com.chinmay.taskapp.presentation.theme.TaskAppTheme
import com.chinmay.taskapp.presentation.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var viewModel: TaskViewModel
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val foregroundServiceGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions[Manifest.permission.FOREGROUND_SERVICE_MICROPHONE] ?: false
        } else {
            true
        }

        if (!recordAudioGranted || !foregroundServiceGranted) {
            showPermissionDeniedDialog()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        if()

        setContent {


            TaskAppTheme {
                viewModel = viewModel()
                viewModel.initTextToSpeech()
                viewModel.startWakeWordDetection()

                var showWelcomeDialog by remember { mutableStateOf(viewModel.isFirstTimeUser()) }
                val username by viewModel.userName.collectAsState()

                if (showWelcomeDialog) {

                    WelcomeDialog(
                        userName = username,
                        onNameEntered = { name ->
                            viewModel.updateUsername(name)
                            showWelcomeDialog = false
                            viewModel.startVoiceCommand("Welcome onboard $name! ", false)
                        }
                    )
                }

                HomeScreen(viewModel)
            }
        }
    }


    private fun checkAndRequestPermissions() {
        val requiredPermissions = mutableListOf(Manifest.permission.RECORD_AUDIO)

        // Add foreground service microphone permission for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE_MICROPHONE)
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Microphone access is needed for voice commands. Please enable it in Settings.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
