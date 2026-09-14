package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TrackEntity
import com.example.data.model.DownloadStatus
import com.example.data.model.TrackDownloadState
import com.example.player.PlayerUiState
import com.example.ui.components.TrackItemRow
import com.example.ui.theme.AeroCyanGlow
import com.example.ui.theme.AeroGelButton
import com.example.ui.theme.AeroGlassCard
import com.example.ui.theme.AeroIceWhite
import com.example.ui.theme.AeroSkyBlue
import com.example.ui.theme.AeroWallpaperBackground
import com.example.ui.theme.AeroWindowHeader
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    activeFilter: String,
    onFilterSelect: (String) -> Unit,
    results: List<TrackEntity>,
    playerState: PlayerUiState,
    downloadStates: Map<String, TrackDownloadState> = emptyMap(),
    onTrackClick: (TrackEntity) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onRetryDownload: ((TrackEntity) -> Unit)? = null,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    onSwitchPlatformCounterpart: ((TrackEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val filterOptions = listOf(
        "ALL" to "All Catalog",
        "SPOTIFY" to "Spotify 🟢",
        "YOUTUBE" to "YouTube 🔴",
        "DOWNLOADED" to "Offline 📥"
    )

    val trendingKeywords = listOf(
        "The Weeknd",
        "Billie Eilish",
        "Dua Lipa",
        "Queen Live",
        "Lo-Fi Beats",
        "Acoustic Live",
        "Synthwave 80s",
        "Tokyo Drift"
    )

    AeroWallpaperBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("search_screen")
        ) {
            // Header
            AeroWindowHeader(
                title = "Search & Explore",
                subtitle = "Unified Spotify & YouTube Music Catalog"
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // Aero Glass Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            "Search songs, artists, or genres...",
                            color = AeroIceWhite.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AeroCyanGlow
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = AeroIceWhite
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x4D0B2E5C),
                        unfocusedContainerColor = Color(0x33081F3E),
                        focusedBorderColor = AeroCyanGlow,
                        unfocusedBorderColor = Color(0x5590CAF9),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unified_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Glass Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { (key, label) ->
                        val isSelected = activeFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterSelect(key) },
                            label = {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else AeroIceWhite.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (key) {
                                    "SPOTIFY" -> SpotifyGreen.copy(alpha = 0.8f)
                                    "YOUTUBE" -> YouTubeRed.copy(alpha = 0.8f)
                                    else -> Color(0x6600E5FF)
                                },
                                containerColor = Color(0x2BFFFFFF)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0x44FFFFFF),
                                selectedBorderColor = AeroCyanGlow
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Quick Trending Suggestions
                if (searchQuery.isEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Trending",
                            tint = AeroCyanGlow,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Popular on Spotify & YouTube",
                            color = AeroIceWhite.copy(alpha = 0.85f),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(trendingKeywords) { keyword ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x38FFFFFF))
                                    .clickable { onQueryChange(keyword) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = keyword,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Results List
            if (results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💎", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Search millions of tracks with zero wait time" else "No matching tracks found for \"$searchQuery\"",
                            color = AeroIceWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${results.size} tracks found",
                        color = com.example.ui.theme.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    AeroGelButton(
                        onClick = { results.firstOrNull()?.let { onTrackClick(it) } },
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play all results",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(results) { track ->
                        val isCurrent = playerState.currentTrack?.id == track.id
                        val dState = downloadStates[track.id] ?: if (track.isDownloaded) TrackDownloadState(DownloadStatus.DOWNLOADED) else TrackDownloadState(DownloadStatus.NOT_DOWNLOADED)
                        TrackItemRow(
                            track = track,
                            isPlaying = isCurrent && playerState.isPlaying,
                            isCurrentTrack = isCurrent,
                            downloadState = dState,
                            onTrackClick = { onTrackClick(track) },
                            onLikeClick = { onLikeClick(track) },
                            onDownloadClick = { onDownloadClick(track) },
                            onRetryDownload = { onRetryDownload?.invoke(track) },
                            onAddToPlaylistClick = { onAddToPlaylistClick(track) },
                            onShareClick = { onShareClick(track) },
                            onSwitchPlatformCounterpart = onSwitchPlatformCounterpart,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
    }
}
