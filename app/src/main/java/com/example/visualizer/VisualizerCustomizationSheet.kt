package com.example.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformSource
import com.example.ui.theme.BabyBlue
import com.example.ui.theme.BabyBlueAccent
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.YouTubeRed
import kotlin.math.roundToInt

/**
 * Full customization sheet & studio for Waveform Visualizer.
 * Provides controls for Visualizer Styles, Color Themes, Spectrum Density,
 * Responsiveness / Sensitivity, Physics Smoothing, Animation Speed, Peak Indicators,
 * Mirroring, and Bloom Glow effects.
 */
@Composable
fun VisualizerCustomizationSheet(
    settings: VisualizerSettings,
    frequencies: FloatArray,
    isPlaying: Boolean,
    platformSource: PlatformSource,
    onSettingsChanged: (VisualizerSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .testTag("visualizer_customization_sheet"),
        colors = CardDefaults.cardColors(containerColor = ObsidianDeep),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            // Drag Indicator Handle
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .align(Alignment.CenterHorizontally)
            )

            // Header Row with Title and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CrossPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Waveform Visualizer Studio",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time spectrum & audio-reactive physics",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_visualizer_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Interactive Live Preview Banner
                item {
                    Text(
                        text = "LIVE SPECTRUM PREVIEW",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, ObsidianStroke, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            WaveformVisualizer(
                                frequencies = frequencies,
                                settings = settings,
                                isPlaying = isPlaying,
                                platformSource = platformSource,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Master Toggle Switch
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Enable Audio Visualizer",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Display real-time waveform on full player and lyrics",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = settings.isEnabled,
                                onCheckedChange = { onSettingsChanged(settings.copy(isEnabled = it)) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CrossPurple,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = ObsidianStroke
                                ),
                                modifier = Modifier.testTag("visualizer_master_switch")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Visualizer Render Style Selector
                item {
                    Text(
                        text = "RENDER STYLES",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VisualizerStyle.values().forEach { style ->
                            val isSelected = settings.style == style
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSettingsChanged(settings.copy(style = style)) }
                                    .testTag("style_${style.name}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CrossPurple.copy(alpha = 0.18f) else ObsidianCard
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CrossPurple) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = style.displayName,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = style.description,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = CrossPurple,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Color Themes & Palettes
                item {
                    Text(
                        text = "COLOR PALETTE THEMES",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(VisualizerColorPalette.values()) { palette ->
                            val isSelected = settings.palette == palette
                            Card(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSettingsChanged(settings.copy(palette = palette)) }
                                    .testTag("palette_${palette.name}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CrossPurple.copy(alpha = 0.2f) else ObsidianCard
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CrossPurple) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(palette.primary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(palette.secondary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(palette.accent)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = palette.displayName,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Frequency Bands / Bar Density Slider
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Frequency Band Density (Bars)",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${settings.barCount} Bands",
                                    color = CrossPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = settings.barCount.toFloat(),
                                onValueChange = { onSettingsChanged(settings.copy(barCount = it.roundToInt())) },
                                valueRange = 16f..64f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = CrossPurple,
                                    activeTrackColor = CrossPurple,
                                    inactiveTrackColor = ObsidianStroke
                                ),
                                modifier = Modifier.testTag("visualizer_bar_count_slider")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Sensitivity / Gain Slider
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Audio Sensitivity / Gain",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${"%.1f".format(settings.sensitivity)}x",
                                    color = CrossPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = settings.sensitivity,
                                onValueChange = { onSettingsChanged(settings.copy(sensitivity = it)) },
                                valueRange = 0.4f..2.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CrossPurple,
                                    activeTrackColor = CrossPurple,
                                    inactiveTrackColor = ObsidianStroke
                                ),
                                modifier = Modifier.testTag("visualizer_sensitivity_slider")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Physics Smoothing Slider
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Physics Damping & Decay",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(settings.smoothing * 100).toInt()}%",
                                    color = CrossPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = settings.smoothing,
                                onValueChange = { onSettingsChanged(settings.copy(smoothing = it)) },
                                valueRange = 0.1f..0.9f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CrossPurple,
                                    activeTrackColor = CrossPurple,
                                    inactiveTrackColor = ObsidianStroke
                                ),
                                modifier = Modifier.testTag("visualizer_smoothing_slider")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Animation Speed Slider
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Oscillation & Wave Speed",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${"%.1f".format(settings.speed)}x",
                                    color = CrossPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = settings.speed,
                                onValueChange = { onSettingsChanged(settings.copy(speed = it)) },
                                valueRange = 0.5f..2.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CrossPurple,
                                    activeTrackColor = CrossPurple,
                                    inactiveTrackColor = ObsidianStroke
                                ),
                                modifier = Modifier.testTag("visualizer_speed_slider")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Feature Toggles: Glow Bloom, Peak Indicators, Mirror Symmetry
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Mirror symmetry toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mirror Waveform Centerline",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = settings.mirrorWave,
                                    onCheckedChange = { onSettingsChanged(settings.copy(mirrorWave = it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = CrossPurple,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = ObsidianStroke
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Glow bloom toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Neon Bloom & Diffusion Glow",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = settings.showGlow,
                                    onCheckedChange = { onSettingsChanged(settings.copy(showGlow = it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = CrossPurple,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = ObsidianStroke
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Peak dots toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Dynamic Peak Hold Dots",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = settings.showPeakDots,
                                    onCheckedChange = { onSettingsChanged(settings.copy(showPeakDots = it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = CrossPurple,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = ObsidianStroke
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Reset to Defaults Button
                item {
                    Button(
                        onClick = { onSettingsChanged(VisualizerSettings()) },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("reset_visualizer_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reset All Visualizer Settings to Defaults",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
