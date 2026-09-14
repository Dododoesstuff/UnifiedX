package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.TrackEntity
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.FullScreenPlayer
import com.example.ui.components.MiniPlayer
import com.example.ui.components.SocialShareSheet
import com.example.ui.components.SyncedLyricsView
import com.example.ui.screens.CollabSessionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UnifiedXApp()
            }
        }
    }
}

@Composable
fun UnifiedXApp(viewModel: MainViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val playerState by viewModel.playerUiState.collectAsState()
    val syncState by viewModel.syncUiState.collectAsState()
    val collabState by viewModel.collabUiState.collectAsState()

    val allTracks by viewModel.allTracks.collectAsState()
    val downloadedTracks by viewModel.downloadedTracks.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val cachedPlaylists by viewModel.cachedPlaylists.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()

    val isFullScreenPlayerOpen by viewModel.isFullScreenPlayerOpen.collectAsState()
    val isLyricsViewOpen by viewModel.isLyricsViewOpen.collectAsState()
    val trackToAddToPlaylist by viewModel.trackToAddToPlaylist.collectAsState()
    val trackToShare by viewModel.trackToShare.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showCreatePlaylistSheet by remember { mutableStateOf(false) }

    // Intercept back button when overlay sheets are active
    BackHandler(enabled = isLyricsViewOpen || isFullScreenPlayerOpen || selectedPlaylist != null) {
        if (isLyricsViewOpen) {
            viewModel.setLyricsViewOpen(false)
        } else if (isFullScreenPlayerOpen) {
            viewModel.setFullScreenPlayerOpen(false)
        } else if (selectedPlaylist != null) {
            viewModel.closePlaylistDetails()
        }
    }

    // Show message if player error occurs
    LaunchedEffect(playerState.errorMessage) {
        playerState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearPlayerError()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDeep),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .navigationBarsPadding()
            ) {
                // Persistent Mini Player above bottom navigation
                if (playerState.currentTrack != null) {
                    MiniPlayer(
                        playerState = playerState,
                        onExpandClick = { viewModel.setFullScreenPlayerOpen(true) },
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onSkipNextClick = { viewModel.skipNext() },
                        onSkipPreviousClick = { viewModel.skipPrevious() },
                        onSeek = { viewModel.seekTo(it) },
                        onLikeClick = { playerState.currentTrack?.let { viewModel.toggleLike(it) } }
                    )
                }

                // M3 Bottom Navigation Bar
                NavigationBar(
                    containerColor = ObsidianSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppNavTab.HOME,
                        onClick = { viewModel.selectTab(AppNavTab.HOME) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                        label = { Text("Discover", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrossPurple,
                            selectedTextColor = CrossPurple,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = CrossPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.SEARCH,
                        onClick = { viewModel.selectTab(AppNavTab.SEARCH) },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrossPurple,
                            selectedTextColor = CrossPurple,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = CrossPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_search")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.LIBRARY,
                        onClick = { viewModel.selectTab(AppNavTab.LIBRARY) },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                        label = { Text("Library", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrossPurple,
                            selectedTextColor = CrossPurple,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = CrossPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_library")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.JAM,
                        onClick = { viewModel.selectTab(AppNavTab.JAM) },
                        icon = { Icon(Icons.Default.Groups, contentDescription = "Collab Jam") },
                        label = { Text("Collab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrossPurple,
                            selectedTextColor = CrossPurple,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = CrossPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_collab")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.SETTINGS,
                        onClick = { viewModel.selectTab(AppNavTab.SETTINGS) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrossPurple,
                            selectedTextColor = CrossPurple,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = CrossPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        tracks = allTracks,
                        playerState = playerState,
                        syncState = syncState,
                        discoveryArtists = viewModel.discoveryArtists,
                        onTrackClick = { viewModel.playTrack(it, allTracks) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onDownloadClick = {
                            viewModel.toggleDownload(it)
                            coroutineScope.launch {
                                val msg = if (it.isDownloaded) "Removed download" else "Downloaded ${it.title} for offline listening"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylistDialog(it) },
                        onShareClick = { viewModel.openSocialShareDialog(it) },
                        onSyncNowClick = {
                            viewModel.startSync()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Cross-platform synchronization in progress...")
                            }
                        }
                    )
                }
                AppNavTab.SEARCH -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        activeFilter = searchFilter,
                        onFilterSelect = { viewModel.setSearchFilter(it) },
                        results = searchResults,
                        playerState = playerState,
                        onTrackClick = { viewModel.playTrack(it, searchResults) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onDownloadClick = {
                            viewModel.toggleDownload(it)
                            coroutineScope.launch {
                                val msg = if (it.isDownloaded) "Removed download" else "Downloaded ${it.title} for offline listening"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylistDialog(it) },
                        onShareClick = { viewModel.openSocialShareDialog(it) }
                    )
                }
                AppNavTab.LIBRARY -> {
                    LibraryScreen(
                        playlists = playlists,
                        downloadedTracks = downloadedTracks,
                        likedTracks = likedTracks,
                        selectedPlaylist = selectedPlaylist,
                        playerState = playerState,
                        syncState = syncState,
                        cachedPlaylists = cachedPlaylists,
                        onPlaylistClick = { viewModel.openPlaylist(it) },
                        onClosePlaylistDetails = { viewModel.closePlaylistDetails() },
                        onCreatePlaylistClick = { showCreatePlaylistSheet = true },
                        onSyncNowClick = { viewModel.startSync() },
                        onTogglePinOffline = { playlistId, currentPinned ->
                            viewModel.togglePinPlaylistOffline(playlistId, currentPinned)
                            coroutineScope.launch {
                                val msg = if (currentPinned) "Unpinned playlist from offline storage" else "Pinned playlist for offline access"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onDownloadClick = {
                            viewModel.toggleDownload(it)
                            coroutineScope.launch {
                                val msg = if (it.isDownloaded) "Removed download" else "Downloaded ${it.title} for offline listening"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylistDialog(it) },
                        onShareClick = { viewModel.openSocialShareDialog(it) }
                    )
                }
                AppNavTab.JAM -> {
                    CollabSessionScreen(
                        collabState = collabState,
                        allTracks = allTracks,
                        playerState = playerState,
                        onHostSession = { viewModel.hostCollabSession(it) },
                        onJoinSession = { viewModel.joinCollabSession(it) },
                        onLeaveSession = { viewModel.leaveCollabSession() },
                        onAddTrackToQueue = { viewModel.addTrackToCollabQueue(it) },
                        onUpvoteTrack = { viewModel.upvoteCollabTrack(it) },
                        onShareInvite = { viewModel.shareCollabInvite() },
                        onPlayTrack = { viewModel.playTrack(it, allTracks) }
                    )
                }
                AppNavTab.SETTINGS -> {
                    SettingsScreen(
                        playerState = playerState,
                        syncState = syncState,
                        userPreferences = userPreferences,
                        onSetAudioQuality = { viewModel.updateStreamingQuality(it) },
                        onSetOfflineModeOnly = { viewModel.toggleOfflineListeningMode(it) },
                        onSetEqPreset = { viewModel.updateEqualizerPreset(it) },
                        onUpdateAccountCredential = { platform, token, user ->
                            viewModel.updateAccountCredential(platform, token, user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Updated credentials for ${platform.displayName} (Saved to Room)")
                            }
                        }
                    )
                }
            }
        }
    }

    // Full Screen Player Sheet (Slide up modal)
    AnimatedVisibility(
        visible = isFullScreenPlayerOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        FullScreenPlayer(
            playerState = playerState,
            onCollapse = { viewModel.setFullScreenPlayerOpen(false) },
            onPlayPause = { viewModel.togglePlayPause() },
            onSeek = { viewModel.seekTo(it) },
            onNext = { viewModel.skipNext() },
            onPrevious = { viewModel.skipPrevious() },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onToggleRepeat = { viewModel.toggleRepeat() },
            onLikeClick = { playerState.currentTrack?.let { viewModel.toggleLike(it) } },
            onOpenLyrics = { viewModel.setLyricsViewOpen(true) },
            onCycleEqPreset = {
                val currentPreset = playerState.activeEqPreset
                val allPresets = com.example.player.EqPreset.values()
                val nextIndex = (allPresets.indexOf(currentPreset) + 1) % allPresets.size
                viewModel.setEqPreset(allPresets[nextIndex])
            },
            onAddToPlaylistClick = { playerState.currentTrack?.let { viewModel.openAddToPlaylistDialog(it) } },
            onShareClick = { playerState.currentTrack?.let { viewModel.openSocialShareDialog(it) } }
        )
    }

    // Synced Real-Time Lyrics View (Slide up on top of player)
    AnimatedVisibility(
        visible = isLyricsViewOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        SyncedLyricsView(
            playerState = playerState,
            onClose = { viewModel.setLyricsViewOpen(false) },
            onSeekToTimestamp = { viewModel.seekTo(it) },
            onShareSnippet = { snippet ->
                playerState.currentTrack?.let { track ->
                    viewModel.shareTrackSocially(track, snippet)
                }
            },
            onReloadLyrics = { viewModel.reloadLyrics() },
            onPlayPause = { viewModel.togglePlayPause() },
            onSkipNext = { viewModel.skipNext() },
            onSkipPrevious = { viewModel.skipPrevious() }
        )
    }

    // Add to Unified Playlist Dialog
    if (trackToAddToPlaylist != null) {
        AddToPlaylistDialog(
            track = trackToAddToPlaylist!!,
            playlists = playlists,
            onDismiss = { viewModel.closeAddToPlaylistDialog() },
            onAddToPlaylist = { playlistId ->
                viewModel.addTrackToPlaylist(playlistId, trackToAddToPlaylist!!.id)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Added ${trackToAddToPlaylist!!.title} to unified playlist")
                }
            },
            onCreateNewPlaylist = { title, desc ->
                viewModel.createUnifiedPlaylist(title, desc)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Created unified playlist: $title")
                }
            }
        )
    }

    // Direct Create Unified Playlist Dialog from Library button
    if (showCreatePlaylistSheet) {
        val sampleTrack = allTracks.firstOrNull() ?: TrackEntity(
            id = "seed",
            title = "First Song",
            artist = "Artist",
            album = "Album",
            durationMs = 180000,
            platformSource = com.example.data.model.PlatformSource.SPOTIFY,
            sourceTrackId = "",
            coverUrl = "",
            streamUrl = "",
            audioQuality = com.example.data.model.AudioQuality.HIGH,
            isDownloaded = false,
            isLiked = false,
            lyricsLrc = "",
            genre = ""
        )
        AddToPlaylistDialog(
            track = sampleTrack,
            playlists = playlists,
            onDismiss = { showCreatePlaylistSheet = false },
            onAddToPlaylist = { showCreatePlaylistSheet = false },
            onCreateNewPlaylist = { title, desc ->
                viewModel.createUnifiedPlaylist(title, desc)
                showCreatePlaylistSheet = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Created unified playlist: $title")
                }
            }
        )
    }

    // Social Media Share Card Sheet
    if (trackToShare != null) {
        SocialShareSheet(
            track = trackToShare!!,
            onDismiss = { viewModel.closeSocialShareDialog() },
            onShareSocial = {
                viewModel.shareTrackSocially(trackToShare!!)
                viewModel.closeSocialShareDialog()
            }
        )
    }
}
