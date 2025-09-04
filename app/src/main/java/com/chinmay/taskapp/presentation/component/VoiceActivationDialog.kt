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
import com.chinmay.taskapp.presentation.theme.ColorPalette

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
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(10.dp, RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(ColorPalette.PastelMint, ColorPalette.CreamWhite)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.mic_lottie))
                    val progress by animateLottieCompositionAsState(composition)

                    Box(
                        modifier = Modifier
                            .size(140.dp) // Increased size 3x
                            .shadow(15.dp, shape = RoundedCornerShape(65.dp), ambientColor = ColorPalette.PastelBlue) // Adjusted for larger size
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(390.dp) // Adjusted animation size
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val alphaAnim = rememberInfiniteTransition()
                    val alpha by alphaAnim.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Text(
                        text = if (recognizedText.isEmpty()) "Listening..." else recognizedText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorPalette.PastelBlue.copy(alpha = alpha)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPalette.PastelPink), // Soft Pastel Button
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .padding(horizontal = 20.dp)
                    ) {
                        Text("Cancel", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
