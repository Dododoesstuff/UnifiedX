package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.example.ui.components.AeroSuperbarNav
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.AppSplashScreen
import com.example.ui.components.FullScreenPlayer
import com.example.ui.components.MiniPlayer
import com.example.ui.components.SocialShareSheet
import com.example.ui.components.SyncedLyricsView
import com.example.ui.screens.CollabSessionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ServiceLoginScreen
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
    val spotifyOAuthState by viewModel.spotifyOAuthState.collectAsState()
    val youtubeOAuthState by viewModel.youtubeOAuthState.collectAsState()
    val savedSpotifyAccounts by viewModel.savedSpotifyAccounts.collectAsState()
    val savedYouTubeAccounts by viewModel.savedYouTubeAccounts.collectAsState()

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
    val downloadStates by viewModel.downloadStates.collectAsState()

    val isLoginScreenOpen by viewModel.isLoginScreenOpen.collectAsState()
    val isFullScreenPlayerOpen by viewModel.isFullScreenPlayerOpen.collectAsState()
    val isLyricsViewOpen by viewModel.isLyricsViewOpen.collectAsState()
    val isVisualizerSettingsOpen by viewModel.isVisualizerSettingsOpen.collectAsState()
    val visualizerSettings by viewModel.visualizerSettings.collectAsState()
    val visualizerFrequencies by viewModel.visualizerFrequencies.collectAsState()
    val trackToAddToPlaylist by viewModel.trackToAddToPlaylist.collectAsState()
    val trackToShare by viewModel.trackToShare.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showCreatePlaylistSheet by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }

    // Request notification permission for background playback controls on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Intercept back button when overlay sheets are active
    BackHandler(enabled = isVisualizerSettingsOpen || isLoginScreenOpen || isLyricsViewOpen || isFullScreenPlayerOpen || selectedPlaylist != null) {
        if (isVisualizerSettingsOpen) {
            viewModel.setVisualizerSettingsOpen(false)
        } else if (isLoginScreenOpen) {
            viewModel.setLoginScreenOpen(false)
        } else if (isLyricsViewOpen) {
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

                // Windows 7 Aero Superbar Bottom Navigation
                AeroSuperbarNav(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
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
                        downloadStates = downloadStates,
                        onTrackClick = { viewModel.playTrack(it, allTracks) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onDownloadClick = {
                            viewModel.toggleDownload(it)
                            coroutineScope.launch {
                                val dState = viewModel.getDownloadStateForTrack(it).status
                                val msg = when (dState) {
                                    com.example.data.model.DownloadStatus.DOWNLOADED -> "Removed download for ${it.title}"
                                    com.example.data.model.DownloadStatus.DOWNLOADING, com.example.data.model.DownloadStatus.PENDING -> "Downloading ${it.title}..."
                                    else -> "Started download for ${it.title}"
                                }
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onRetryDownload = { track ->
                            viewModel.retryDownload(track)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Retrying download for ${track.title}")
                            }
                        },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylistDialog(it) },
                        onShareClick = { viewModel.openSocialShareDialog(it) },
                        onSyncNowClick = {
                            viewModel.startSync()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Cross-platform synchronization in progress...")
                            }
                        },
                        onOpenLinkAccountsClick = { viewModel.setLoginScreenOpen(true) }
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
                        downloadStates = downloadStates,
                        onTrackClick = { viewModel.playTrack(it, searchResults) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onDownloadClick = {
                            viewModel.toggleDownload(it)
                            coroutineScope.launch {
                                val dState = viewModel.getDownloadStateForTrack(it).status
                                val msg = when (dState) {
                                    com.example.data.model.DownloadStatus.DOWNLOADED -> "Removed download for ${it.title}"
                                    com.example.data.model.DownloadStatus.DOWNLOADING, com.example.data.model.DownloadStatus.PENDING -> "Downloading ${it.title}..."
                                    else -> "Started download for ${it.title}"
                                }
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onRetryDownload = { track ->
                            viewModel.retryDownload(track)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Retrying download for ${track.title}")
                            }
                        },
                        onAddToPlaylistClick = { viewModel.openAddToPlaylistDialog(it) },
                        onShareClick = { viewModel.openSocialShareDialog(it) },
                        onSwitchPlatformCounterpart = {
                            viewModel.switchPlatformCounterpart(it)
                            coroutineScope.launch {
                                val target = if (it.platformSource == com.example.data.model.PlatformSource.SPOTIFY) "YouTube 4K Live" else "Spotify Lossless"
                                snackbarHostState.showSnackbar("Switched to $target version")
                            }
                        }
                    )
                }
                AppNavTab.LIBRARY -> {
                    LibraryScreen(
                        playlists = playlists,
                        downloadedTracks = downloadedTracks,
                        likedTracks = likedTracks,
                        allTracks = allTracks,
                        selectedPlaylist = selectedPlaylist,
                        playerState = playerState,
                        syncState = syncState,
                        cachedPlaylists = cachedPlaylists,
                        downloadStates = downloadStates,
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
                                val dState = viewModel.getDownloadStateForTrack(it).status
                                val msg = when (dState) {
                                    com.example.data.model.DownloadStatus.DOWNLOADED -> "Removed download for ${it.title}"
                                    com.example.data.model.DownloadStatus.DOWNLOADING, com.example.data.model.DownloadStatus.PENDING -> "Downloading ${it.title}..."
                                    else -> "Started download for ${it.title}"
                                }
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onRetryDownload = { track ->
                            viewModel.retryDownload(track)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Retrying download for ${track.title}")
                            }
                        },
                        onRetryAllFailedDownloads = {
                            viewModel.retryAllFailedDownloads()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Retrying all failed downloads")
                            }
                        },
                        onSimulateError = { track ->
                            viewModel.simulateDownloadError(track)
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
                        visualizerSettings = visualizerSettings,
                        visualizerFrequencies = visualizerFrequencies,
                        onUpdateVisualizerSettings = { viewModel.updateVisualizerSettings(it) },
                        onOpenVisualizerStudio = { viewModel.setVisualizerSettingsOpen(true) },
                        onSetAudioQuality = { viewModel.updateStreamingQuality(it) },
                        onSetOfflineModeOnly = { viewModel.toggleOfflineListeningMode(it) },
                        onSetEqPreset = { viewModel.updateEqualizerPreset(it) },
                        onUpdateCrossfadeSettings = { enabled, seconds -> viewModel.updateCrossfadeSettings(enabled, seconds) },
                        onUpdateAccountCredential = { platform, token, user ->
                            viewModel.updateAccountCredential(platform, token, user)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Updated credentials for ${platform.displayName} (Saved to Room)")
                            }
                        },
                        onOpenLinkAccountsClick = { viewModel.setLoginScreenOpen(true) }
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
            frequencies = visualizerFrequencies,
            visualizerSettings = visualizerSettings,
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
            onShareClick = { playerState.currentTrack?.let { viewModel.openSocialShareDialog(it) } },
            onSwitchPlatformCounterpart = {
                playerState.currentTrack?.let {
                    viewModel.switchPlatformCounterpart(it)
                    coroutineScope.launch {
                        val target = if (it.platformSource == com.example.data.model.PlatformSource.SPOTIFY) "YouTube 4K Live" else "Spotify Lossless"
                        snackbarHostState.showSnackbar("Switched to $target version")
                    }
                }
            },
            onOpenVisualizerSettings = { viewModel.setVisualizerSettingsOpen(true) }
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
            frequencies = visualizerFrequencies,
            visualizerSettings = visualizerSettings,
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

    // App Startup Splash Screen with Fancy Logo Animation
    if (showSplashScreen) {
        AppSplashScreen(
            onAnimationFinish = { showSplashScreen = false }
        )
    }

    // Guided Music Service Login & Account Linking Screen Overlay
    AnimatedVisibility(
        visible = isLoginScreenOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        ServiceLoginScreen(
            syncState = syncState,
            allPlaylists = playlists,
            allTracks = allTracks,
            spotifyOAuthState = spotifyOAuthState,
            youtubeOAuthState = youtubeOAuthState,
            savedSpotifyAccounts = savedSpotifyAccounts,
            savedYouTubeAccounts = savedYouTubeAccounts,
            onLoginWithEmail = { platform, email, password, displayName, onComplete ->
                viewModel.loginWithEmail(platform, email, password, displayName) { success, error ->
                    onComplete(success, error)
                    if (success) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Logged into ${platform.displayName} ($email)")
                        }
                    }
                }
            },
            onSwitchAccount = { platform, email, displayName ->
                viewModel.switchAccount(platform, email, displayName)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Switched to ${platform.displayName} ($email)")
                }
            },
            onSignOutAccount = { platform ->
                viewModel.signOutAccount(platform)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Signed out of ${platform.displayName}")
                }
            },
            onRemoveSavedAccount = { platform, email ->
                viewModel.removeSavedAccount(platform, email)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Removed $email from saved accounts")
                }
            },
            onLinkSpotify = { token, username ->
                viewModel.updateAccountCredential(com.example.data.model.PlatformSource.SPOTIFY, token, username)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Spotify account linked & authenticated")
                }
            },
            onLinkYouTube = { apiKey, channelName ->
                viewModel.updateAccountCredential(com.example.data.model.PlatformSource.YOUTUBE, apiKey, channelName)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("YouTube account linked & authenticated")
                }
            },
            onExchangeOAuthCode = { source, code ->
                val oauthPlatform = if (source == com.example.data.model.PlatformSource.SPOTIFY) {
                    com.example.data.model.oauth.OAuthPlatform.SPOTIFY
                } else {
                    com.example.data.model.oauth.OAuthPlatform.YOUTUBE
                }
                viewModel.exchangeOAuthCode(oauthPlatform, code)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Exchanging OAuth code with ${oauthPlatform.displayName}...")
                }
            },
            onRefreshOAuthToken = { source ->
                val oauthPlatform = if (source == com.example.data.model.PlatformSource.SPOTIFY) {
                    com.example.data.model.oauth.OAuthPlatform.SPOTIFY
                } else {
                    com.example.data.model.oauth.OAuthPlatform.YOUTUBE
                }
                viewModel.refreshOAuthToken(oauthPlatform)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Refreshing OAuth token for ${oauthPlatform.displayName}...")
                }
            },
            onTransferPlaylist = { sourcePlatform, targetPlatform, playlistTitle, tracks ->
                viewModel.transferPlaylist(sourcePlatform, targetPlatform, playlistTitle, tracks)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Transferring '$playlistTitle' to ${targetPlatform.displayName}...")
                }
            },
            onTransferLikedSongs = { sourcePlatform, targetPlatform ->
                viewModel.transferLikedSongs(sourcePlatform, targetPlatform)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Transferring Liked Songs to ${targetPlatform.displayName}...")
                }
            },
            onDisconnectPlatform = { platform ->
                viewModel.disconnectService(platform)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Disconnected ${platform.displayName}")
                }
            },
            onStartSync = {
                viewModel.startSync()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Cross-platform synchronization in progress...")
                }
            },
            onContinueToApp = {
                viewModel.setLoginScreenOpen(false)
            }
        )
    }

    // Waveform Visualizer Studio Customization Bottom Sheet
    AnimatedVisibility(
        visible = isVisualizerSettingsOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        com.example.visualizer.VisualizerCustomizationSheet(
            settings = visualizerSettings,
            frequencies = visualizerFrequencies,
            isPlaying = playerState.isPlaying,
            platformSource = playerState.currentTrack?.platformSource ?: com.example.data.model.PlatformSource.SPOTIFY,
            onSettingsChanged = { viewModel.updateVisualizerSettings(it) },
            onDismiss = { viewModel.setVisualizerSettingsOpen(false) }
        )
    }
}
