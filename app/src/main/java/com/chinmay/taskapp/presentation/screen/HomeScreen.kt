package com.chinmay.taskapp.presentation.screen

import android.speech.tts.TextToSpeech
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
import com.chinmay.taskapp.presentation.viewmodel.TaskViewModel


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


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Task Manager", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF1E1E1E), // Dark Gray
                        titleContentColor = Color.White
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
                        viewModel.startVoiceCommand(query,true)
                        viewModel.isListening.value = true

                    },
                    containerColor = Color(0xFF00B0FF),
                    shape = CircleShape,
                    modifier = Modifier
                        .shadow(15.dp, CircleShape)
                        .size(65.dp)
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Input", tint = Color.White)
                }
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = selectedScreen == "Home") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GreetingMessage()
                        Spacer(modifier = Modifier.height(16.dp))


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF00B0FF)) // Neon Blue
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
                            modifier = Modifier
                                .size(220.dp) ,
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Slightly Darker Gray
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                TaskPieChart(tasks) // Your existing pie chart
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