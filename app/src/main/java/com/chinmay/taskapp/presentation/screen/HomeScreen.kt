package com.chinmay.taskapp.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.presentation.component.*
import com.chinmay.taskapp.presentation.theme.ColorPalette
import com.chinmay.taskapp.presentation.viewmodel.TaskViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: TaskViewModel = viewModel()) {
    var selectedScreen by remember { mutableStateOf("Home") }
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    ListeningDialog(
        isVisible = viewModel.isListening.value,
        onDismiss = { viewModel.dismissDialog() },
        recognizedText = viewModel.recognizedText.value
    )

    AddTaskDialog(
        isVisible = viewModel.addTaskVisible.value,
        viewModel,
        onDismiss = { viewModel._addTaskVisible.value = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorPalette.PastelBlue, // Pastel Theme Applied
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedScreen, onScreenSelected = { selectedScreen = it })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val query = "Hello. Choose whether you want to add, delete, or update a task."
                    viewModel._recognizedText.value = "Listening..."
                    viewModel._action.value = null
                    viewModel.startVoiceCommand(query, true)
                    viewModel._isListening.value = true
                },
                containerColor = ColorPalette.PastelMint,
                shape = CircleShape,
                modifier = Modifier.shadow(10.dp, CircleShape).size(60.dp)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Voice Input", tint = Color.White)
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(ColorPalette.PastelMauve), // Applied Pastel Background
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = selectedScreen == "Home") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GreetingMessage(viewModel.userName.value, onAddTaskClick = {
                        viewModel._addTaskVisible.value = true
                    })
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = ColorPalette.PastelPink) // Pastel Pink
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TaskSummary(tasks)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.size(220.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorPalette.PastelYellow)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            TaskPieChart(tasks)
                        }
                    }
                }
            }
            when (selectedScreen) {
                "Pending" -> TaskList(tasks.filter { it.status == TaskStatus.PENDING }, viewModel)
                "Completed" -> TaskList(tasks.filter { it.status == TaskStatus.COMPLETED }, viewModel)
                "All" -> TaskList(tasks, viewModel)
            }
        }
    }
}
