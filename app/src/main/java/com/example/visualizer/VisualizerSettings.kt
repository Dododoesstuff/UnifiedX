package com.example.visualizer

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BabyBlue
import com.example.ui.theme.BabyBlueAccent
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.YouTubeRed

/**
 * Visualizer Render Modes
 */
enum class VisualizerStyle(val displayName: String, val description: String) {
    BARS_SYMMETRIC("Symmetric Bars", "Mirroring vertical frequency spectrum bars"),
    NEON_WAVE("Fluid Neon Wave", "Smooth continuous sine curve with glow"),
    CIRCULAR_BURST("Radial Radial Pulse", "Expanding circular frequency audio spikes"),
    PARTICLE_FIELD("Floating Particles", "Dynamic audio-reactive floating particles"),
    RETRO_VU_METER("Retro Studio VU", "Multi-segment LED sound pressure meter"),
    HOLOGRAPHIC_RIBBON("Holo Ribbon", "Layered gradient neon ribbon waves")
}

/**
 * Color Scheme Palettes for the Waveform
 */
enum class VisualizerColorPalette(val displayName: String, val primary: Color, val secondary: Color, val accent: Color) {
    SOURCE_ADAPTIVE("Source Match", SpotifyGreen, YouTubeRed, BabyBlue),
    CYBER_NEON("Cyber Neon", NeonCyan, ElectricViolet, BabyBlueAccent),
    OBSIDIAN_GOLD("Obsidian Gold", HiResGold, Color(0xFFFFA000), Color(0xFFFFD54F)),
    BABY_BLUE_AURORA("Baby Blue Aurora", BabyBlue, BabyBlueAccent, Color(0xFFE0F2FE)),
    SPECTRUM_CHROMA("Spectrum Chroma", Color(0xFFFF007A), Color(0xFF7928CA), Color(0xFF00DFD8)),
    MONOCHROME_GLOW("Monochrome Ice", Color(0xFFF8FAFC), Color(0xFF94A3B8), Color(0xFF38BDF8))
}

/**
 * Configuration model for waveform visualizer customization
 */
data class VisualizerSettings(
    val isEnabled: Boolean = true,
    val style: VisualizerStyle = VisualizerStyle.BARS_SYMMETRIC,
    val palette: VisualizerColorPalette = VisualizerColorPalette.SOURCE_ADAPTIVE,
    val barCount: Int = 40,            // 16 to 64
    val sensitivity: Float = 1.0f,      // 0.4f to 2.5f
    val smoothing: Float = 0.65f,       // 0.1f to 0.95f
    val showGlow: Boolean = true,
    val showPeakDots: Boolean = true,
    val speed: Float = 1.0f,            // 0.5f to 2.0f
    val mirrorWave: Boolean = true,
    val barCornerRadiusDp: Float = 4f,
    val fillGradient: Boolean = true,
    val audioReactivity: Float = 1.0f
)
