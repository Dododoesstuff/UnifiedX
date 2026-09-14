package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PlatformSource
import com.example.player.PlayerUiState
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppPrimary
import com.example.ui.theme.AppSurface
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun MiniPlayer(
    playerState: PlayerUiState,
    onExpandClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit = {},
    onSeek: (Long) -> Unit = {},
    onLikeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val track = playerState.currentTrack ?: return
    val coroutineScope = rememberCoroutineScope()
    val dragOffsetX = remember { Animatable(0f) }

    LaunchedEffect(track.id) {
        dragOffsetX.snapTo(0f)
    }

    val duration = playerState.durationMs.coerceAtLeast(1L)
    val currentPos = playerState.currentPositionMs.coerceIn(0L, duration)
    val progress = (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

    val isSpotify = track.platformSource == PlatformSource.SPOTIFY
    val platformAccent = if (isSpotify) SpotifyGreen else YouTubeRed

    var isUserScrubbing by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }

    val activeProgress = if (isUserScrubbing) scrubProgress else progress

    val cardShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = AppPrimary.copy(alpha = 0.15f)
            )
            .clip(cardShape)
            .border(
                border = BorderStroke(width = 1.dp, color = AppBorder),
                shape = cardShape
            )
            .background(AppSurface)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            val currentX = dragOffsetX.value
                            if (currentX < -60f) {
                                dragOffsetX.animateTo(-200f, animationSpec = tween(120))
                                onSkipNextClick()
                                dragOffsetX.snapTo(200f)
                                dragOffsetX.animateTo(0f, animationSpec = spring())
                            } else if (currentX > 60f) {
                                dragOffsetX.animateTo(200f, animationSpec = tween(120))
                                onSkipPreviousClick()
                                dragOffsetX.snapTo(-200f)
                                dragOffsetX.animateTo(0f, animationSpec = spring())
                            } else {
                                dragOffsetX.animateTo(0f, animationSpec = spring())
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            dragOffsetX.animateTo(0f, animationSpec = spring())
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            dragOffsetX.snapTo(dragOffsetX.value + dragAmount * 0.9f)
                        }
                    }
                )
            }
            .clickable(onClick = onExpandClick)
            .testTag("persistent_bottom_playback_bar")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Scrubbing Progress Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(AppBorder)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val seekRatio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                            onSeek((seekRatio * duration).toLong())
                        }
                    }
                    .testTag("playback_progress_bar")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(activeProgress)
                        .height(3.dp)
                        .background(AppPrimary)
                )
            }

            // Track Details & Player Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Artwork Thumbnail
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, AppBorder, RoundedCornerShape(8.dp))
                        .background(AppBorder)
                ) {
                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = "Now playing artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Platform Source Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(platformAccent)
                            .border(1.dp, Color.Black, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Track Metadata
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (playerState.isCrossfading) {
                            "${track.artist} • Crossfading ✦"
                        } else {
                            "${track.artist} • ${formatMs(currentPos)}"
                        },
                        color = if (playerState.isCrossfading) AppPrimary else TextSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = if (playerState.isCrossfading) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("mini_player_like")
                    ) {
                        Icon(
                            imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (track.isLiked) "Unlike" else "Like",
                            tint = if (track.isLiked) AppPrimary else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipPreviousClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("mini_player_skip_previous")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous track",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppPrimary)
                            .clickable(onClick = onPlayPauseClick)
                            .testTag("mini_player_play_pause"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipNextClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("mini_player_skip_next")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next track",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
