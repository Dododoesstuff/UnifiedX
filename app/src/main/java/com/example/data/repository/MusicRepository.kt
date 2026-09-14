package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistTrackCrossRef
import com.example.data.local.PlaylistWithTracks
import com.example.data.local.TrackEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class MusicRepository(private val database: AppDatabase) {
    private val trackDao = database.trackDao()
    private val playlistDao = database.playlistDao()
    private val userPreferencesDao = database.userPreferencesDao()
    private val cachedPlaylistMetadataDao = database.cachedPlaylistMetadataDao()

    val allTracks: Flow<List<TrackEntity>> = trackDao.getAllTracks()
    val downloadedTracks: Flow<List<TrackEntity>> = trackDao.getDownloadedTracks()
    val likedTracks: Flow<List<TrackEntity>> = trackDao.getLikedTracks()
    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
    val userPreferences: Flow<UserPreferencesEntity?> = userPreferencesDao.getUserPreferences()
    val allCachedPlaylistMetadata: Flow<List<CachedPlaylistMetadataEntity>> = cachedPlaylistMetadataDao.getAllCachedMetadata()
    val offlinePinnedPlaylists: Flow<List<CachedPlaylistMetadataEntity>> = cachedPlaylistMetadataDao.getOfflinePinnedPlaylists()

    fun getTracksBySource(source: PlatformSource): Flow<List<TrackEntity>> =
        trackDao.getTracksBySource(source)

    fun searchTracks(query: String): Flow<List<TrackEntity>> =
        trackDao.searchTracks(query)

    fun getPlaylistWithTracks(playlistId: String): Flow<PlaylistWithTracks?> =
        playlistDao.getPlaylistWithTracks(playlistId)

    fun getCachedPlaylistMetadata(playlistId: String): Flow<CachedPlaylistMetadataEntity?> =
        cachedPlaylistMetadataDao.getCachedMetadataById(playlistId)

    suspend fun getTrackById(id: String): TrackEntity? = withContext(Dispatchers.IO) {
        trackDao.getTrackById(id)
    }

    suspend fun toggleLike(id: String, currentLiked: Boolean) = withContext(Dispatchers.IO) {
        trackDao.setLiked(id, !currentLiked)
    }

    suspend fun downloadTrack(id: String, quality: AudioQuality = AudioQuality.LOSSLESS) = withContext(Dispatchers.IO) {
        val estimatedBytes = when (quality) {
            AudioQuality.NORMAL -> 4_500_000L
            AudioQuality.HIGH -> 9_200_000L
            AudioQuality.LOSSLESS -> 19_500_000L
        }
        trackDao.setDownloaded(id, true, estimatedBytes)
        trackDao.setAudioQuality(id, quality)
    }

    suspend fun removeDownload(id: String) = withContext(Dispatchers.IO) {
        trackDao.setDownloaded(id, false, 0L)
    }

    suspend fun setTrackAudioQuality(id: String, quality: AudioQuality) = withContext(Dispatchers.IO) {
        trackDao.setAudioQuality(id, quality)
    }

    suspend fun createPlaylist(title: String, description: String): String = withContext(Dispatchers.IO) {
        val playlistId = "pl_" + UUID.randomUUID().toString().take(8)
        val newPlaylist = PlaylistEntity(
            id = playlistId,
            title = title.ifBlank { "Unified Playlist" },
            description = description.ifBlank { "Cross-platform mix containing Spotify and YouTube tracks" },
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            isUnified = true,
            isCollaborative = false,
            sessionCode = null
        )
        playlistDao.insertPlaylist(newPlaylist)
        
        // Also cache metadata for offline access
        cachedPlaylistMetadataDao.insertOrUpdateMetadata(
            CachedPlaylistMetadataEntity(
                playlistId = playlistId,
                title = newPlaylist.title,
                description = newPlaylist.description,
                coverUrl = newPlaylist.coverUrl,
                totalTrackCount = 0,
                downloadedTrackCount = 0,
                totalDurationMs = 0L,
                cachedSizeBytes = 0L,
                platformSource = PlatformSource.SPOTIFY,
                isOfflinePinned = false,
                isFullyDownloaded = false,
                lastCachedTimestamp = System.currentTimeMillis()
            )
        )
        playlistId
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: String) = withContext(Dispatchers.IO) {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = trackId,
            positionIndex = (System.currentTimeMillis() % 1000).toInt()
        )
        playlistDao.insertCrossRef(crossRef)
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) = withContext(Dispatchers.IO) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    suspend fun togglePinPlaylistOffline(playlistId: String, currentPinned: Boolean) = withContext(Dispatchers.IO) {
        cachedPlaylistMetadataDao.setOfflinePinned(playlistId, !currentPinned)
    }

    suspend fun cachePlaylistMetadata(metadata: CachedPlaylistMetadataEntity) = withContext(Dispatchers.IO) {
        cachedPlaylistMetadataDao.insertOrUpdateMetadata(metadata)
    }

    // User preferences access
    suspend fun updateOfflineListeningMode(enabled: Boolean) = withContext(Dispatchers.IO) {
        userPreferencesDao.updateOfflineMode(enabled)
    }

    suspend fun updateStreamingQuality(quality: AudioQuality) = withContext(Dispatchers.IO) {
        userPreferencesDao.updateStreamingQuality(quality.name)
    }

    suspend fun updateDownloadQuality(quality: AudioQuality) = withContext(Dispatchers.IO) {
        userPreferencesDao.updateDownloadQuality(quality.name)
    }

    suspend fun updateEqualizerPreset(preset: String) = withContext(Dispatchers.IO) {
        userPreferencesDao.updateEqualizerPreset(preset)
    }

    suspend fun updateApiKeys(spotifyToken: String, youtubeApiKey: String) = withContext(Dispatchers.IO) {
        userPreferencesDao.updateSpotifyConnection(spotifyToken, spotifyToken.isNotBlank())
        userPreferencesDao.updateYoutubeConnection(youtubeApiKey, youtubeApiKey.isNotBlank())
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        if (trackDao.getTrackCount() == 0 || userPreferencesDao.getPreferencesCount() == 0) {
            database.seedInitialData()
        }
    }

    suspend fun insertCustomTrack(track: TrackEntity) = withContext(Dispatchers.IO) {
        trackDao.insertTrack(track)
    }

    suspend fun updateSyncTimestamp(timestamp: Long) = withContext(Dispatchers.IO) {
        trackDao.updateAllSyncTimestamp(timestamp)
        userPreferencesDao.updateLastSync(timestamp)
    }
}
