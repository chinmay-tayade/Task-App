package com.chinmay.taskapp.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.presentation.theme.ColorPalette

@Composable
fun TaskPieChart(tasks: List<Task>) {
    val completed = tasks.count { it.status == TaskStatus.COMPLETED }
    val pending = tasks.count { it.status == TaskStatus.PENDING }
    val total = completed + pending

    if (total == 0) {
        Text(
            "No tasks available",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        return
    }

    val completedAngle = 360f * (completed.toFloat() / total)
    val pendingAngle = 360f - completedAngle

    val completedColor = ColorPalette.NeonBlue
    val pendingColor =  ColorPalette.PastelPink

    Card(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorPalette.DarkSurface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Task Completion",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2

                    drawArc(
                        color = completedColor,
                        startAngle = -90f,
                        sweepAngle = completedAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Fill
                    )

                    drawArc(
                        color = pendingColor,
                        startAngle = -90f + completedAngle,
                        sweepAngle = pendingAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Fill
                    )
                }

                Text(
                    text = "${(completed.toFloat() / total * 100).toInt()}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TaskLegendItem("Completed", completedColor)
                TaskLegendItem("Pending", pendingColor)
            }
        }
    }
}

@Composable
fun TaskLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
