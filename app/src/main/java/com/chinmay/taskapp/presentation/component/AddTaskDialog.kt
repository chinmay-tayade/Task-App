package com.chinmay.taskapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddTaskDialog(
    isVisible: Boolean,
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
) {
    if (isVisible) {
        val coroutineScope = rememberCoroutineScope()
        var title by remember { mutableStateOf("") }
        var dueDateTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
        var status by remember { mutableStateOf(TaskStatus.PENDING) }

        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        var selectedDateTime by remember {
            mutableStateOf("Select Due Date & Time")
        }

        fun showTimePicker() {
            val timePickerDialog = android.app.TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    dueDateTime = calendar.timeInMillis

                    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    selectedDateTime = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.show()
        }

        fun showDatePicker() {
            val datePickerDialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    showTimePicker()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        Dialog(onDismissRequest = onDismiss) {
            val backgroundColor = Color(0xFFFFF8E1) // Soft cream background
            val textColor = Color(0xFF5C6BC0) // Soft blue for contrast
            val fieldBackground = Color(0xFFE3F2FD) // Light pastel blue for input
            val buttonColor = Color(0xFF81C784) // Pastel green button
            val highlightColor = Color(0xFFFFB74D) // Warm orange accent

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Add New Task", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = highlightColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title", color = textColor) },
                        textStyle = LocalTextStyle.current.copy(color = textColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(fieldBackground)
                            .padding(6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(fieldBackground)
                            .clickable { showDatePicker() }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedDateTime, fontSize = 16.sp, color = textColor)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Status:", fontSize = 16.sp, color = textColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TaskStatus.entries.forEach { taskStatus ->
                                Text(
                                    text = taskStatus.name,
                                    fontSize = 14.sp,
                                    fontWeight = if (status == taskStatus) FontWeight.Bold else FontWeight.Normal,
                                    color = if (status == taskStatus) highlightColor else textColor,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (status == taskStatus) highlightColor.copy(alpha = 0.3f) else Color.Transparent)
                                        .clickable { status = taskStatus }
                                        .padding(6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {


                            coroutineScope.launch {
                                if (title.isNotEmpty() && selectedDateTime != "Select Due Date & Time") {
                                    val newTask = Task(title = title, dueDate = dueDateTime, status = status)
                                    viewModel.pushTask(newTask)
                                    onDismiss()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Save Task", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
