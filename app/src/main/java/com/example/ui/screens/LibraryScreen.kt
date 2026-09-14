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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
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
import com.example.player.PlayerUiState
import com.example.sync.SyncState
import com.example.ui.components.TrackItemRow
import com.example.ui.theme.AeroCyanGlow
import com.example.ui.theme.AeroGelButton
import com.example.ui.theme.AeroGlassCard
import com.example.ui.theme.AeroIceWhite
import com.example.ui.theme.AeroSkyBlue
import com.example.ui.theme.AeroWallpaperBackground
import com.example.ui.theme.AeroWindowHeader
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import com.example.data.model.DownloadStatus
import com.example.data.model.TrackDownloadState
import com.example.ui.theme.AppPrimary
import com.example.ui.theme.ErrorRed

@Composable
fun LibraryScreen(
    playlists: List<PlaylistEntity>,
    downloadedTracks: List<TrackEntity>,
    likedTracks: List<TrackEntity>,
    allTracks: List<TrackEntity> = emptyList(),
    selectedPlaylist: PlaylistWithTracks?,
    playerState: PlayerUiState,
    syncState: SyncState,
    cachedPlaylists: List<CachedPlaylistMetadataEntity> = emptyList(),
    downloadStates: Map<String, TrackDownloadState> = emptyMap(),
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onClosePlaylistDetails: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onSyncNowClick: () -> Unit,
    onTogglePinOffline: (String, Boolean) -> Unit = { _, _ -> },
    onTrackClick: (TrackEntity, List<TrackEntity>) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onRetryDownload: ((TrackEntity) -> Unit)? = null,
    onRetryAllFailedDownloads: (() -> Unit)? = null,
    onSimulateError: ((TrackEntity) -> Unit)? = null,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Playlists", "Offline Vault", "Liked")

    // If viewing a specific playlist
    if (selectedPlaylist != null) {
        PlaylistDetailView(
            playlistWithTracks = selectedPlaylist,
            playerState = playerState,
            downloadStates = downloadStates,
            onBack = onClosePlaylistDetails,
            onTrackClick = { track -> onTrackClick(track, selectedPlaylist.tracks) },
            onLikeClick = onLikeClick,
            onDownloadClick = onDownloadClick,
            onRetryDownload = onRetryDownload,
            onAddToPlaylistClick = onAddToPlaylistClick,
            onShareClick = onShareClick,
            modifier = modifier
        )
        return
    }

    AeroWallpaperBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("library_screen")
        ) {
            // Header
            AeroWindowHeader(
                title = "Music Library",
                subtitle = "Playlists, Offline Tracks & Favorites",
                actions = {
                    AeroGelButton(
                        onClick = onCreatePlaylistClick,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("create_playlist_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Unified Playlist",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            // Cross-Platform Synchronization Glass Card
            AeroGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("library_sync_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = AeroCyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Liquid Sync Engine",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        AeroGelButton(
                            onClick = onSyncNowClick,
                            modifier = Modifier.testTag("sync_library_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (syncState.isSyncing) "Syncing..." else "Sync Now",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (syncState.isSyncing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { syncState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AeroCyanGlow,
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = syncState.currentStepDescription,
                            color = AeroCyanGlow,
                            fontSize = 11.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connected: ${syncState.spotifyAccount.accountUsername} & ${syncState.youtubeAccount.accountUsername} • Last: ${syncState.lastSyncFormatted}",
                            color = AeroIceWhite.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Glass Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = AeroCyanGlow,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AeroCyanGlow,
                        height = 3.dp
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
                                color = if (selectedTabIndex == index) Color.White else AeroIceWhite.copy(alpha = 0.7f),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Content based on tab
            when (selectedTabIndex) {
                0 -> {
                    // Unified Playlists
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(playlists) { playlist ->
                            AeroPlaylistCard(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist) }
                            )
                        }
                    }
                }
                1 -> {
                    // Offline Vault
                    val downloadingCount = downloadStates.values.count { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PENDING }
                    val errorCount = downloadStates.values.count { it.status == DownloadStatus.ERROR }

                    val vaultTracks = remember(downloadedTracks, downloadStates, allTracks) {
                        val activeTrackIds = downloadStates.filter {
                            it.value.status != DownloadStatus.NOT_DOWNLOADED
                        }.keys
                        val extraActiveTracks = allTracks.filter { it.id in activeTrackIds }
                        (downloadedTracks + extraActiveTracks).distinctBy { it.id }
                    }

                    val totalBytes = downloadedTracks.sumOf { it.downloadedBytes }
                    val totalMb = totalBytes / 1_000_000

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        item {
                            AeroGlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Offline Vault Management",
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${vaultTracks.size} Tracks Managed • ${totalMb} MB Cached",
                                                color = AeroIceWhite.copy(alpha = 0.75f),
                                                fontSize = 12.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.CloudDownload,
                                            contentDescription = null,
                                            tint = AppPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Status Summary Chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Downloaded Chip
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppPrimary.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DownloadDone,
                                                contentDescription = null,
                                                tint = AppPrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "${downloadedTracks.size} Ready",
                                                color = AppPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Downloading/Pending Chip
                                        if (downloadingCount > 0) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(HiResGold.copy(alpha = 0.15f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.HourglassTop,
                                                    contentDescription = null,
                                                    tint = HiResGold,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "$downloadingCount Downloading",
                                                    color = HiResGold,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        // Error Chip
                                        if (errorCount > 0) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(ErrorRed.copy(alpha = 0.15f))
                                                    .clickable { onRetryAllFailedDownloads?.invoke() }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ErrorOutline,
                                                    contentDescription = null,
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "$errorCount Failed (Retry)",
                                                    color = ErrorRed,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Active Error Banner with Retry All Action
                                    if (errorCount > 0) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ErrorRed.copy(alpha = 0.20f))
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ErrorOutline,
                                                    contentDescription = "Error",
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "$errorCount download(s) failed due to network glitch.",
                                                    color = Color.White,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(ErrorRed)
                                                    .clickable { onRetryAllFailedDownloads?.invoke() }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Retry",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "Retry All",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (cachedPlaylists.isNotEmpty()) {
                            item {
                                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                                    Text(
                                        text = "Cached Playlists for Offline Access",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Stored locally for uninterrupted listening",
                                        color = AeroIceWhite.copy(alpha = 0.7f),
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
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        if (vaultTracks.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No tracks downloaded yet. Tap download on any track.", color = AeroIceWhite.copy(alpha = 0.7f), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(vaultTracks) { track ->
                                val isCurrent = playerState.currentTrack?.id == track.id
                                val dState = downloadStates[track.id] ?: if (track.isDownloaded) TrackDownloadState(DownloadStatus.DOWNLOADED) else TrackDownloadState(DownloadStatus.NOT_DOWNLOADED)
                                TrackItemRow(
                                    track = track,
                                    isPlaying = isCurrent && playerState.isPlaying,
                                    isCurrentTrack = isCurrent,
                                    downloadState = dState,
                                    onTrackClick = { onTrackClick(track, vaultTracks) },
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
                                    .padding(horizontal = 18.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${likedTracks.size} Favorite Songs across Spotify & YouTube",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
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
                                    Text("No liked tracks yet. Tap heart to add songs to your favorites.", color = AeroIceWhite.copy(alpha = 0.7f), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(likedTracks) { track ->
                                val isCurrent = playerState.currentTrack?.id == track.id
                                val dState = downloadStates[track.id] ?: if (track.isDownloaded) TrackDownloadState(DownloadStatus.DOWNLOADED) else TrackDownloadState(DownloadStatus.NOT_DOWNLOADED)
                                TrackItemRow(
                                    track = track,
                                    isPlaying = isCurrent && playerState.isPlaying,
                                    isCurrentTrack = isCurrent,
                                    downloadState = dState,
                                    onTrackClick = { onTrackClick(track, likedTracks) },
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
    }
}

@Composable
fun AeroPlaylistCard(playlist: PlaylistEntity, onClick: () -> Unit) {
    AeroGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_card_${playlist.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A1C36))
            ) {
                AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.title,
                    color = Color.White,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = playlist.description,
                    color = AeroIceWhite.copy(alpha = 0.75f),
                    fontSize = 11.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Spotify 🟢 + YouTube 🔴",
                        color = AeroCyanGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x3300E5FF))
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
    downloadStates: Map<String, TrackDownloadState> = emptyMap(),
    onBack: () -> Unit,
    onTrackClick: (TrackEntity) -> Unit,
    onLikeClick: (TrackEntity) -> Unit,
    onDownloadClick: (TrackEntity) -> Unit,
    onRetryDownload: ((TrackEntity) -> Unit)? = null,
    onAddToPlaylistClick: (TrackEntity) -> Unit,
    onShareClick: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist = playlistWithTracks.playlist
    val tracks = playlistWithTracks.tracks

    AeroWallpaperBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("playlist_detail_view"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                AeroGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
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
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = playlist.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF091F3E))
                            ) {
                                AsyncImage(
                                    model = playlist.coverUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Unified Cross-Platform Playlist",
                                    color = AeroCyanGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = playlist.description,
                                    color = AeroIceWhite.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 3.dp)
                                )
                                Text(
                                    text = "${tracks.size} tracks from Spotify & YouTube",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
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
                        Text("No songs in this playlist yet. Add songs from Home or Search!", color = AeroIceWhite.copy(alpha = 0.7f))
                    }
                }
            } else {
                items(tracks) { track ->
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

@Composable
fun CachedPlaylistRow(
    cached: CachedPlaylistMetadataEntity,
    onPinClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AeroGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp)) {
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
                        color = Color.White,
                        fontSize = 13.5.sp,
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
                                .background(HiResGold.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("PINNED", color = HiResGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                val mb = cached.cachedSizeBytes / 1_000_000
                Text(
                    text = "${cached.downloadedTrackCount}/${cached.totalTrackCount} tracks • ${mb} MB",
                    color = AeroIceWhite.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onPinClick) {
                Icon(
                    imageVector = if (cached.isOfflinePinned) Icons.Filled.CloudDone else Icons.Filled.DownloadDone,
                    contentDescription = if (cached.isOfflinePinned) "Unpin" else "Pin",
                    tint = if (cached.isOfflinePinned) HiResGold else AeroIceWhite.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
