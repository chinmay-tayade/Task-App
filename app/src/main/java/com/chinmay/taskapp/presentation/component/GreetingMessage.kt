package com.chinmay.taskapp.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.chinmay.taskapp.presentation.theme.ColorPalette
import java.util.Calendar


@Composable
fun GreetingMessage(userName: String, onAddTaskClick: () -> Unit) {
    val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        time < 12 -> "Good Morning ☀️"
        time < 18 -> "Good Afternoon ☀️"
        else -> "Good Evening 🌙"
    }

    val alphaAnim = rememberInfiniteTransition()
    val alpha by alphaAnim.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(10.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ColorPalette.PastelLavender) // Soft Lavender Background
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = greeting,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPalette.PastelBlue.copy(alpha = alpha) // Soft Blue Text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome back, ${userName} 🚀",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorPalette.PastelPink // Soft Pink for Friendly Touch
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(ColorPalette.PastelCoral, shape = RoundedCornerShape(50)) // Soft Peach Button
                        .clickable { onAddTaskClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                        contentDescription = "Add Task",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Add Task",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = ColorPalette.PastelGray, // Softer Gray for Muted Effect
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
