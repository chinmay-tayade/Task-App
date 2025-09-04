package com.chinmay.taskapp.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.presentation.theme.ColorPalette
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateTimeFormat = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Asia/Kolkata")
    }
    val dueDateTime = dateTimeFormat.format(Date(task.dueDate))

    var isSwipedToDelete by remember { mutableStateOf(false) }

    // ✅ Remove remember { mutableStateOf(task.status.isCompleted()) }
    val isCompleted = task.status.isCompleted() // ✅ Always derive from task object

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            if (isCompleted) ColorPalette.DarkBackground else ColorPalette.DarkSurface,
            ColorPalette.DarkSurface
        )
    )

    AnimatedVisibility(
        visible = !isSwipedToDelete,
        exit = slideOutHorizontally(animationSpec = tween(300)) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -100) {
                            isSwipedToDelete = true
                            onDelete()
                        }
                    }
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(backgroundBrush, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    // Task Title with Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Outlined.DoneAll else Icons.Outlined.Schedule,
                            contentDescription = "Task Status",
                            tint = if (isCompleted) ColorPalette.PastelMint else ColorPalette.PastelPink,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color.Gray else ColorPalette.NeonBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCompleted) "✅ Completed" else "⏳ Pending",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isCompleted) ColorPalette.PastelMint else ColorPalette.PastelPink
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Due Date",
                                tint = ColorPalette.NeonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dueDateTime,
                                fontSize = 14.sp,
                                color = ColorPalette.NeonBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Delete Button
                        IconButton(onClick = { isSwipedToDelete = true; onDelete() }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete Task",
                                tint = ColorPalette.PastelLavender,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // ✅ Use the correct task status directly from ViewModel update
                        Button(
                            onClick = { onClick() },  // ✅ No local state, rely on ViewModel
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) ColorPalette.PastelLavender else ColorPalette.PastelMint
                            ),
                            modifier = Modifier.shadow(6.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isCompleted) "Mark as Pending" else "Mark as Completed",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
