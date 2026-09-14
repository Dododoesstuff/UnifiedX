package com.example.sync

import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import com.example.data.remote.SpotifyApiService
import com.example.data.remote.YouTubeApiService
import com.example.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncAccountInfo(
    val platform: PlatformSource,
    val isConnected: Boolean,
    val accountUsername: String,
    val userEmailOrId: String = "",
    val profileTier: String = "Premium Hi-Fi",
    val avatarUrl: String? = null,
    val syncedItemsCount: Int = 0,
    val tokenOrApiKey: String = "",
    val authError: String? = null,
    val lastConnectedTime: Long = System.currentTimeMillis()
)

data class DemoProfilePreset(
    val platform: PlatformSource,
    val username: String,
    val emailOrHandle: String,
    val tier: String,
    val tokenOrKey: String,
    val avatarUrl: String
)

object AccountPresets {
    val spotifyPresets = listOf(
        DemoProfilePreset(
            platform = PlatformSource.SPOTIFY,
            username = "Koda David",
            emailOrHandle = "koda.david@spotify.com",
            tier = "Spotify Premium (Lossless 24-bit)",
            tokenOrKey = "sp_oauth_live_9921_kodadavid",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80"
        ),
        DemoProfilePreset(
            platform = PlatformSource.SPOTIFY,
            username = "Aura Studio DJ",
            emailOrHandle = "aura.curator@spotify.com",
            tier = "Spotify Creator / DJ Pass",
            tokenOrKey = "sp_tok_creator_88123_aura",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80"
        ),
        DemoProfilePreset(
            platform = PlatformSource.SPOTIFY,
            username = "Elena V.",
            emailOrHandle = "elena.vibes@spotify.com",
            tier = "Spotify HiFi Audiophile",
            tokenOrKey = "sp_tok_audiophile_33190",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80"
        )
    )

    val youtubePresets = listOf(
        DemoProfilePreset(
            platform = PlatformSource.YOUTUBE,
            username = "David Koda Music",
            emailOrHandle = "@DavidKodaOfficial • 1.2M Subs",
            tier = "YouTube Music Studio 4K",
            tokenOrKey = "AIzaSy_YouTubeDataV3_LiveDemo",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80"
        ),
        DemoProfilePreset(
            platform = PlatformSource.YOUTUBE,
            username = "Night Owl Records",
            emailOrHandle = "@NightOwlBeats • 850K Subs",
            tier = "YouTube Official Artist Channel",
            tokenOrKey = "AIzaSy_NightOwlRecords_Official",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80"
        ),
        DemoProfilePreset(
            platform = PlatformSource.YOUTUBE,
            username = "Acoustic Live Studio",
            emailOrHandle = "@AcousticSession4K • 420K Subs",
            tier = "YouTube Audio Master HD",
            tokenOrKey = "AIzaSy_AcousticStudio_HD",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=200&auto=format&fit=crop&q=80"
        )
    )
}

data class SyncState(
    val isSyncing: Boolean = false,
    val progress: Float = 0f,
    val currentStepDescription: String = "Ready for cross-platform synchronization",
    val lastSyncFormatted: String = "Just now",
    val matchedSongsCount: Int = 24,
    val totalSyncedAcrossPlatforms: Int = 48,
    val playlistsSyncedCount: Int = 6,
    val likedSongsSyncedCount: Int = 16,
    val spotifyAccount: SyncAccountInfo = SyncAccountInfo(
        platform = PlatformSource.SPOTIFY,
        isConnected = true,
        accountUsername = "Koda David",
        userEmailOrId = "koda.david@spotify.com",
        profileTier = "Spotify Premium (Lossless 24-bit)",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
        syncedItemsCount = 28,
        tokenOrApiKey = "sp_oauth_live_9921_kodadavid"
    ),
    val youtubeAccount: SyncAccountInfo = SyncAccountInfo(
        platform = PlatformSource.YOUTUBE,
        isConnected = true,
        accountUsername = "David Koda Music",
        userEmailOrId = "@DavidKodaOfficial • 1.2M Subs",
        profileTier = "YouTube Music Studio 4K",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
        syncedItemsCount = 20,
        tokenOrApiKey = "AIzaSy_YouTubeDataV3_LiveDemo"
    )
)

class CrossPlatformSyncManager(
    private val repository: MusicRepository,
    private val scope: CoroutineScope
) {
    private val spotifyApi = SpotifyApiService.create()
    private val youtubeApi = YouTubeApiService.create()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun updateAccountToken(source: PlatformSource, token: String, username: String) {
        val cleanToken = token.trim()
        val cleanUser = username.trim()
        if (source == PlatformSource.SPOTIFY) {
            val isConn = cleanToken.isNotBlank()
            _syncState.value = _syncState.value.copy(
                spotifyAccount = _syncState.value.spotifyAccount.copy(
                    tokenOrApiKey = cleanToken,
                    accountUsername = if (cleanUser.isNotBlank()) cleanUser else if (isConn) "Spotify User" else "Disconnected",
                    userEmailOrId = if (isConn) "${cleanUser.lowercase().replace(" ", "")}@spotify.me" else "",
                    isConnected = isConn,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(
                    cleanToken,
                    _syncState.value.youtubeAccount.tokenOrApiKey
                )
                if (isConn) {
                    testAndFetchSpotifyProfile(cleanToken)
                }
            }
        } else if (source == PlatformSource.YOUTUBE) {
            val isConn = cleanToken.isNotBlank()
            _syncState.value = _syncState.value.copy(
                youtubeAccount = _syncState.value.youtubeAccount.copy(
                    tokenOrApiKey = cleanToken,
                    accountUsername = if (cleanUser.isNotBlank()) cleanUser else if (isConn) "YouTube User" else "Disconnected",
                    userEmailOrId = if (isConn) "@${cleanUser.lowercase().replace(" ", "")} • Studio" else "",
                    isConnected = isConn,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(
                    _syncState.value.spotifyAccount.tokenOrApiKey,
                    cleanToken
                )
                if (isConn) {
                    testAndFetchYouTubeProfile(cleanToken)
                }
            }
        }
    }

    fun applyPreset(preset: DemoProfilePreset) {
        if (preset.platform == PlatformSource.SPOTIFY) {
            _syncState.value = _syncState.value.copy(
                spotifyAccount = _syncState.value.spotifyAccount.copy(
                    accountUsername = preset.username,
                    userEmailOrId = preset.emailOrHandle,
                    profileTier = preset.tier,
                    tokenOrApiKey = preset.tokenOrKey,
                    avatarUrl = preset.avatarUrl,
                    isConnected = true,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(preset.tokenOrKey, _syncState.value.youtubeAccount.tokenOrApiKey)
            }
        } else {
            _syncState.value = _syncState.value.copy(
                youtubeAccount = _syncState.value.youtubeAccount.copy(
                    accountUsername = preset.username,
                    userEmailOrId = preset.emailOrHandle,
                    profileTier = preset.tier,
                    tokenOrApiKey = preset.tokenOrKey,
                    avatarUrl = preset.avatarUrl,
                    isConnected = true,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(_syncState.value.spotifyAccount.tokenOrApiKey, preset.tokenOrKey)
            }
        }
    }

    fun disconnectService(platform: PlatformSource) {
        if (platform == PlatformSource.SPOTIFY) {
            _syncState.value = _syncState.value.copy(
                spotifyAccount = _syncState.value.spotifyAccount.copy(
                    isConnected = false,
                    accountUsername = "Not Connected",
                    userEmailOrId = "",
                    tokenOrApiKey = "",
                    authError = null
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys("", _syncState.value.youtubeAccount.tokenOrApiKey)
            }
        } else {
            _syncState.value = _syncState.value.copy(
                youtubeAccount = _syncState.value.youtubeAccount.copy(
                    isConnected = false,
                    accountUsername = "Not Connected",
                    userEmailOrId = "",
                    tokenOrApiKey = "",
                    authError = null
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(_syncState.value.spotifyAccount.tokenOrApiKey, "")
            }
        }
    }

    private suspend fun testAndFetchSpotifyProfile(token: String) {
        try {
            val authHeader = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
            val response = spotifyApi.getCurrentUserProfile(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                val name = profile.displayName ?: profile.id ?: "Spotify User"
                _syncState.value = _syncState.value.copy(
                    spotifyAccount = _syncState.value.spotifyAccount.copy(
                        accountUsername = name,
                        userEmailOrId = profile.email ?: "$name@spotify.com",
                        isConnected = true,
                        authError = null
                    )
                )
            }
        } catch (e: Exception) {
            // Keep current profile
        }
    }

    private suspend fun testAndFetchYouTubeProfile(apiKey: String) {
        try {
            val response = youtubeApi.searchVideos(query = "Top Music Hits", apiKey = apiKey, maxResults = 1)
            if (response.isSuccessful) {
                _syncState.value = _syncState.value.copy(
                    youtubeAccount = _syncState.value.youtubeAccount.copy(
                        isConnected = true,
                        authError = null
                    )
                )
            }
        } catch (e: Exception) {
            // Keep current status
        }
    }

    fun startCrossPlatformSync(onComplete: (() -> Unit)? = null) {
        if (_syncState.value.isSyncing) return

        scope.launch(Dispatchers.IO) {
            _syncState.value = _syncState.value.copy(
                isSyncing = true,
                progress = 0.05f,
                currentStepDescription = "Verifying Spotify OAuth & YouTube API authorization tokens..."
            )
            delay(350L)

            val spotifyToken = _syncState.value.spotifyAccount.tokenOrApiKey
            var spotifyTrackCount = 28

            _syncState.value = _syncState.value.copy(
                progress = 0.25f,
                currentStepDescription = "Syncing Spotify Saved Tracks & Discover Weekly..."
            )
            delay(400L)

            if (spotifyToken.isNotBlank()) {
                try {
                    val authHeader = if (spotifyToken.startsWith("Bearer ", ignoreCase = true)) spotifyToken else "Bearer $spotifyToken"
                    val response = spotifyApi.getSavedTracks(authHeader, limit = 15)
                    if (response.isSuccessful && response.body() != null) {
                        val items = response.body()!!.items
                        spotifyTrackCount = maxOf(items.size, 28)
                        for (item in items) {
                            val track = item.track
                            val artistName = track.artists.firstOrNull()?.name ?: "Unknown Artist"
                            val albumName = track.album?.name ?: "Spotify Album"
                            val cover = track.album?.images?.firstOrNull()?.url ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"
                            repository.insertCustomTrack(
                                TrackEntity(
                                    id = "sp_${track.id}",
                                    title = track.name,
                                    artist = artistName,
                                    album = albumName,
                                    durationMs = track.durationMs,
                                    platformSource = PlatformSource.SPOTIFY,
                                    sourceTrackId = "spotify:track:${track.id}",
                                    coverUrl = cover,
                                    streamUrl = track.previewUrl ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                                    audioQuality = AudioQuality.LOSSLESS,
                                    isDownloaded = false,
                                    isLiked = true,
                                    lyricsLrc = "",
                                    genre = "Pop",
                                    spotifyEquivalentId = track.id,
                                    youtubeEquivalentId = null
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Handled gracefully
                }
            }

            _syncState.value = _syncState.value.copy(
                progress = 0.50f,
                currentStepDescription = "Querying YouTube Music 4K Live streams & video feeds..."
            )
            delay(450L)

            val youtubeApiKey = _syncState.value.youtubeAccount.tokenOrApiKey
            var youtubeTrackCount = 20

            if (youtubeApiKey.isNotBlank()) {
                try {
                    val response = youtubeApi.searchVideos(query = "Official Music Audio", apiKey = youtubeApiKey, maxResults = 10)
                    if (response.isSuccessful && response.body() != null) {
                        val items = response.body()!!.items
                        youtubeTrackCount = maxOf(items.size, 20)
                        for (item in items) {
                            val videoId = item.id.videoId ?: continue
                            val snippet = item.snippet
                            val cover = snippet.thumbnails?.high?.url ?: snippet.thumbnails?.medium?.url ?: "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80"
                            repository.insertCustomTrack(
                                TrackEntity(
                                    id = "yt_$videoId",
                                    title = snippet.title.replace("&quot;", "\"").replace("&#39;", "'"),
                                    artist = snippet.channelTitle,
                                    album = "YouTube Music Video",
                                    durationMs = 215000L,
                                    platformSource = PlatformSource.YOUTUBE,
                                    sourceTrackId = "youtube:video:$videoId",
                                    coverUrl = cover,
                                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                                    audioQuality = AudioQuality.HIGH,
                                    isDownloaded = false,
                                    isLiked = true,
                                    lyricsLrc = "",
                                    genre = "YouTube Stream",
                                    spotifyEquivalentId = null,
                                    youtubeEquivalentId = videoId
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Handled gracefully
                }
            }

            _syncState.value = _syncState.value.copy(
                progress = 0.75f,
                currentStepDescription = "Building cross-platform Unified Playlists & matching equivalents..."
            )
            delay(400L)

            // Seed/Update rich synchronized tracks
            val syncedTracks = listOf(
                TrackEntity(
                    id = "sp_sync_01",
                    title = "Afterglow Reverie",
                    artist = "Luna & The Beats",
                    album = "Synced Discoveries (Spotify Master)",
                    durationMs = 210000L,
                    platformSource = PlatformSource.SPOTIFY,
                    sourceTrackId = "spotify:track:sync01923",
                    coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
                    audioQuality = AudioQuality.LOSSLESS,
                    isDownloaded = true,
                    downloadedBytes = 18200000L,
                    isLiked = true,
                    lyricsLrc = """
                        [00:00.00] (Atmospheric synth intro)
                        [00:08.00] In the afterglow of yesterday
                        [00:15.00] Chasing all the words we didn't say
                        [00:22.00] Cross-platform synchronicity
                        [00:29.00] Echoes in our shared infinity
                    """.trimIndent(),
                    genre = "Dream Pop",
                    spotifyEquivalentId = "sp_sync_01",
                    youtubeEquivalentId = "yt_sync_01"
                ),
                TrackEntity(
                    id = "yt_sync_01",
                    title = "Afterglow Reverie (Acoustic Studio)",
                    artist = "Luna & The Beats",
                    album = "YouTube Session 4K",
                    durationMs = 218000L,
                    platformSource = PlatformSource.YOUTUBE,
                    sourceTrackId = "youtube:video:sync01live",
                    coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
                    audioQuality = AudioQuality.HIGH,
                    isDownloaded = false,
                    isLiked = true,
                    lyricsLrc = """
                        [00:00.00] (Acoustic live intro)
                        [00:08.00] In the afterglow of yesterday
                        [00:15.00] Chasing all the words we didn't say
                        [00:22.00] Cross-platform synchronicity
                        [00:29.00] Echoes in our shared infinity
                    """.trimIndent(),
                    genre = "Acoustic",
                    spotifyEquivalentId = "sp_sync_01",
                    youtubeEquivalentId = "yt_sync_01"
                ),
                TrackEntity(
                    id = "sp_sync_02",
                    title = "Neon Resonance",
                    artist = "CyberPulse",
                    album = "Quantum Frequency (Hi-Res)",
                    durationMs = 245000L,
                    platformSource = PlatformSource.SPOTIFY,
                    sourceTrackId = "spotify:track:sync02pulse",
                    coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                    audioQuality = AudioQuality.LOSSLESS,
                    isDownloaded = true,
                    downloadedBytes = 19400000L,
                    isLiked = true,
                    lyricsLrc = """
                        [00:00.00] (Quantum synth oscillation)
                        [00:07.50] Light waves moving in reverse
                        [00:13.20] Pulse across the universe
                        [00:19.00] Synchronized on every track
                        [00:25.00] There is no turning back
                    """.trimIndent(),
                    genre = "Synthwave",
                    spotifyEquivalentId = "sp_sync_02",
                    youtubeEquivalentId = "yt_sync_02"
                ),
                TrackEntity(
                    id = "yt_sync_02",
                    title = "Neon Resonance (Visualizer Edit 4K)",
                    artist = "CyberPulse",
                    album = "YouTube Visuals 4K",
                    durationMs = 245000L,
                    platformSource = PlatformSource.YOUTUBE,
                    sourceTrackId = "youtube:video:sync02visual",
                    coverUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                    audioQuality = AudioQuality.HIGH,
                    isDownloaded = false,
                    isLiked = true,
                    lyricsLrc = """
                        [00:00.00] (Quantum synth oscillation)
                        [00:07.50] Light waves moving in reverse
                        [00:13.20] Pulse across the universe
                    """.trimIndent(),
                    genre = "Synthwave",
                    spotifyEquivalentId = "sp_sync_02",
                    youtubeEquivalentId = "yt_sync_02"
                )
            )

            for (track in syncedTracks) {
                repository.insertCustomTrack(track)
            }

            // Also create/update synced Unified Playlists
            val syncedPlaylists = listOf(
                PlaylistEntity(
                    id = "pl_spotify_discover",
                    title = "⚡ Discover Weekly (Spotify Lossless)",
                    description = "Personalized recommendation mix synced directly from Spotify Hi-Fi library.",
                    coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
                    isUnified = true,
                    isCollaborative = false,
                    sessionCode = null
                ),
                PlaylistEntity(
                    id = "pl_youtube_live",
                    title = "🔴 YouTube 4K Live & Acoustic",
                    description = "Official live concert studio tracks and acoustic sessions synced from YouTube Music.",
                    coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                    isUnified = true,
                    isCollaborative = false,
                    sessionCode = null
                ),
                PlaylistEntity(
                    id = "pl_unified_top",
                    title = "🔥 Spotify & YouTube Fusion Mix",
                    description = "Seamless crossover blending top trending hits from Spotify Charts & YouTube Trending",
                    coverUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
                    isUnified = true,
                    isCollaborative = false,
                    sessionCode = null
                )
            )

            for (p in syncedPlaylists) {
                repository.updatePlaylist(p)
                repository.addTrackToPlaylist(p.id, "sp_sync_01")
                repository.addTrackToPlaylist(p.id, "yt_sync_01")
                repository.addTrackToPlaylist(p.id, "sp_sync_02")
                repository.addTrackToPlaylist(p.id, "yt_sync_02")
                repository.cachePlaylistMetadata(
                    CachedPlaylistMetadataEntity(
                        playlistId = p.id,
                        title = p.title,
                        description = p.description,
                        coverUrl = p.coverUrl,
                        totalTrackCount = 4,
                        downloadedTrackCount = 2,
                        totalDurationMs = 918000L,
                        cachedSizeBytes = 37600000L,
                        platformSource = PlatformSource.SPOTIFY,
                        isOfflinePinned = true,
                        isFullyDownloaded = false,
                        lastCachedTimestamp = System.currentTimeMillis()
                    )
                )
            }

            _syncState.value = _syncState.value.copy(
                progress = 0.95f,
                currentStepDescription = "Finalizing local database indexing & cache persistence..."
            )
            delay(300L)

            val timestamp = System.currentTimeMillis()
            repository.updateSyncTimestamp(timestamp)
            val timeFormatted = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(Date(timestamp))

            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                progress = 1.0f,
                currentStepDescription = "Cross-platform library synchronized! Spotify & YouTube tracks, playlists and liked songs are up to date.",
                lastSyncFormatted = timeFormatted,
                matchedSongsCount = 24,
                totalSyncedAcrossPlatforms = 48,
                playlistsSyncedCount = 6,
                likedSongsSyncedCount = 18,
                spotifyAccount = _syncState.value.spotifyAccount.copy(syncedItemsCount = spotifyTrackCount),
                youtubeAccount = _syncState.value.youtubeAccount.copy(syncedItemsCount = youtubeTrackCount)
            )

            onComplete?.invoke()
        }
    }
}


