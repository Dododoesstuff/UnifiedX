package com.example.visualizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.PlatformSource
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.YouTubeRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * High-performance hardware-accelerated Canvas waveform visualizer.
 * Supports multiple render styles: Symmetric Bars, Neon Waves, Circular Radial Burst,
 * Floating Particle Field, Retro VU Meter, and Holo Ribbon.
 */
@Composable
fun WaveformVisualizer(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    isPlaying: Boolean,
    platformSource: PlatformSource = PlatformSource.SPOTIFY,
    modifier: Modifier = Modifier
) {
    if (!settings.isEnabled || frequencies.isEmpty()) {
        Box(modifier = modifier)
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_rotation")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (4000 / settings.speed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseOffset"
    )

    // Dynamic color resolution
    val primaryColor = when (settings.palette) {
        VisualizerColorPalette.SOURCE_ADAPTIVE -> if (platformSource == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed
        else -> settings.palette.primary
    }
    val secondaryColor = when (settings.palette) {
        VisualizerColorPalette.SOURCE_ADAPTIVE -> if (platformSource == PlatformSource.SPOTIFY) Color(0xFF10B981) else Color(0xFFFF5252)
        else -> settings.palette.secondary
    }
    val accentColor = settings.palette.accent

    val intensityMultiplier = if (isPlaying) 1.0f else 0.15f

    Canvas(modifier = modifier) {
        when (settings.style) {
            VisualizerStyle.BARS_SYMMETRIC -> {
                drawSymmetricBars(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    accent = accentColor,
                    intensity = intensityMultiplier
                )
            }
            VisualizerStyle.NEON_WAVE -> {
                drawNeonWave(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    phase = phaseOffset,
                    intensity = intensityMultiplier
                )
            }
            VisualizerStyle.CIRCULAR_BURST -> {
                drawCircularBurst(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    accent = accentColor,
                    rotationPhase = phaseOffset,
                    intensity = intensityMultiplier
                )
            }
            VisualizerStyle.PARTICLE_FIELD -> {
                drawParticleField(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    accent = accentColor,
                    phase = phaseOffset,
                    intensity = intensityMultiplier
                )
            }
            VisualizerStyle.RETRO_VU_METER -> {
                drawRetroVuMeter(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    intensity = intensityMultiplier
                )
            }
            VisualizerStyle.HOLOGRAPHIC_RIBBON -> {
                drawHolographicRibbon(
                    frequencies = frequencies,
                    settings = settings,
                    primary = primaryColor,
                    secondary = secondaryColor,
                    accent = accentColor,
                    phase = phaseOffset,
                    intensity = intensityMultiplier
                )
            }
        }
    }
}

// -------------------------------------------------------------
// RENDERERS
// -------------------------------------------------------------

private fun DrawScope.drawSymmetricBars(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    accent: Color,
    intensity: Float
) {
    val barCount = min(frequencies.size, settings.barCount)
    if (barCount <= 0) return

    val totalWidth = size.width
    val totalHeight = size.height
    val centerY = totalHeight / 2f
    val barSpacing = totalWidth * 0.015f
    val barWidth = (totalWidth - (barSpacing * (barCount - 1))) / barCount.coerceAtLeast(1)
    val cornerRadius = CornerRadius(settings.barCornerRadiusDp.dp.toPx(), settings.barCornerRadiusDp.dp.toPx())

    for (i in 0 until barCount) {
        val magnitude = (frequencies[i] * intensity).coerceIn(0.04f, 1.0f)
        val barHeight = (totalHeight * 0.85f) * magnitude
        val left = i * (barWidth + barSpacing)

        val top = if (settings.mirrorWave) centerY - (barHeight / 2f) else totalHeight - barHeight
        val height = barHeight

        // Vertical gradient for high-end look
        val brush = Brush.verticalGradient(
            colors = listOf(primary, secondary),
            startY = top,
            endY = top + height
        )

        // Draw main bar
        drawRoundRect(
            brush = brush,
            topLeft = Offset(left, top),
            size = Size(barWidth, height),
            cornerRadius = cornerRadius
        )

        // Draw Peak Dot
        if (settings.showPeakDots && intensity > 0.3f) {
            val peakY = if (settings.mirrorWave) top - 4.dp.toPx() else top - 4.dp.toPx()
            drawCircle(
                color = accent,
                radius = (barWidth * 0.45f).coerceIn(2.dp.toPx(), 4.dp.toPx()),
                center = Offset(left + (barWidth / 2f), peakY)
            )
        }
    }
}

private fun DrawScope.drawNeonWave(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    phase: Float,
    intensity: Float
) {
    val count = min(frequencies.size, settings.barCount)
    if (count < 2) return

    val totalWidth = size.width
    val totalHeight = size.height
    val centerY = totalHeight / 2f
    val dx = totalWidth / (count - 1).toFloat()

    val path = Path()
    val fillPath = Path()
    fillPath.moveTo(0f, totalHeight)

    for (i in 0 until count) {
        val mag = (frequencies[i] * intensity).coerceIn(0.05f, 1.0f)
        val waveOffset = sin(phase + (i.toFloat() / count) * PI * 2).toFloat() * 15.dp.toPx()
        val y = centerY - (mag * (totalHeight * 0.4f) * sin((i.toFloat() / count) * PI).toFloat()) + waveOffset
        val x = i * dx

        if (i == 0) {
            path.moveTo(x, y)
            fillPath.lineTo(x, y)
        } else {
            val prevX = (i - 1) * dx
            val prevMag = (frequencies[i - 1] * intensity).coerceIn(0.05f, 1.0f)
            val prevWave = sin(phase + ((i - 1).toFloat() / count) * PI * 2).toFloat() * 15.dp.toPx()
            val prevY = centerY - (prevMag * (totalHeight * 0.4f) * sin(((i - 1).toFloat() / count) * PI).toFloat()) + prevWave
            
            val controlX = (prevX + x) / 2f
            path.quadraticBezierTo(controlX, prevY, x, y)
            fillPath.quadraticBezierTo(controlX, prevY, x, y)
        }
    }

    fillPath.lineTo(totalWidth, totalHeight)
    fillPath.close()

    // Draw smooth gradient fill under curve
    if (settings.fillGradient) {
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primary.copy(alpha = 0.25f), Color.Transparent),
                startY = centerY - 40.dp.toPx(),
                endY = totalHeight
            )
        )
    }

    // Glow line (wide stroke, lower opacity)
    if (settings.showGlow) {
        drawPath(
            path = path,
            color = primary.copy(alpha = 0.35f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }

    // Crisp neon line
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(listOf(primary, secondary)),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawCircularBurst(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    accent: Color,
    rotationPhase: Float,
    intensity: Float
) {
    val count = min(frequencies.size, settings.barCount)
    if (count <= 0) return

    val center = Offset(size.width / 2f, size.height / 2f)
    val baseRadius = min(size.width, size.height) * 0.28f
    val maxSpikeLength = min(size.width, size.height) * 0.22f
    val angleStep = (2 * PI / count).toFloat()

    // Inner glowing core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(primary.copy(alpha = 0.2f * intensity), Color.Transparent),
            center = center,
            radius = baseRadius * 1.2f
        ),
        radius = baseRadius * 1.2f,
        center = center
    )

    // Inner ring
    drawCircle(
        color = secondary.copy(alpha = 0.4f),
        radius = baseRadius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    for (i in 0 until count) {
        val mag = (frequencies[i] * intensity).coerceIn(0.04f, 1.0f)
        val angle = (i * angleStep) + rotationPhase
        val spikeLength = maxSpikeLength * mag

        val startX = center.x + cos(angle) * baseRadius
        val startY = center.y + sin(angle) * baseRadius

        val endX = center.x + cos(angle) * (baseRadius + spikeLength)
        val endY = center.y + sin(angle) * (baseRadius + spikeLength)

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(primary, secondary),
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        if (settings.showPeakDots && mag > 0.35f) {
            val peakX = center.x + cos(angle) * (baseRadius + spikeLength + 5.dp.toPx())
            val peakY = center.y + sin(angle) * (baseRadius + spikeLength + 5.dp.toPx())
            drawCircle(
                color = accent,
                radius = 2.5.dp.toPx(),
                center = Offset(peakX, peakY)
            )
        }
    }
}

private fun DrawScope.drawParticleField(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    accent: Color,
    phase: Float,
    intensity: Float
) {
    val count = min(frequencies.size, settings.barCount)
    if (count <= 0) return

    val totalWidth = size.width
    val totalHeight = size.height
    val centerY = totalHeight / 2f

    for (i in 0 until count) {
        val mag = (frequencies[i] * intensity).coerceIn(0.05f, 1.0f)
        val normalizedIndex = i.toFloat() / count.toFloat()
        
        val x = (normalizedIndex * totalWidth) + (sin(phase + i) * 10.dp.toPx())
        val floatOffset = sin(phase * 2f + (i * 0.7f)) * (totalHeight * 0.35f * mag)
        val y = centerY + floatOffset
        val particleRadius = (3.dp.toPx() + (mag * 6.dp.toPx())).coerceIn(2.dp.toPx(), 10.dp.toPx())

        // Particle glow
        if (settings.showGlow) {
            drawCircle(
                color = primary.copy(alpha = 0.25f * mag),
                radius = particleRadius * 2.2f,
                center = Offset(x, y)
            )
        }

        val particleColor = if (i % 3 == 0) accent else if (i % 2 == 0) primary else secondary
        drawCircle(
            color = particleColor.copy(alpha = (0.5f + mag * 0.5f).coerceIn(0.2f, 1f)),
            radius = particleRadius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawRetroVuMeter(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    intensity: Float
) {
    val count = min(frequencies.size, 16) // 16 discrete channels
    if (count <= 0) return

    val totalWidth = size.width
    val totalHeight = size.height
    val numSegments = 12
    val segmentSpacing = 3.dp.toPx()
    val channelSpacing = 4.dp.toPx()
    val channelWidth = (totalWidth - (channelSpacing * (count - 1))) / count
    val segmentHeight = (totalHeight - (segmentSpacing * (numSegments - 1))) / numSegments

    for (ch in 0 until count) {
        val mag = (frequencies[ch] * intensity).coerceIn(0.0f, 1.0f)
        val activeSegments = (mag * numSegments).toInt().coerceIn(1, numSegments)
        val left = ch * (channelWidth + channelSpacing)

        for (seg in 0 until numSegments) {
            val isLit = (numSegments - 1 - seg) < activeSegments
            val top = seg * (segmentHeight + segmentSpacing)

            val segColor = when {
                seg < 2 -> Color(0xFFFF3366) // Overload / Peak Red
                seg < 5 -> Color(0xFFFFB300) // Caution Gold
                else -> primary              // Normal Green / Accent
            }

            drawRoundRect(
                color = if (isLit) segColor else segColor.copy(alpha = 0.12f),
                topLeft = Offset(left, top),
                size = Size(channelWidth, segmentHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawHolographicRibbon(
    frequencies: FloatArray,
    settings: VisualizerSettings,
    primary: Color,
    secondary: Color,
    accent: Color,
    phase: Float,
    intensity: Float
) {
    val count = min(frequencies.size, settings.barCount)
    if (count < 3) return

    val totalWidth = size.width
    val totalHeight = size.height
    val centerY = totalHeight / 2f
    val dx = totalWidth / (count - 1).toFloat()

    // 3 interwoven ribbons at different harmonic phases
    val ribbonColors = listOf(primary, secondary, accent)
    for (r in 0 until 3) {
        val ribbonPhase = phase + (r * PI.toFloat() * 0.6f)
        val path = Path()

        for (i in 0 until count) {
            val mag = (frequencies[i] * intensity).coerceIn(0.04f, 1.0f)
            val harmonicY = sin(ribbonPhase + (i.toFloat() / count) * PI * 3).toFloat() * (totalHeight * 0.35f * mag)
            val x = i * dx
            val y = centerY + harmonicY

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = ribbonColors[r].copy(alpha = 0.75f),
            style = Stroke(width = (4 - r).dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
