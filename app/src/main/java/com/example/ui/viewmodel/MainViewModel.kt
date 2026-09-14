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
import com.example.data.model.PlatformSource
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

    val playerUiState = playerManager.uiState
    val syncUiState = syncManager.syncState
    val collabUiState = collabManager.sessionState

    // Navigation state
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _isFullScreenPlayerOpen = MutableStateFlow(false)
    val isFullScreenPlayerOpen: StateFlow<Boolean> = _isFullScreenPlayerOpen.asStateFlow()

    private val _isLyricsViewOpen = MutableStateFlow(false)
    val isLyricsViewOpen: StateFlow<Boolean> = _isLyricsViewOpen.asStateFlow()

    private val _trackToAddToPlaylist = MutableStateFlow<TrackEntity?>(null)
    val trackToAddToPlaylist: StateFlow<TrackEntity?> = _trackToAddToPlaylist.asStateFlow()

    private val _trackToShare = MutableStateFlow<TrackEntity?>(null)
    val trackToShare: StateFlow<TrackEntity?> = _trackToShare.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<PlaylistWithTracks?>(null)
    val selectedPlaylist: StateFlow<PlaylistWithTracks?> = _selectedPlaylist.asStateFlow()

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
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setFullScreenPlayerOpen(isOpen: Boolean) {
        _isFullScreenPlayerOpen.value = isOpen
    }

    fun setLyricsViewOpen(isOpen: Boolean) {
        _isLyricsViewOpen.value = isOpen
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
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
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
            repository.toggleLike(track.id, track.isLiked)
        }
    }

    fun toggleDownload(track: TrackEntity) {
        viewModelScope.launch {
            if (track.isDownloaded) {
                repository.removeDownload(track.id)
            } else {
                repository.downloadTrack(track.id, playerUiState.value.streamingQuality)
            }
        }
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

    // Cross-platform sync
    fun startSync() {
        syncManager.startCrossPlatformSync()
    }

    fun updateAccountCredential(source: PlatformSource, token: String, username: String) {
        syncManager.updateAccountToken(source, token, username)
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
