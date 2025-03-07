package com.example.fuudyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF355E37),
    secondary = Color(0xFFE0F1CB),
    background = Color(0xFFE8F5E9),
    surface = Color(0xFFF1F8E9),
    onPrimary = Color.White,
    onSecondary = Color.Black
)

@Composable
fun FuudyApp(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val DarkColorScheme = null
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    if (colors != null) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}
