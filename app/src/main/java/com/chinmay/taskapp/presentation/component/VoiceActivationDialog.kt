package com.chinmay.taskapp.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.chinmay.taskapp.R

@Composable
fun ListeningDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    recognizedText: String
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212))
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.voice_animation))
                    val progress by animateLottieCompositionAsState(composition)

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50.dp), ambientColor = Color(0xFF00B0FF)) // Neon Blue Glow Effect
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(120.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    val alphaAnim = rememberInfiniteTransition()
                    val alpha by alphaAnim.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Text(
                        text = if (recognizedText.isEmpty()) "Listening..." else recognizedText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00B0FF).copy(alpha = alpha)
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Cancel", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
