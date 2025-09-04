package com.chinmay.taskapp.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val Shapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(30.dp)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
)

object ColorPalette {

    // Light Theme (Pastel Colors)
    val PastelPink = Color(0xFFF8C8DC)
    val PastelLavender = Color(0xFFD8BFD8)
    val PastelYellow = Color(0xFFFDEFB2)
    val PastelBlue = Color(0xFFB3E5FC)
    val PastelGray = Color(0xFFD3D3D3)
    val PastelMauve = Color(0xFFE0B0FF)
    val PastelCoral = Color(0xFFFF9AA2)
    val PastelMint = Color(0xFF98FB98)
    val CreamWhite = Color(0xFFFFF7ED) // Light Cream White


    // Dark Theme (Neon & Dark Colors)
    val NeonBlue = Color(0xFF1E90FF)
    val DarkSurface = Color(0xFF121212)
    val DarkBackground = Color(0xFF1C1C1C)
}

// Light & Dark Color Schemes
private val LightColorScheme = lightColorScheme(
    primary = ColorPalette.PastelBlue,
    surface = ColorPalette.PastelGray,
    onPrimary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = ColorPalette.NeonBlue,
    surface = ColorPalette.DarkSurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun TaskAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
        ) {
            content()
        }
    }
}
