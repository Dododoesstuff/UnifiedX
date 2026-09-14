package com.example.ui.screens

import com.example.data.model.DownloadStatus
import com.example.data.model.TrackDownloadState

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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.draw.shadow
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
import com.example.ui.theme.AeroCobaltDark
import com.example.ui.theme.AeroCyanGlow
import com.example.ui.theme.AeroGelBlueBottom
import com.example.ui.theme.AeroGelBlueTop
import com.example.ui.theme.AeroGelButton
import com.example.ui.theme.AeroGlassCard
import com.example.ui.theme.AeroIceWhite
import com.example.ui.theme.AeroLiquidAqua
import com.example.ui.theme.AeroSkyBlue
import com.example.ui.theme.AeroWallpaperBackground
import com.example.ui.theme.AeroWindowHeader
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
    downloadStates: Map<String, TrackDownloadState> = emptyMap(),
    onTrackClick: (TrackEntity) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onRetryDownload: ((TrackEntity) -> Unit)? = null,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    onSyncNowClick: () -> Unit,
    onOpenLinkAccountsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val isSearching = searchQuery.isNotBlank()

    // Filtered list
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

    AeroWallpaperBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Title Bar
            item {
                AeroWindowHeader(
                    title = "Unified Music",
                    subtitle = "Spotify & YouTube Unified Experience",
                    onOrbClick = onOpenLinkAccountsClick,
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Link accounts pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(com.example.ui.theme.AppPrimary.copy(alpha = 0.15f))
                                    .clickable(onClick = onOpenLinkAccountsClick)
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                                    .testTag("home_link_accounts_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Accounts",
                                    tint = com.example.ui.theme.AppPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Accounts",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Sync pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(com.example.ui.theme.AppSurfaceElevated)
                                    .clickable(onClick = onSyncNowClick)
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                                    .testTag("home_sync_badge"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    tint = com.example.ui.theme.TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (syncState.isSyncing) "Syncing" else "Synced",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                )
            }

            // Search Input Field
            item {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search Spotify & YouTube tracks...",
                            color = com.example.ui.theme.TextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearching) com.example.ui.theme.AppPrimary else com.example.ui.theme.TextSecondary,
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
                                    tint = com.example.ui.theme.TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = com.example.ui.theme.AppSurface,
                        unfocusedContainerColor = com.example.ui.theme.AppSurface,
                        focusedBorderColor = com.example.ui.theme.AppPrimary,
                        unfocusedBorderColor = com.example.ui.theme.AppBorder,
                        focusedTextColor = com.example.ui.theme.TextPrimary,
                        unfocusedTextColor = com.example.ui.theme.TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("home_top_search_bar")
                )
            }

            // Glass Filter Chips Row
            item {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "ALL" to "All Music",
                        "SPOTIFY" to "Spotify 🟢",
                        "YOUTUBE" to "YouTube 🔴",
                        "LOSSLESS" to "Hi-Res Audio",
                        "DOWNLOADED" to "Offline 📥"
                    )

                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = key },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = com.example.ui.theme.OxygenRed,
                                selectedLabelColor = Color.White,
                                containerColor = com.example.ui.theme.OxygenSurface,
                                labelColor = com.example.ui.theme.OxygenTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = com.example.ui.theme.OxygenCardBorder,
                                selectedBorderColor = com.example.ui.theme.OxygenRed
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }

            // Search Results or Feed
            if (isSearching) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Search Results",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${displayTracks.size} songs found",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                        }

                        if (displayTracks.isNotEmpty()) {
                            AeroGelButton(
                                onClick = { displayTracks.firstOrNull()?.let { onTrackClick(it) } },
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
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
                    Spacer(modifier = Modifier.height(6.dp))
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
                                text = "No matching tracks for \"$searchQuery\"",
                                color = AeroIceWhite.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(displayTracks) { track ->
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
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            } else {
                // Windows 7 Aero Fusion Featured Glass Card
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    AeroGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("home_hero_banner"),
                        onClick = { tracks.firstOrNull()?.let { onTrackClick(it) } }
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
                                        tint = com.example.ui.theme.AppPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UNIFIED MUSIC FUSION",
                                        color = com.example.ui.theme.AppPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Cross-Platform Playlist Mix",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Lossless Spotify + High-Fidelity YouTube stream blend",
                                    color = TextSecondary,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Glossy Gel Play Button
                            AeroGelButton(
                                onClick = { tracks.firstOrNull()?.let { onTrackClick(it) } },
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(46.dp)
                                    .testTag("hero_quick_play_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Fusion",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Featured Artists in Liquid Glass
                if (discoveryArtists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Spotlight Artists",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 18.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(discoveryArtists) { artist ->
                                    ArtistSpotlightGlassCard(artist = artist)
                                }
                            }
                        }
                    }
                }

                // Music Tracks List Header
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Trending Catalog",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${displayTracks.size} songs ready for instant streaming",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                        }

                        // Shuffle & Play All Gel Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val shuffled = displayTracks.shuffled()
                                    shuffled.firstOrNull()?.let { onTrackClick(it) }
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .testTag("library_shuffle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = AeroIceWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            AeroGelButton(
                                onClick = { displayTracks.firstOrNull()?.let { onTrackClick(it) } },
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(34.dp)
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
                    Spacer(modifier = Modifier.height(6.dp))
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
                                text = "No tracks available in this filter",
                                color = AeroIceWhite.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(displayTracks) { track ->
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
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistSpotlightGlassCard(artist: ArtistSpotlight) {
    val platformColor = if (artist.platform == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed

    AeroGlassCard(
        modifier = Modifier.width(118.dp),
        contentPadding = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF092040))
            ) {
                AsyncImage(
                    model = artist.imageUrl,
                    contentDescription = artist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = artist.name,
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = artist.genre,
                color = AeroIceWhite.copy(alpha = 0.7f),
                fontSize = 9.5.sp,
                maxLines = 1
            )
        }
    }
}
