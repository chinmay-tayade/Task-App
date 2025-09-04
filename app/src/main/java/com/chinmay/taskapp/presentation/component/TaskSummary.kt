package com.chinmay.taskapp.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.model.TaskStatus
import com.chinmay.taskapp.presentation.theme.ColorPalette

@Composable
fun TaskSummary(tasks: List<Task>) {
    val completed = tasks.count { it.status == TaskStatus.COMPLETED }
    val pending = tasks.count { it.status == TaskStatus.PENDING }
    val total = tasks.size

    val progress by animateFloatAsState(targetValue = if (total > 0) completed.toFloat() / total else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = ColorPalette.DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Your Task Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(ColorPalette.DarkBackground, shape = RoundedCornerShape(50)),
                color = ColorPalette.NeonBlue,
                trackColor = ColorPalette.PastelBlue,
            )

            Spacer(modifier = Modifier.height(12.dp))


            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TaskStat("Total", total, Color.White, Icons.AutoMirrored.Filled.List)
                TaskStat("Pending", pending, Color.Red, Icons.Outlined.HourglassEmpty)
                TaskStat("Completed", completed, Color.Green, Icons.Filled.TaskAlt)
            }
        }
    }
}

@Composable
fun TaskStat(label: String, count: Int, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 14.sp, color = Color.LightGray)
        Text(text = count.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
