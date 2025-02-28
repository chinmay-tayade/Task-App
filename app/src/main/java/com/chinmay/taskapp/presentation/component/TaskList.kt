package com.chinmay.taskapp.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.presentation.viewmodel.TaskViewModel

@Composable
fun TaskList(tasks: List<Task>, viewModel: TaskViewModel) {
    if (tasks.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📭 No tasks available",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start adding new tasks!",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    TaskItem(task, onClick = { viewModel.updateTaskStatus(task) })
                }
            }
        }
    }
}

fun TaskStatus.isCompleted(): Boolean {
    return this == TaskStatus.COMPLETED
}
