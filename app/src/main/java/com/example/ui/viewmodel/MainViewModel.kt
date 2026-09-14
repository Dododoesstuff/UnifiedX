package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.collab.CollaborativeSessionManager
import com.example.data.local.AppDatabase
import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistWithTracks
import com.example.data.local.TrackEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.AudioQuality
import com.example.data.model.DownloadStatus
import com.example.data.model.PlatformSource
import com.example.data.model.TrackDownloadState
import com.example.data.repository.MusicRepository
import com.example.player.AudioPlayerHolder
import com.example.player.AudioPlayerManager
import com.example.player.EqPreset
import com.example.player.MediaPlaybackService
import com.example.sync.CrossPlatformSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    HOME("Discover"),
    SEARCH("Search"),
    LIBRARY("Library"),
    JAM("Collab Jam"),
    SETTINGS("Settings")
}

data class ArtistSpotlight(
    val name: String,
    val platform: PlatformSource,
    val listeners: String,
    val imageUrl: String,
    val genre: String,
    val isRising: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = MusicRepository(database)
    val playerManager = AudioPlayerManager(application)
    val syncManager = CrossPlatformSyncManager(repository, viewModelScope)
    val collabManager = CollaborativeSessionManager(application)
    val waveformEngine = com.example.visualizer.AudioWaveformEngine(application, viewModelScope)
    val seamlessMusicApi = com.example.data.engine.SeamlessUnifiedMusicApi(repository)

    // Secure OAuth 2.0 repository and EncryptedSharedPreferences storage
    val secureOAuthStorage: com.example.data.local.security.SecureOAuthStorage =
        com.example.data.local.security.EncryptedOAuthStorage(application)
    val oauthRepository: com.example.data.repository.oauth.OAuthRepository =
        com.example.data.repository.oauth.OAuthRepositoryImpl(
            secureStorage = secureOAuthStorage,
            userPreferencesDao = database.userPreferencesDao(),
            externalScope = viewModelScope
        )

    val spotifyOAuthState = oauthRepository.spotifyAuthState
    val youtubeOAuthState = oauthRepository.youtubeAuthState

    private val _savedSpotifyAccounts = MutableStateFlow<List<com.example.data.local.security.SavedAccountRecord>>(emptyList())
    val savedSpotifyAccounts: StateFlow<List<com.example.data.local.security.SavedAccountRecord>> = _savedSpotifyAccounts.asStateFlow()

    private val _savedYouTubeAccounts = MutableStateFlow<List<com.example.data.local.security.SavedAccountRecord>>(emptyList())
    val savedYouTubeAccounts: StateFlow<List<com.example.data.local.security.SavedAccountRecord>> = _savedYouTubeAccounts.asStateFlow()

    fun refreshSavedAccounts() {
        _savedSpotifyAccounts.value = oauthRepository.getSavedAccounts(com.example.data.model.oauth.OAuthPlatform.SPOTIFY)
        _savedYouTubeAccounts.value = oauthRepository.getSavedAccounts(com.example.data.model.oauth.OAuthPlatform.YOUTUBE)
    }

    val playerUiState = playerManager.uiState
    val syncUiState = syncManager.syncState
    val collabUiState = collabManager.sessionState
    val visualizerSettings = waveformEngine.settings
    val visualizerFrequencies = waveformEngine.rawFrequencies

    // Navigation state
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _isLoginScreenOpen = MutableStateFlow(false)
    val isLoginScreenOpen: StateFlow<Boolean> = _isLoginScreenOpen.asStateFlow()

    private val _isFullScreenPlayerOpen = MutableStateFlow(false)
    val isFullScreenPlayerOpen: StateFlow<Boolean> = _isFullScreenPlayerOpen.asStateFlow()

    private val _isLyricsViewOpen = MutableStateFlow(false)
    val isLyricsViewOpen: StateFlow<Boolean> = _isLyricsViewOpen.asStateFlow()

    private val _isVisualizerSettingsOpen = MutableStateFlow(false)
    val isVisualizerSettingsOpen: StateFlow<Boolean> = _isVisualizerSettingsOpen.asStateFlow()

    private val _trackToAddToPlaylist = MutableStateFlow<TrackEntity?>(null)
    val trackToAddToPlaylist: StateFlow<TrackEntity?> = _trackToAddToPlaylist.asStateFlow()

    private val _trackToShare = MutableStateFlow<TrackEntity?>(null)
    val trackToShare: StateFlow<TrackEntity?> = _trackToShare.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<PlaylistWithTracks?>(null)
    val selectedPlaylist: StateFlow<PlaylistWithTracks?> = _selectedPlaylist.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<String, TrackDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, TrackDownloadState>> = _downloadStates.asStateFlow()

    // Data streams
    val allTracks: StateFlow<List<TrackEntity>> = repository.allTracks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val downloadedTracks: StateFlow<List<TrackEntity>> = repository.downloadedTracks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val likedTracks: StateFlow<List<TrackEntity>> = repository.likedTracks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playlists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userPreferences: StateFlow<UserPreferencesEntity?> = repository.userPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val cachedPlaylists: StateFlow<List<CachedPlaylistMetadataEntity>> = repository.allCachedPlaylistMetadata.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val offlinePinnedPlaylists: StateFlow<List<CachedPlaylistMetadataEntity>> = repository.offlinePinnedPlaylists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow("ALL") // ALL, SPOTIFY, YOUTUBE, DOWNLOADED
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    val searchResults: StateFlow<List<TrackEntity>> = combine(
        allTracks,
        _searchQuery,
        _searchFilter
    ) { tracks, query, filter ->
        val filteredByQuery = if (query.isBlank()) {
            tracks
        } else {
            tracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
            }
        }
        when (filter) {
            "SPOTIFY" -> filteredByQuery.filter { it.platformSource == PlatformSource.SPOTIFY }
            "YOUTUBE" -> filteredByQuery.filter { it.platformSource == PlatformSource.YOUTUBE }
            "UNIFIED" -> filteredByQuery.filter { it.spotifyEquivalentId != null || it.youtubeEquivalentId != null }
            "DOWNLOADED" -> filteredByQuery.filter { it.isDownloaded }
            else -> filteredByQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Curated discovery artists
    val discoveryArtists = listOf(
        ArtistSpotlight(
            name = "Aura Nova",
            platform = PlatformSource.SPOTIFY,
            listeners = "1.4M monthly listeners",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            genre = "Electronic / Synth"
        ),
        ArtistSpotlight(
            name = "Kavinsky Wave",
            platform = PlatformSource.YOUTUBE,
            listeners = "890K subscribers",
            imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            genre = "Live Studio Synthwave"
        ),
        ArtistSpotlight(
            name = "Maya Lin",
            platform = PlatformSource.SPOTIFY,
            listeners = "2.1M monthly listeners",
            imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            genre = "Acoustic Pop"
        ),
        ArtistSpotlight(
            name = "Chilled Cow Studio",
            platform = PlatformSource.YOUTUBE,
            listeners = "14.2M subscribers",
            imageUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80",
            genre = "Lo-Fi Beats"
        )
    )

    init {
        AudioPlayerHolder.playerManager = playerManager
        viewModelScope.launch {
            repository.ensureSeeded()
            refreshSavedAccounts()
        }
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                prefs?.let {
                    playerManager.setOfflineModeOnly(it.isOfflineModeOnly)
                    playerManager.setStreamingQuality(it.streamingQuality)
                    playerManager.setAutoCrossfade(it.autoCrossfade)
                    playerManager.setCrossfadeDuration(it.crossfadeSeconds)
                }
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setLoginScreenOpen(isOpen: Boolean) {
        _isLoginScreenOpen.value = isOpen
    }

    fun setFullScreenPlayerOpen(isOpen: Boolean) {
        _isFullScreenPlayerOpen.value = isOpen
    }

    fun setLyricsViewOpen(isOpen: Boolean) {
        _isLyricsViewOpen.value = isOpen
    }

    fun setVisualizerSettingsOpen(isOpen: Boolean) {
        _isVisualizerSettingsOpen.value = isOpen
    }

    fun updateVisualizerSettings(settings: com.example.visualizer.VisualizerSettings) {
        waveformEngine.updateSettings(settings)
    }

    fun openAddToPlaylistDialog(track: TrackEntity) {
        _trackToAddToPlaylist.value = track
    }

    fun closeAddToPlaylistDialog() {
        _trackToAddToPlaylist.value = null
    }

    fun openSocialShareDialog(track: TrackEntity) {
        _trackToShare.value = track
    }

    fun closeSocialShareDialog() {
        _trackToShare.value = null
    }

    fun openPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            repository.getPlaylistWithTracks(playlist.id).collect { pw ->
                _selectedPlaylist.value = pw
            }
        }
    }

    fun closePlaylistDetails() {
        _selectedPlaylist.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && query.length >= 2) {
            viewModelScope.launch {
                val platform = when (_searchFilter.value) {
                    "SPOTIFY" -> PlatformSource.SPOTIFY
                    "YOUTUBE" -> PlatformSource.YOUTUBE
                    else -> null
                }
                seamlessMusicApi.searchUnified(query, platform, playerUiState.value.streamingQuality)
            }
        }
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
        if (_searchQuery.value.isNotBlank()) {
            viewModelScope.launch {
                val platform = when (filter) {
                    "SPOTIFY" -> PlatformSource.SPOTIFY
                    "YOUTUBE" -> PlatformSource.YOUTUBE
                    else -> null
                }
                seamlessMusicApi.searchUnified(_searchQuery.value, platform, playerUiState.value.streamingQuality)
            }
        }
    }

    fun switchPlatformCounterpart(track: TrackEntity) {
        viewModelScope.launch {
            val counterpart = seamlessMusicApi.resolveCrossPlatformCounterpart(track)
            playTrack(counterpart)
        }
    }

    // Player delegates
    fun playTrack(track: TrackEntity, queue: List<TrackEntity> = emptyList()) {
        val tracksQueue = if (queue.isNotEmpty()) queue else allTracks.value
        try {
            MediaPlaybackService.startService(getApplication())
        } catch (e: Exception) {
            // Service start exception safety
        }
        playerManager.playTrack(track, tracksQueue)
    }

    fun togglePlayPause() = playerManager.togglePlayPause()
    fun seekTo(positionMs: Long) = playerManager.seekTo(positionMs)
    fun skipNext() = playerManager.skipNext()
    fun skipPrevious() = playerManager.skipPrevious()
    fun toggleShuffle() = playerManager.toggleShuffle()
    fun toggleRepeat() = playerManager.toggleRepeat()
    fun setEqPreset(preset: EqPreset) = playerManager.setEqPreset(preset)
    fun setStreamingQuality(quality: AudioQuality) = playerManager.setStreamingQuality(quality)
    fun setOfflineModeOnly(enabled: Boolean) = playerManager.setOfflineModeOnly(enabled)
    fun clearPlayerError() = playerManager.clearError()
    fun reloadLyrics() = playerManager.reloadLyrics()

    // Library actions
    fun toggleLike(track: TrackEntity) {
        viewModelScope.launch {
            val newLiked = !track.isLiked
            repository.toggleLike(track.id, track.isLiked)
            playerManager.updateLikedState(track.id, newLiked)
            seamlessMusicApi.syncLikeToPlatform(track, newLiked)
        }
    }

    fun getDownloadStateForTrack(track: TrackEntity): TrackDownloadState {
        val mapped = _downloadStates.value[track.id]
        if (mapped != null) return mapped
        return if (track.isDownloaded) {
            TrackDownloadState(status = DownloadStatus.DOWNLOADED, progressPercent = 100)
        } else {
            TrackDownloadState(status = DownloadStatus.NOT_DOWNLOADED, progressPercent = 0)
        }
    }

    fun toggleDownload(track: TrackEntity) {
        val currentStatus = getDownloadStateForTrack(track).status
        viewModelScope.launch {
            if (currentStatus == DownloadStatus.DOWNLOADED) {
                repository.removeDownload(track.id)
                playerManager.updateDownloadedState(track.id, false)
                _downloadStates.value = _downloadStates.value - track.id
            } else if (currentStatus == DownloadStatus.DOWNLOADING || currentStatus == DownloadStatus.PENDING) {
                _downloadStates.value = _downloadStates.value + (track.id to TrackDownloadState(status = DownloadStatus.NOT_DOWNLOADED))
            } else {
                startDownloadFlow(track)
            }
        }
    }

    fun retryDownload(track: TrackEntity) {
        viewModelScope.launch {
            startDownloadFlow(track)
        }
    }

    fun retryAllFailedDownloads() {
        viewModelScope.launch {
            val failedTrackIds = _downloadStates.value.filter { it.value.status == DownloadStatus.ERROR }.keys
            allTracks.value.filter { it.id in failedTrackIds }.forEach { track ->
                launch { startDownloadFlow(track) }
            }
        }
    }

    fun simulateDownloadError(track: TrackEntity) {
        _downloadStates.value = _downloadStates.value + (track.id to TrackDownloadState(
            status = DownloadStatus.ERROR,
            progressPercent = 35,
            errorMessage = "Network timeout downloading high-fidelity stream"
        ))
    }

    private suspend fun startDownloadFlow(track: TrackEntity) {
        _downloadStates.value = _downloadStates.value + (track.id to TrackDownloadState(
            status = DownloadStatus.PENDING,
            progressPercent = 0
        ))
        kotlinx.coroutines.delay(400)

        val progressSteps = listOf(20, 50, 85, 100)
        for (p in progressSteps) {
            _downloadStates.value = _downloadStates.value + (track.id to TrackDownloadState(
                status = DownloadStatus.DOWNLOADING,
                progressPercent = p
            ))
            kotlinx.coroutines.delay(300)
        }

        repository.downloadTrack(track.id, playerUiState.value.streamingQuality)
        playerManager.updateDownloadedState(track.id, true)
        _downloadStates.value = _downloadStates.value + (track.id to TrackDownloadState(
            status = DownloadStatus.DOWNLOADED,
            progressPercent = 100
        ))
    }

    fun createUnifiedPlaylist(title: String, description: String) {
        viewModelScope.launch {
            val id = repository.createPlaylist(title, description)
            trackToAddToPlaylist.value?.let {
                repository.addTrackToPlaylist(id, it.id)
                closeAddToPlaylistDialog()
            }
        }
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, trackId)
            closeAddToPlaylistDialog()
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            if (_selectedPlaylist.value?.playlist?.id == playlistId) {
                _selectedPlaylist.value = null
            }
        }
    }

    // Cross-platform sync & accounts
    fun startSync() {
        syncManager.startCrossPlatformSync()
    }

    fun updateAccountCredential(source: PlatformSource, token: String, username: String) {
        syncManager.updateAccountToken(source, token, username)
        val oauthPlatform = when (source) {
            PlatformSource.SPOTIFY -> com.example.data.model.oauth.OAuthPlatform.SPOTIFY
            PlatformSource.YOUTUBE -> com.example.data.model.oauth.OAuthPlatform.YOUTUBE
            PlatformSource.LOCAL -> null
        }
        if (oauthPlatform != null && token.isNotBlank()) {
            viewModelScope.launch {
                oauthRepository.saveDirectAccessToken(oauthPlatform, token, username)
            }
        }
    }

    fun buildOAuthUrl(
        platform: com.example.data.model.oauth.OAuthPlatform,
        clientId: String? = null,
        redirectUri: String? = null
    ): String {
        return oauthRepository.buildAuthorizationUrl(platform, clientId, redirectUri)
    }

    fun exchangeOAuthCode(
        platform: com.example.data.model.oauth.OAuthPlatform,
        code: String,
        redirectUri: String? = null,
        clientId: String? = null,
        clientSecret: String? = null
    ) {
        viewModelScope.launch {
            val result = oauthRepository.exchangeAuthorizationCode(platform, code, redirectUri, clientId, clientSecret)
            if (result.isSuccess) {
                val token = result.getOrThrow()
                syncManager.updateAccountToken(platform.platformSource, token.accessToken, platform.displayName)
            }
        }
    }

    fun refreshOAuthToken(platform: com.example.data.model.oauth.OAuthPlatform) {
        viewModelScope.launch {
            val result = oauthRepository.refreshAccessToken(platform, force = true)
            if (result.isSuccess) {
                val token = result.getOrThrow()
                syncManager.updateAccountToken(platform.platformSource, token.accessToken, platform.displayName)
            }
        }
    }

    fun signOutOAuth(platform: com.example.data.model.oauth.OAuthPlatform) {
        viewModelScope.launch {
            oauthRepository.signOut(platform)
            syncManager.disconnectService(platform.platformSource)
            refreshSavedAccounts()
        }
    }

    fun signOutAccount(platformSource: PlatformSource) {
        val platform = if (platformSource == PlatformSource.SPOTIFY) {
            com.example.data.model.oauth.OAuthPlatform.SPOTIFY
        } else {
            com.example.data.model.oauth.OAuthPlatform.YOUTUBE
        }
        signOutOAuth(platform)
    }

    fun loginWithEmail(
        platform: com.example.data.model.oauth.OAuthPlatform,
        email: String,
        password: String,
        displayName: String? = null,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val result = oauthRepository.loginWithAccountEmail(platform, email, password, displayName)
            if (result.isSuccess) {
                val profile = result.getOrThrow()
                syncManager.updateAccountToken(
                    platform.platformSource,
                    "sec_session_${platform.name.lowercase()}_${profile.id.hashCode()}",
                    profile.displayName
                )
                refreshSavedAccounts()
                onResult(true, null)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Failed to log in"
                onResult(false, errorMsg)
            }
        }
    }

    fun loginWithEmail(
        platformSource: PlatformSource,
        email: String,
        password: String,
        displayName: String? = null,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val platform = if (platformSource == PlatformSource.SPOTIFY) {
            com.example.data.model.oauth.OAuthPlatform.SPOTIFY
        } else {
            com.example.data.model.oauth.OAuthPlatform.YOUTUBE
        }
        loginWithEmail(platform, email, password, displayName, onResult)
    }

    fun switchAccount(
        platform: com.example.data.model.oauth.OAuthPlatform,
        email: String,
        displayName: String? = null
    ) {
        viewModelScope.launch {
            val result = oauthRepository.switchAccount(platform, email, displayName)
            if (result.isSuccess) {
                val profile = result.getOrThrow()
                syncManager.updateAccountToken(
                    platform.platformSource,
                    "sec_session_${platform.name.lowercase()}_${profile.id.hashCode()}",
                    profile.displayName
                )
                refreshSavedAccounts()
            }
        }
    }

    fun switchAccount(
        platformSource: PlatformSource,
        email: String,
        displayName: String? = null
    ) {
        val platform = if (platformSource == PlatformSource.SPOTIFY) {
            com.example.data.model.oauth.OAuthPlatform.SPOTIFY
        } else {
            com.example.data.model.oauth.OAuthPlatform.YOUTUBE
        }
        switchAccount(platform, email, displayName)
    }

    fun getSavedAccounts(platform: com.example.data.model.oauth.OAuthPlatform): List<com.example.data.local.security.SavedAccountRecord> {
        return oauthRepository.getSavedAccounts(platform)
    }

    fun removeSavedAccount(platform: com.example.data.model.oauth.OAuthPlatform, email: String) {
        oauthRepository.removeSavedAccount(platform, email)
        refreshSavedAccounts()
    }

    fun removeSavedAccount(platformSource: PlatformSource, email: String) {
        val platform = if (platformSource == PlatformSource.SPOTIFY) {
            com.example.data.model.oauth.OAuthPlatform.SPOTIFY
        } else {
            com.example.data.model.oauth.OAuthPlatform.YOUTUBE
        }
        removeSavedAccount(platform, email)
    }

    fun transferPlaylist(
        sourcePlatform: PlatformSource,
        targetPlatform: PlatformSource,
        playlistTitle: String,
        tracks: List<TrackEntity>
    ) {
        viewModelScope.launch {
            syncManager.transferPlaylist(sourcePlatform, targetPlatform, playlistTitle, tracks)
        }
    }

    fun transferLikedSongs(sourcePlatform: PlatformSource, targetPlatform: PlatformSource) {
        viewModelScope.launch {
            syncManager.transferLikedSongs(sourcePlatform, targetPlatform)
        }
    }

    fun disconnectService(platform: PlatformSource) {
        syncManager.disconnectService(platform)
    }

    // User preferences & offline access actions
    fun toggleOfflineListeningMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateOfflineListeningMode(enabled)
            playerManager.setOfflineModeOnly(enabled)
        }
    }

    fun updateStreamingQuality(quality: AudioQuality) {
        viewModelScope.launch {
            repository.updateStreamingQuality(quality)
            playerManager.setStreamingQuality(quality)
        }
    }

    fun updateEqualizerPreset(preset: EqPreset) {
        viewModelScope.launch {
            repository.updateEqualizerPreset(preset.label)
            playerManager.setEqPreset(preset)
        }
    }

    fun updateCrossfadeSettings(autoCrossfade: Boolean, seconds: Int) {
        viewModelScope.launch {
            repository.updateCrossfadeSettings(autoCrossfade, seconds)
            playerManager.setAutoCrossfade(autoCrossfade)
            playerManager.setCrossfadeDuration(seconds)
        }
    }

    fun updateApiKeys(spotifyToken: String, youtubeApiKey: String) {
        viewModelScope.launch {
            repository.updateApiKeys(spotifyToken, youtubeApiKey)
        }
    }

    fun togglePinPlaylistOffline(playlistId: String, currentPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinPlaylistOffline(playlistId, currentPinned)
        }
    }

    // Collab actions
    fun hostCollabSession(name: String) = collabManager.hostNewSession(name)
    fun joinCollabSession(code: String) = collabManager.joinSession(code)
    fun leaveCollabSession() = collabManager.leaveSession()
    fun addTrackToCollabQueue(track: TrackEntity) = collabManager.addTrackToLiveQueue(track)
    fun upvoteCollabTrack(itemId: String) = collabManager.toggleUpvote(itemId)
    fun shareCollabInvite() = collabManager.shareSessionInvite()
    fun shareTrackSocially(track: TrackEntity, lyricSnippet: String? = null) =
        collabManager.shareTrackSocially(track, lyricSnippet)

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
