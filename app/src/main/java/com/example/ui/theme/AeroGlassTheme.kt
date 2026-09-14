package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Professional Surface Container
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = AppSurface,
    borderColor: Color = AppBorder,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = AppPrimary.copy(alpha = 0.15f)),
            onClick = onClick
        )
    } else Modifier

    val activeBorder = if (isPressed) AppPrimary else borderColor
    val activeBg = if (isPressed) AppSurfaceElevated else backgroundColor

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = AppPrimary.copy(alpha = if (isPressed) 0.25f else 0.08f)
            )
            .clip(shape)
            .border(
                border = BorderStroke(width = 1.dp, color = activeBorder),
                shape = shape
            )
            .background(activeBg)
            .then(clickableModifier)
            .padding(contentPadding)
    ) {
        content()
    }
}

// Professional Primary Action Button
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    primaryColor: Color = AppPrimary,
    contentPadding: Dp = 10.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = shape,
                spotColor = primaryColor.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isPressed) {
                        listOf(AppPrimaryDark, primaryColor)
                    } else {
                        listOf(primaryColor, AppPrimaryDark)
                    }
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// Clean Screen Background
@Composable
fun AppScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .drawBehind {
                // Subtle Ambient Gradient Spot at top corner for visual depth
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppPrimary.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.12f),
                        radius = size.width * 0.7f
                    )
                )
            }
    ) {
        content()
    }
}

// Clean Header Bar
@Composable
fun AppHeaderBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onOrbClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppPrimary.copy(alpha = 0.15f))
                    .border(1.dp, AppPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable(enabled = onOrbClick != null) { onOrbClick?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "App Logo",
                    tint = AppPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            actions()
        }
    }
}

// Legacy Aliases for Component Calls
@Composable
fun AeroGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = AppSurface,
    borderColor: Color = AppBorder,
    contentPadding: Dp = 16.dp,
    hasGlossHighlight: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        contentPadding = contentPadding,
        onClick = onClick,
        content = content
    )
}

@Composable
fun AeroGelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    topColor: Color = AppPrimary,
    bottomColor: Color = AppPrimaryDark,
    glowColor: Color = AppPrimary,
    contentPadding: Dp = 10.dp,
    content: @Composable BoxScope.() -> Unit
) {
    AppButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        primaryColor = topColor,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun AeroWallpaperBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    AppScreenBackground(
        modifier = modifier,
        content = content
    )
}

@Composable
fun AeroWindowHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onOrbClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    AppHeaderBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onOrbClick = onOrbClick,
        actions = actions
    )
}
