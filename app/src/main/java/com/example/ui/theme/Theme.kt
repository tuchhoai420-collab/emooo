package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EmoColorScheme = darkColorScheme(
    primary = EmoCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004F54),
    onPrimaryContainer = Color(0xFF80F8FF),
    secondary = EmoPurple,
    onSecondary = Color.White,
    secondaryContainer = EmoPurpleDark,
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = EmoNeonPink,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // EMO aesthetic is dark cyber OLED
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EmoColorScheme,
        typography = Typography,
        content = content
    )
}
