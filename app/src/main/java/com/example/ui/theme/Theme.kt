package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CrossBeatDarkColorScheme = darkColorScheme(
    primary = CrossPurple, // Baby Blue
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFC2E7FF),
    secondary = SpotifyGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = YouTubeRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF450A0A),
    onTertiaryContainer = Color(0xFFFECACA),
    background = ObsidianDeep,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianStroke
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark mode interface
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CrossBeatDarkColorScheme,
        typography = Typography,
        content = content
    )
}
