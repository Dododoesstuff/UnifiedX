package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BabyBlue
import com.example.ui.theme.BabyBlueAccent
import com.example.ui.theme.BabyBlueLight
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppSplashScreen(
    onAnimationFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    // Animation values
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }
    val textAlpha = remember { Animatable(0f) }
    val textTranslateY = remember { Animatable(30f) }
    val badgeAlpha = remember { Animatable(0f) }
    val haloExpansion = remember { Animatable(0.6f) }

    // Pulsing and orbital particles transition
    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val orbitalAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbital_rotation"
    )

    LaunchedEffect(Unit) {
        // Step 1: Smooth spring pop-in of logo
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = 220f
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 180f
                )
            )
        }
        launch {
            haloExpansion.animateTo(
                targetValue = 1.25f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }

        // Step 2: Stagger in Title & Subtitle
        delay(400)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(450, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textTranslateY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 250f
                )
            )
        }

        // Step 3: Reveal platform badges (Spotify & YouTube)
        delay(250)
        badgeAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )

        // Hold display momentarily for full visual experience
        delay(950)

        // Step 4: Fade out splash
        isVisible = false
        delay(350)
        onAnimationFinish()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(350)) + scaleOut(targetScale = 1.08f, animationSpec = tween(350)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianDeep)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // Tap to skip
                    isVisible = false
                    onAnimationFinish()
                }
                .testTag("app_splash_screen"),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Radial Gradient Aura in Baby Blue & Deep Obsidian
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f - 40f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BabyBlueAccent.copy(alpha = 0.28f * alpha.value),
                            BabyBlue.copy(alpha = 0.12f * alpha.value),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = size.minDimension * 0.7f * pulseGlow
                    ),
                    center = centerOffset,
                    radius = size.minDimension * 0.7f * pulseGlow
                )
            }

            // Orbital Particles & Sound Wave Rings
            Canvas(
                modifier = Modifier
                    .size(240.dp)
                    .scale(haloExpansion.value)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.minDimension / 2f - 15f

                // Outer rotating gradient track
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            SpotifyGreen.copy(alpha = 0.6f * alpha.value),
                            BabyBlue.copy(alpha = 0.8f * alpha.value),
                            YouTubeRed.copy(alpha = 0.6f * alpha.value),
                            BabyBlueLight.copy(alpha = 0.8f * alpha.value),
                            SpotifyGreen.copy(alpha = 0.6f * alpha.value)
                        )
                    ),
                    center = center,
                    radius = baseRadius * pulseGlow,
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round
                    )
                )

                // Orbiting Spotify Green Particle
                val spotAngleRad = Math.toRadians(orbitalAngle.toDouble())
                val spotX = center.x + (baseRadius * pulseGlow * cos(spotAngleRad)).toFloat()
                val spotY = center.y + (baseRadius * pulseGlow * sin(spotAngleRad)).toFloat()
                drawCircle(
                    color = SpotifyGreen.copy(alpha = alpha.value),
                    radius = 4.5f,
                    center = Offset(spotX, spotY)
                )

                // Orbiting YouTube Red Particle (180 deg offset)
                val ytAngleRad = Math.toRadians((orbitalAngle + 180).toDouble())
                val ytX = center.x + (baseRadius * pulseGlow * cos(ytAngleRad)).toFloat()
                val ytY = center.y + (baseRadius * pulseGlow * sin(ytAngleRad)).toFloat()
                drawCircle(
                    color = YouTubeRed.copy(alpha = alpha.value),
                    radius = 4.5f,
                    center = Offset(ytX, ytY)
                )

                // Orbiting Baby Blue Glow particle (90 deg offset)
                val bbAngleRad = Math.toRadians((orbitalAngle + 90).toDouble())
                val bbX = center.x + (baseRadius * 0.85f * cos(bbAngleRad)).toFloat()
                val bbY = center.y + (baseRadius * 0.85f * sin(bbAngleRad)).toFloat()
                drawCircle(
                    color = BabyBlueLight.copy(alpha = 0.9f * alpha.value),
                    radius = 3.5f,
                    center = Offset(bbX, bbY)
                )
            }

            // Main Logo & Brand Typography Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Logo with scale, rotation, and soft glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(scale.value)
                        .rotate(rotation.value)
                        .alpha(alpha.value),
                    contentAlignment = Alignment.Center
                ) {
                    // Soft background glow
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(BabyBlueAccent.copy(alpha = 0.45f), Color.Transparent)
                                )
                            )
                    )

                    // Actual Logo Graphic
                    Image(
                        painter = painterResource(id = R.drawable.unifiedx_logo_modern),
                        contentDescription = "UnifiedX Logo",
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Name "UnifiedX" with gradient and fade
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .alpha(textAlpha.value)
                        .graphicsLayer {
                            translationY = textTranslateY.value
                        }
                ) {
                    Text(
                        text = "UnifiedX",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Spotify & YouTube In Harmony",
                        color = BabyBlue,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Platform badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(badgeAlpha.value)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SpotifyGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SpotifyGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Spotify",
                                color = SpotifyGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "✕",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(YouTubeRed.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(YouTubeRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YouTube",
                                color = YouTubeRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Bottom copyright / studio label
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
                    .alpha(textAlpha.value)
            ) {
                Text(
                    text = "Seamless Cross-Platform Audio",
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
