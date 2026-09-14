package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CrossBeatDarkColorScheme = darkColorScheme(
    primary = AppPrimary, // Baby Blue
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = AppAccentCyan, // Soft Mint Accent
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = AppAccentLavender, // Soft Pastel Lavender Accent
    onTertiary = Color(0xFF381E72),
    tertiaryContainer = Color(0xFF4C1D95),
    onTertiaryContainer = Color(0xFFEDE9FE),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = AppBorder
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
