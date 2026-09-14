package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import com.example.player.EqPreset
import com.example.player.PlayerUiState
import com.example.player.RepeatMode
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun FullScreenPlayer(
    playerState: PlayerUiState,
    onCollapse: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onLikeClick: () -> Unit,
    onOpenLyrics: () -> Unit,
    onCycleEqPreset: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playerState.currentTrack ?: return
    val coroutineScope = rememberCoroutineScope()

    var isUserSeeking by remember { mutableStateOf(false) }
    var seekSliderPosition by remember { mutableStateOf(0f) }

    // Swipe down to minimize state
    val dragOffsetY = remember { Animatable(0f) }
    // Swipe left/right for next/previous track state
    val dragOffsetX = remember { Animatable(0f) }

    // Reset horizontal drag offset when current track changes
    LaunchedEffect(track.id) {
        dragOffsetX.snapTo(0f)
    }

    val currentSliderVal = if (isUserSeeking) {
        seekSliderPosition
    } else {
        if (playerState.durationMs > 0) {
            (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    val sourceColor = if (track.platformSource == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed

    val artScale by animateFloatAsState(
        targetValue = if (playerState.isPlaying) 1.0f else 0.92f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "artScale"
    )

    // Calculate background alpha based on vertical drag
    val backdropAlpha = (1f - (dragOffsetY.value / 600f)).coerceIn(0.4f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, dragOffsetY.value.roundToInt()) }
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0E2238).copy(alpha = backdropAlpha),
                        ObsidianDeep.copy(alpha = backdropAlpha),
                        ObsidianDeep.copy(alpha = backdropAlpha)
                    )
                )
            )
            .pointerInput(Unit) {
                // Vertical drag gesture on the screen to minimize/collapse player
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (dragOffsetY.value > 150f) {
                                // Threshold met: minimize player
                                dragOffsetY.animateTo(1000f, animationSpec = tween(200))
                                onCollapse()
                                dragOffsetY.snapTo(0f)
                            } else {
                                // Snap back to top
                                dragOffsetY.animateTo(0f, animationSpec = spring())
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            dragOffsetY.animateTo(0f, animationSpec = spring())
                        }
                    },
                    onDrag = { change, dragAmount ->
                        // Only allow dragging downwards (positive Y)
                        if (dragOffsetY.value + dragAmount.y >= 0f) {
                            change.consume()
                            coroutineScope.launch {
                                dragOffsetY.snapTo(dragOffsetY.value + dragAmount.y * 0.85f)
                            }
                        }
                    }
                )
            }
            .testTag("full_screen_player")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Pull-down handle bar indicator for swipe gesture affordance
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .clickable { onCollapse() }
                    .testTag("drag_handle_indicator")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Top bar with collapse chevron and Source pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.testTag("collapse_player_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Cross-Platform Source Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(sourceColor.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(sourceColor)
                    )
                    Text(
                        text = "Source: ${track.platformSource.displayName}",
                        color = sourceColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share button
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.testTag("full_player_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Album Artwork with interactive horizontal swipe gesture (left for next, right for previous)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                    .scale(artScale)
                    .alpha((1f - (abs(dragOffsetX.value) / 400f)).coerceIn(0.5f, 1f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(ObsidianCard)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val currentX = dragOffsetX.value
                                    if (currentX < -80f) {
                                        // Swiped Left -> Next Track
                                        dragOffsetX.animateTo(-300f, animationSpec = tween(150))
                                        onNext()
                                        dragOffsetX.snapTo(300f)
                                        dragOffsetX.animateTo(0f, animationSpec = spring())
                                    } else if (currentX > 80f) {
                                        // Swiped Right -> Previous Track
                                        dragOffsetX.animateTo(300f, animationSpec = tween(150))
                                        onPrevious()
                                        dragOffsetX.snapTo(-300f)
                                        dragOffsetX.animateTo(0f, animationSpec = spring())
                                    } else {
                                        // Reset to center
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
                                    dragOffsetX.snapTo(dragOffsetX.value + dragAmount * 0.95f)
                                }
                            }
                        )
                    }
                    .testTag("full_player_artwork"),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = "${track.title} artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            // Subtle Swipe Navigation Hint
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀ Swipe track ▶",
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Quality & Offline Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.audioQuality.badge,
                        color = HiResGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(HiResGold.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    if (track.isDownloaded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SpotifyGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = null,
                                tint = SpotifyGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Offline Cached",
                                color = SpotifyGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Cross-Platform Equalizer Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onCycleEqPreset() }
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "EQ Preset",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = playerState.activeEqPreset.label,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Track Title, Artist, and Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = track.album,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("full_player_like_button")
                ) {
                    Icon(
                        imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (track.isLiked) "Unlike" else "Like",
                        tint = if (track.isLiked) YouTubeRed else TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Seek Slider
            Slider(
                value = currentSliderVal,
                onValueChange = {
                    isUserSeeking = true
                    seekSliderPosition = it
                },
                onValueChangeFinished = {
                    isUserSeeking = false
                    val targetMs = (seekSliderPosition * playerState.durationMs).toLong()
                    onSeek(targetMs)
                },
                colors = SliderDefaults.colors(
                    thumbColor = sourceColor,
                    activeTrackColor = sourceColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("full_player_seek_slider")
            )

            // Timestamps
            val currentPos = if (isUserSeeking) {
                (seekSliderPosition * playerState.durationMs).toLong()
            } else {
                playerState.currentPositionMs
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(currentPos),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = formatMs(playerState.durationMs),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Playback Controls Row (Shuffle, Prev, Play/Pause, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playerState.isShuffle) sourceColor else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp).testTag("previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = TextPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Big Play / Pause Circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(sourceColor)
                        .clickable(onClick = onPlayPause)
                        .testTag("full_player_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                        tint = if (track.platformSource == PlatformSource.SPOTIFY) Color.Black else Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp).testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = TextPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.testTag("repeat_button")
                ) {
                    Icon(
                        imageVector = if (playerState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) sourceColor else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Bar: Lyrics Sheet Toggle & Add to Playlist
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Synced Lyrics Action
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenLyrics)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("open_lyrics_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lyrics,
                        contentDescription = "Open Synced Lyrics",
                        tint = sourceColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Synced Lyrics",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Add to Unified Playlist
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onAddToPlaylistClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_to_playlist_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = "Add to Unified Playlist",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to Playlist",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSecs = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSecs / 60
    val seconds = totalSecs % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
