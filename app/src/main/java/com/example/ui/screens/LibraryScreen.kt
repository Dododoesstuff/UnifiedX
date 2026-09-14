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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistWithTracks
import com.example.data.local.TrackEntity
import com.example.data.model.PlatformSource
import com.example.player.PlayerUiState
import com.example.sync.SyncState
import com.example.ui.components.TrackItemRow
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun LibraryScreen(
    playlists: List<PlaylistEntity>,
    downloadedTracks: List<TrackEntity>,
    likedTracks: List<TrackEntity>,
    selectedPlaylist: PlaylistWithTracks?,
    playerState: PlayerUiState,
    syncState: SyncState,
    cachedPlaylists: List<CachedPlaylistMetadataEntity> = emptyList(),
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onClosePlaylistDetails: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onSyncNowClick: () -> Unit,
    onTogglePinOffline: (String, Boolean) -> Unit = { _, _ -> },
    onTrackClick: (TrackEntity, List<TrackEntity>) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Unified Playlists", "Offline Vault", "Liked Songs")

    // If viewing a specific playlist
    if (selectedPlaylist != null) {
        PlaylistDetailView(
            playlistWithTracks = selectedPlaylist,
            playerState = playerState,
            onBack = onClosePlaylistDetails,
            onTrackClick = { track -> onTrackClick(track, selectedPlaylist.tracks) },
            onLikeClick = onLikeClick,
            onDownloadClick = onDownloadClick,
            onAddToPlaylistClick = onAddToPlaylistClick,
            onShareClick = onShareClick,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .testTag("library_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Unified Library",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Spotify & YouTube seamlessly synchronized",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = onCreatePlaylistClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CrossPurple)
                    .size(42.dp)
                    .testTag("create_playlist_header_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Unified Playlist",
                    tint = Color.White
                )
            }
        }

        // Cross-Platform Synchronization Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("library_sync_card"),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cross-Platform Sync Engine",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onSyncNowClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("sync_library_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (syncState.isSyncing) "Syncing..." else "Sync Now",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (syncState.isSyncing) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { syncState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = SpotifyGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = syncState.currentStepDescription,
                        color = ElectricViolet,
                        fontSize = 11.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connected: ${syncState.spotifyAccount.accountUsername} (Spotify) & ${syncState.youtubeAccount.accountUsername} (YouTube) • Last: ${syncState.lastSyncFormatted}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = CrossPurple,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CrossPurple
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) TextPrimary else TextSecondary,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content based on tab
        when (selectedTabIndex) {
            0 -> {
                // Unified Playlists
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) }
                        )
                    }
                }
            }
            1 -> {
                // Offline Vault
                val totalBytes = downloadedTracks.sumOf { it.downloadedBytes }
                val totalMb = totalBytes / 1_000_000

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${downloadedTracks.size} Offline Tracks",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${totalMb} MB Cached (Lossless/HQ)",
                                color = HiResGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (cachedPlaylists.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                                Text(
                                    text = "Cached Playlists for Offline Access",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Playlist metadata stored in Room database for offline browsing",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        items(cachedPlaylists) { cached ->
                            CachedPlaylistRow(
                                cached = cached,
                                onPinClick = { onTogglePinOffline(cached.playlistId, cached.isOfflinePinned) },
                                onClick = {
                                    playlists.find { it.id == cached.playlistId }?.let { onPlaylistClick(it) }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    if (downloadedTracks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No tracks downloaded yet. Tap download on any track for offline listening.", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(downloadedTracks) { track ->
                            val isCurrent = playerState.currentTrack?.id == track.id
                            TrackItemRow(
                                track = track,
                                isPlaying = isCurrent && playerState.isPlaying,
                                isCurrentTrack = isCurrent,
                                onTrackClick = { onTrackClick(track, downloadedTracks) },
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
            2 -> {
                // Liked Songs
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${likedTracks.size} Favorite Songs across Spotify & YouTube",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (likedTracks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No liked tracks yet. Tap heart to add songs to your favorites.", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(likedTracks) { track ->
                            val isCurrent = playerState.currentTrack?.id == track.id
                            TrackItemRow(
                                track = track,
                                isPlaying = isCurrent && playerState.isPlaying,
                                isCurrentTrack = isCurrent,
                                onTrackClick = { onTrackClick(track, likedTracks) },
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
    }
}

@Composable
fun PlaylistCard(playlist: PlaylistEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("playlist_card_${playlist.id}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = playlist.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Spotify 🟢 + YouTube 🔴",
                        color = CrossPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrossPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistDetailView(
    playlistWithTracks: PlaylistWithTracks,
    playerState: PlayerUiState,
    onBack: () -> Unit,
    onTrackClick: (TrackEntity) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist = playlistWithTracks.playlist
    val tracks = playlistWithTracks.tracks

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .testTag("playlist_detail_view"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_playlist_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = playlist.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = playlist.coverUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Unified Cross-Platform Playlist",
                            color = CrossPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = playlist.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${tracks.size} tracks from Spotify & YouTube",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        if (tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No songs in this playlist yet. Add songs from Home or Search!", color = TextSecondary)
                }
            }
        } else {
            items(tracks) { track ->
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

@Composable
fun CachedPlaylistRow(
    cached: CachedPlaylistMetadataEntity,
    onPinClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(52.dp)) {
                AsyncImage(
                    model = cached.coverUrl,
                    contentDescription = cached.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cached.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (cached.isOfflinePinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(HiResGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("OFFLINE PINNED", color = HiResGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                val mb = cached.cachedSizeBytes / 1_000_000
                Text(
                    text = "${cached.downloadedTrackCount}/${cached.totalTrackCount} tracks cached • ${mb} MB",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onPinClick) {
                Icon(
                    imageVector = if (cached.isOfflinePinned) Icons.Filled.CloudDone else Icons.Filled.DownloadDone,
                    contentDescription = if (cached.isOfflinePinned) "Unpin from Offline" else "Pin for Offline",
                    tint = if (cached.isOfflinePinned) HiResGold else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
