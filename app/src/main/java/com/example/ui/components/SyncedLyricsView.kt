package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.LyricsLine
import com.example.data.model.PlatformSource
import com.example.player.PlayerUiState
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SyncedLyricsView(
    playerState: PlayerUiState,
    frequencies: FloatArray = FloatArray(0),
    visualizerSettings: com.example.visualizer.VisualizerSettings = com.example.visualizer.VisualizerSettings(),
    onClose: () -> Unit,
    onSeekToTimestamp: (Long) -> Unit,
    onShareSnippet: (String) -> Unit,
    onReloadLyrics: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val track = playerState.currentTrack ?: return
    val lyrics = playerState.parsedLyrics
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dragOffsetY = remember { Animatable(0f) }

    // Smooth auto-scroll keeping the active lyric line centered
    LaunchedEffect(playerState.activeLyricIndex) {
        if (playerState.activeLyricIndex in lyrics.indices) {
            val targetScroll = (playerState.activeLyricIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    val isSpotify = track.platformSource == PlatformSource.SPOTIFY
    val platformAccent = if (isSpotify) SpotifyGreen else YouTubeRed

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, dragOffsetY.value.roundToInt()) }
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B192C),
                        Color(0xFF0F2642),
                        ObsidianDeep
                    )
                )
            )
            .testTag("synced_lyrics_view")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Drag Down Bar & Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (dragOffsetY.value > 120f) {
                                        dragOffsetY.animateTo(800f, animationSpec = tween(150))
                                        onClose()
                                        dragOffsetY.snapTo(0f)
                                    } else {
                                        dragOffsetY.animateTo(0f, animationSpec = spring())
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    dragOffsetY.animateTo(0f, animationSpec = spring())
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                if (dragOffsetY.value + dragAmount >= 0f) {
                                    change.consume()
                                    coroutineScope.launch {
                                        dragOffsetY.snapTo(dragOffsetY.value + dragAmount * 0.85f)
                                    }
                                }
                            }
                        )
                    }
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                // Pull down handle bar
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_lyrics_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to player",
                            tint = TextPrimary
                        )
                    }

                // Thumbnail & Track info
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Refresh / Fetch from API Button
                IconButton(
                    onClick = onReloadLyrics,
                    modifier = Modifier.testTag("reload_lyrics_api_button")
                ) {
                    if (playerState.isLyricsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = CrossPurple,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Fetch lyrics from API",
                            tint = TextSecondary
                        )
                    }
                }

                // Share active line snippet
                IconButton(
                    onClick = {
                        val activeLine = lyrics.getOrNull(playerState.activeLyricIndex)?.text ?: track.title
                        onShareSnippet(activeLine)
                    },
                    modifier = Modifier.testTag("share_lyrics_snippet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share lyrics snippet",
                        tint = CrossPurple
                    )
                }
            }
        }

            // Status Bar Pill (API Source & Sync indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // API Source badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (playerState.isLyricsSynced) Icons.Default.CloudSync else Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = if (playerState.isLyricsLoading) ElectricViolet else SpotifyGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (playerState.isLyricsLoading) "Fetching from API..." else playerState.lyricsSource,
                        color = if (playerState.isLyricsLoading) ElectricViolet else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Interactive Hint
                Text(
                    text = "Tap line to jump",
                    color = TextSecondary.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            // Real-Time Waveform Visualizer in Lyrics Mode
            if (visualizerSettings.isEnabled && frequencies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.visualizer.WaveformVisualizer(
                        frequencies = frequencies,
                        settings = visualizerSettings,
                        isPlaying = playerState.isPlaying,
                        platformSource = track.platformSource,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Lyrics Scroll Area
            if (playerState.isLyricsLoading && lyrics.isEmpty()) {
                // Loading Skeleton View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = CrossPurple,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Fetching time-synced lyrics from API...",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Matching \"${track.title}\" by ${track.artist}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else if (lyrics.isEmpty()) {
                // Empty state with manual fetch retry
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎶", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Synchronized Lyrics Found",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "This track might be an instrumental or not yet indexed on the synced lyrics database.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onReloadLyrics,
                            colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("retry_lyrics_fetch_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Fetch from API", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // Live Synced Lyrics List with Active Line Highlighting
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(top = 20.dp, bottom = 140.dp, start = 20.dp, end = 20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    itemsIndexed(lyrics) { index, line ->
                        val isActive = index == playerState.activeLyricIndex
                        val isPast = index < playerState.activeLyricIndex

                        val textColor by animateColorAsState(
                            targetValue = when {
                                isActive -> Color.White
                                isPast -> TextSecondary.copy(alpha = 0.55f)
                                else -> TextTertiary.copy(alpha = 0.35f)
                            },
                            animationSpec = tween(durationMillis = 280),
                            label = "lyricTextColor"
                        )

                        val backgroundColor by animateColorAsState(
                            targetValue = if (isActive) CrossPurple.copy(alpha = 0.22f) else Color.Transparent,
                            animationSpec = tween(durationMillis = 280),
                            label = "lyricBgColor"
                        )

                        val borderColor by animateColorAsState(
                            targetValue = if (isActive) CrossPurple.copy(alpha = 0.45f) else Color.Transparent,
                            animationSpec = tween(durationMillis = 280),
                            label = "lyricBorderColor"
                        )

                        val textSize = if (isActive) 23.sp else 18.sp
                        val fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold
                        val formattedTime = formatTimestamp(line.timestampMs)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(backgroundColor)
                                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                .clickable { onSeekToTimestamp(line.timestampMs) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("lyric_line_$index")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Live equalizer visualizer for the active singing line
                                if (isActive) {
                                    ActiveLineVisualizer(color = platformAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = line.text,
                                        color = textColor,
                                        fontSize = textSize,
                                        fontWeight = fontWeight,
                                        lineHeight = if (isActive) 32.sp else 26.sp
                                    )
                                    if (isActive) {
                                        Text(
                                            text = formattedTime,
                                            color = platformAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                if (isActive) {
                                    IconButton(
                                        onClick = { onShareSnippet(line.text) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share verse",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Mini-Dock with Playback Controls
            LyricsBottomPlaybackDock(
                playerState = playerState,
                onPlayPause = onPlayPause,
                onSeek = onSeekToTimestamp,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious
            )
        }
    }
}

@Composable
private fun ActiveLineVisualizer(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "eqAnim")
    val height1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height1.dp)
                .clip(CircleShape)
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height2.dp)
                .clip(CircleShape)
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height3.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun LyricsBottomPlaybackDock(
    playerState: PlayerUiState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit
) {
    var isUserScrubbing by remember { mutableStateOf(false) }
    var scrubPosition by remember { mutableStateOf(0f) }

    val currentMs = if (isUserScrubbing) scrubPosition.toLong() else playerState.currentPositionMs
    val durationMs = playerState.durationMs.coerceAtLeast(1L)
    val progressFraction = (currentMs.toFloat() / durationMs).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianSurface.copy(alpha = 0.95f))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Scrubber Slider
        Slider(
            value = progressFraction,
            onValueChange = { frac ->
                isUserScrubbing = true
                scrubPosition = frac * durationMs
            },
            onValueChangeFinished = {
                onSeek(scrubPosition.toLong())
                isUserScrubbing = false
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = CrossPurple,
                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .testTag("lyrics_playback_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTimestamp(currentMs),
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = formatTimestamp(durationMs),
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSkipPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CrossPurple)
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onSkipNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
