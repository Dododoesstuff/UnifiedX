package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import com.example.player.PlayerUiState
import com.example.sync.SyncState
import com.example.ui.components.TrackItemRow
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.ArtistSpotlight

@Composable
fun HomeScreen(
    tracks: List<TrackEntity>,
    playerState: PlayerUiState,
    syncState: SyncState,
    discoveryArtists: List<ArtistSpotlight>,
    onTrackClick: (TrackEntity) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    onSyncNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val isSearching = searchQuery.isNotBlank()

    // Unified filtered list based on query and active filter pill
    val displayTracks = remember(tracks, searchQuery, selectedFilter) {
        val base = if (isSearching) {
            val q = searchQuery.trim()
            tracks.filter { track ->
                track.title.contains(q, ignoreCase = true) ||
                track.artist.contains(q, ignoreCase = true) ||
                track.album.contains(q, ignoreCase = true)
            }
        } else {
            tracks
        }

        when (selectedFilter) {
            "SPOTIFY" -> base.filter { it.platformSource == PlatformSource.SPOTIFY }
            "YOUTUBE" -> base.filter { it.platformSource == PlatformSource.YOUTUBE }
            "LOSSLESS" -> base.filter { it.audioQuality == AudioQuality.LOSSLESS }
            "DOWNLOADED" -> base.filter { it.isDownloaded }
            else -> base
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // App Header: Clean Branding & Sync Indicator
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.unifiedx_logo_modern),
                        contentDescription = "UnifiedX Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "UnifiedX",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Spotify & YouTube Unified",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                // Minimalist Sync Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CrossPurple.copy(alpha = 0.12f))
                        .clickable(onClick = onSyncNowClick)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("home_sync_badge"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = CrossPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (syncState.isSyncing) "Syncing" else "Synced",
                        color = CrossPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Clean Search Bar
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search tracks, artists, or albums...",
                        color = TextSecondary,
                        fontSize = 13.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearching) CrossPurple else TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                },
                trailingIcon = {
                    if (isSearching) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.testTag("home_search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = CrossPurple.copy(alpha = 0.6f),
                    unfocusedBorderColor = ObsidianStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("home_top_search_bar")
            )
        }

        // Clean Single Row of Source & Library Filter Chips
        item {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    "ALL" to "All",
                    "SPOTIFY" to "Spotify",
                    "YOUTUBE" to "YouTube",
                    "LOSSLESS" to "Hi-Res Lossless",
                    "DOWNLOADED" to "Downloaded"
                )

                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    val activeColor = when (key) {
                        "SPOTIFY" -> SpotifyGreen
                        "YOUTUBE" -> YouTubeRed
                        else -> CrossPurple
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = activeColor,
                            selectedLabelColor = if (key == "SPOTIFY") Color.Black else Color.White,
                            containerColor = ObsidianCard,
                            labelColor = TextSecondary
                        ),
                        border = null,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }

        // Active Search Results Mode vs Default Feed Mode
        if (isSearching) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Search Results",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${displayTracks.size} songs found",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (displayTracks.isNotEmpty()) {
                        IconButton(
                            onClick = { displayTracks.firstOrNull()?.let { onTrackClick(it) } },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CrossPurple)
                                .testTag("play_search_results_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Results",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (displayTracks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(displayTracks) { track ->
                    val isCurrent = playerState.currentTrack?.id == track.id
                    TrackItemRow(
                        track = track,
                        isPlaying = isCurrent && playerState.isPlaying,
                        isCurrentTrack = isCurrent,
                        onTrackClick = { onTrackClick(track) },
                        onLikeClick = { onLikeClick(track) },
                        onDownloadClick = { onDownloadClick(track) },
                        onAddToPlaylistClick = { onAddToPlaylistClick(track) },
                        onShareClick = { onShareClick(track) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        } else {
            // Refined Hero Quick-Mix Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("home_hero_banner"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF201335),
                                        Color(0xFF111726)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ElectricViolet,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UNIFIED FUSION",
                                        color = ElectricViolet,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Cross-Platform Mix",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Spotify + YouTube unified queue",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Quick Play Action
                            IconButton(
                                onClick = { tracks.firstOrNull()?.let { onTrackClick(it) } },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CrossPurple)
                                    .testTag("hero_quick_play_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Fusion",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Featured Artists Spotlight
            if (discoveryArtists.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(22.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Artists",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(discoveryArtists) { artist ->
                                ArtistSpotlightCard(artist = artist)
                            }
                        }
                    }
                }
            }

            // Music Library List Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tracks",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${displayTracks.size} songs",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Play All & Shuffle Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                val shuffled = displayTracks.shuffled()
                                shuffled.firstOrNull()?.let { onTrackClick(it) }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ObsidianCard)
                                .testTag("library_shuffle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                displayTracks.firstOrNull()?.let { onTrackClick(it) }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CrossPurple)
                                .testTag("library_play_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play All",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (displayTracks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs in this view",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(displayTracks) { track ->
                    val isCurrent = playerState.currentTrack?.id == track.id
                    TrackItemRow(
                        track = track,
                        isPlaying = isCurrent && playerState.isPlaying,
                        isCurrentTrack = isCurrent,
                        onTrackClick = { onTrackClick(track) },
                        onLikeClick = { onLikeClick(track) },
                        onDownloadClick = { onDownloadClick(track) },
                        onAddToPlaylistClick = { onAddToPlaylistClick(track) },
                        onShareClick = { onShareClick(track) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistSpotlightCard(artist: ArtistSpotlight) {
    val platformColor = if (artist.platform == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed

    Card(
        modifier = Modifier
            .width(115.dp)
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = artist.imageUrl,
                    contentDescription = artist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artist.name,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = artist.genre,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
