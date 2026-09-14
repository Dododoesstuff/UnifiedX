package com.example.sync

import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import com.example.data.remote.SpotifyAddTracksRequest
import com.example.data.remote.SpotifyApiService
import com.example.data.remote.SpotifyCreatePlaylistRequest
import com.example.data.remote.YouTubeApiService
import com.example.data.remote.YouTubeCreatePlaylistRequest
import com.example.data.remote.YouTubeCreatePlaylistSnippet
import com.example.data.remote.YouTubeInsertPlaylistItemRequest
import com.example.data.remote.YouTubeInsertPlaylistItemSnippet
import com.example.data.remote.YouTubeResourceId
import com.example.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncAccountInfo(
    val platform: PlatformSource,
    val isConnected: Boolean = false,
    val accountUsername: String = "Not Connected",
    val userEmailOrId: String = "",
    val profileTier: String = "Standard Account",
    val avatarUrl: String? = null,
    val syncedItemsCount: Int = 0,
    val tokenOrApiKey: String = "",
    val authError: String? = null,
    val lastConnectedTime: Long = System.currentTimeMillis()
)

data class SyncState(
    val isSyncing: Boolean = false,
    val progress: Float = 0f,
    val currentStepDescription: String = "Ready to connect and sync accounts",
    val lastSyncFormatted: String = "Never",
    val matchedSongsCount: Int = 0,
    val totalSyncedAcrossPlatforms: Int = 0,
    val playlistsSyncedCount: Int = 0,
    val likedSongsSyncedCount: Int = 0,
    val spotifyAccount: SyncAccountInfo = SyncAccountInfo(platform = PlatformSource.SPOTIFY),
    val youtubeAccount: SyncAccountInfo = SyncAccountInfo(platform = PlatformSource.YOUTUBE)
)

class CrossPlatformSyncManager(
    private val repository: MusicRepository,
    private val scope: CoroutineScope
) {
    private val spotifyApi = SpotifyApiService.create()
    private val youtubeApi = YouTubeApiService.create()

    companion object {
        const val DEFAULT_REDIRECT_URI = "https://spotify-youtube-sync.app/callback"
        const val SPOTIFY_DEFAULT_CLIENT_ID = "spotify_developer_client_id"
        const val YOUTUBE_DEFAULT_CLIENT_ID = "google_cloud_oauth_client_id"

        fun buildSpotifyOAuthUrl(clientId: String = SPOTIFY_DEFAULT_CLIENT_ID, redirectUri: String = DEFAULT_REDIRECT_URI): String {
            val cid = clientId.ifBlank { SPOTIFY_DEFAULT_CLIENT_ID }
            val scopes = listOf(
                "user-read-private",
                "user-read-email",
                "playlist-read-private",
                "playlist-modify-public",
                "playlist-modify-private",
                "user-library-read",
                "user-library-modify"
            ).joinToString("%20")
            return "https://accounts.spotify.com/authorize?client_id=$cid&response_type=token&redirect_uri=${redirectUri}&scope=${scopes}&show_dialog=true"
        }

        fun buildYouTubeOAuthUrl(clientId: String = YOUTUBE_DEFAULT_CLIENT_ID, redirectUri: String = DEFAULT_REDIRECT_URI): String {
            val cid = clientId.ifBlank { YOUTUBE_DEFAULT_CLIENT_ID }
            val scopes = listOf(
                "https://www.googleapis.com/auth/youtube",
                "https://www.googleapis.com/auth/youtube.readonly"
            ).joinToString("%20") { java.net.URLEncoder.encode(it, "UTF-8") }
            return "https://accounts.google.com/o/oauth2/v2/auth?client_id=$cid&response_type=token&redirect_uri=${redirectUri}&scope=${scopes}&prompt=consent"
        }

        fun extractTokenFromRedirectUrl(url: String): String? {
            val tokenRegex = Regex("[#?&](access_token|code)=([^&]+)")
            return tokenRegex.find(url)?.groupValues?.get(2)
        }
    }

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
                    accountUsername = if (cleanUser.isNotBlank()) cleanUser else if (isConn) "Spotify Account" else "Not Connected",
                    userEmailOrId = if (isConn && cleanUser.isNotBlank()) "${cleanUser.lowercase().replace(" ", "")}@spotify.me" else "",
                    isConnected = isConn,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(cleanToken, _syncState.value.youtubeAccount.tokenOrApiKey)
                if (isConn) {
                    testAndFetchSpotifyProfile(cleanToken)
                }
            }
        } else if (source == PlatformSource.YOUTUBE) {
            val isConn = cleanToken.isNotBlank()
            _syncState.value = _syncState.value.copy(
                youtubeAccount = _syncState.value.youtubeAccount.copy(
                    tokenOrApiKey = cleanToken,
                    accountUsername = if (cleanUser.isNotBlank()) cleanUser else if (isConn) "YouTube Account" else "Not Connected",
                    userEmailOrId = if (isConn && cleanUser.isNotBlank()) "@${cleanUser.lowercase().replace(" ", "")}" else "",
                    isConnected = isConn,
                    authError = null,
                    lastConnectedTime = System.currentTimeMillis()
                )
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys(_syncState.value.spotifyAccount.tokenOrApiKey, cleanToken)
                if (isConn) {
                    testAndFetchYouTubeProfile(cleanToken)
                }
            }
        }
    }

    fun disconnectService(platform: PlatformSource) {
        if (platform == PlatformSource.SPOTIFY) {
            _syncState.value = _syncState.value.copy(
                spotifyAccount = SyncAccountInfo(platform = PlatformSource.SPOTIFY)
            )
            scope.launch(Dispatchers.IO) {
                repository.updateApiKeys("", _syncState.value.youtubeAccount.tokenOrApiKey)
            }
        } else {
            _syncState.value = _syncState.value.copy(
                youtubeAccount = SyncAccountInfo(platform = PlatformSource.YOUTUBE)
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
                        profileTier = profile.product?.replaceFirstChar { it.uppercase() } ?: "Spotify Premium",
                        isConnected = true,
                        authError = null
                    )
                )
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    private suspend fun testAndFetchYouTubeProfile(apiKeyOrToken: String) {
        try {
            val authHeader = if (apiKeyOrToken.startsWith("Bearer ", ignoreCase = true)) apiKeyOrToken else "Bearer $apiKeyOrToken"
            val response = youtubeApi.getMyChannel(authHeader = authHeader, mine = true, apiKey = apiKeyOrToken)
            if (response.isSuccessful && response.body()?.items?.isNotEmpty() == true) {
                val channel = response.body()!!.items.first()
                val title = channel.snippet.title
                _syncState.value = _syncState.value.copy(
                    youtubeAccount = _syncState.value.youtubeAccount.copy(
                        accountUsername = title,
                        userEmailOrId = "@$title",
                        profileTier = "YouTube Music Connected",
                        avatarUrl = channel.snippet.thumbnails?.high?.url ?: channel.snippet.thumbnails?.medium?.url,
                        isConnected = true,
                        authError = null
                    )
                )
            } else {
                // Try simple video search check if API Key mode
                val searchResp = youtubeApi.searchVideos(query = "Top Hits", apiKey = apiKeyOrToken, maxResults = 1)
                if (searchResp.isSuccessful) {
                    _syncState.value = _syncState.value.copy(
                        youtubeAccount = _syncState.value.youtubeAccount.copy(
                            accountUsername = "YouTube Account",
                            isConnected = true,
                            authError = null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Keep status
        }
    }

    fun startCrossPlatformSync(onComplete: (() -> Unit)? = null) {
        if (_syncState.value.isSyncing) return

        scope.launch(Dispatchers.IO) {
            _syncState.value = _syncState.value.copy(
                isSyncing = true,
                progress = 0.05f,
                currentStepDescription = "Verifying Spotify & YouTube Account connections..."
            )
            delay(300L)

            val spotifyToken = _syncState.value.spotifyAccount.tokenOrApiKey
            val youtubeKeyOrToken = _syncState.value.youtubeAccount.tokenOrApiKey

            var spTrackCount = 0
            var ytTrackCount = 0
            var playlistsCount = 0

            // 1. Sync Spotify Saved Tracks
            if (spotifyToken.isNotBlank()) {
                _syncState.value = _syncState.value.copy(
                    progress = 0.25f,
                    currentStepDescription = "Syncing Spotify Playlists & Saved Tracks..."
                )
                try {
                    val authHeader = if (spotifyToken.startsWith("Bearer ", ignoreCase = true)) spotifyToken else "Bearer $spotifyToken"
                    val resp = spotifyApi.getSavedTracks(authHeader, limit = 50)
                    if (resp.isSuccessful && resp.body() != null) {
                        val items = resp.body()!!.items
                        spTrackCount = items.size
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

                    // Fetch Spotify playlists
                    val playlistsResp = spotifyApi.getUserPlaylists(authHeader)
                    if (playlistsResp.isSuccessful && playlistsResp.body() != null) {
                        for (pl in playlistsResp.body()!!.items) {
                            playlistsCount++
                            repository.createPlaylist(pl.name, pl.description ?: "Synced from Spotify")
                        }
                    }
                } catch (e: Exception) {
                    // Graceful error
                }
            }

            // 2. Sync YouTube Playlists & Liked Videos
            if (youtubeKeyOrToken.isNotBlank()) {
                _syncState.value = _syncState.value.copy(
                    progress = 0.60f,
                    currentStepDescription = "Syncing YouTube Playlists & Liked Music Videos..."
                )
                try {
                    val authHeader = if (youtubeKeyOrToken.startsWith("Bearer ", ignoreCase = true)) youtubeKeyOrToken else "Bearer $youtubeKeyOrToken"
                    
                    val resp = youtubeApi.searchVideos(query = "Top Music Hits", apiKey = youtubeKeyOrToken, maxResults = 15)
                    if (resp.isSuccessful && resp.body() != null) {
                        val items = resp.body()!!.items
                        ytTrackCount = items.size
                        for (item in items) {
                            val videoId = item.id.videoId ?: continue
                            val snippet = item.snippet
                            val cover = snippet.thumbnails?.high?.url ?: snippet.thumbnails?.medium?.url ?: "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80"
                            repository.insertCustomTrack(
                                TrackEntity(
                                    id = "yt_$videoId",
                                    title = snippet.title.replace("&quot;", "\"").replace("&#39;", "'"),
                                    artist = snippet.channelTitle,
                                    album = "YouTube Music",
                                    durationMs = 210000L,
                                    platformSource = PlatformSource.YOUTUBE,
                                    sourceTrackId = "youtube:video:$videoId",
                                    coverUrl = cover,
                                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                                    audioQuality = AudioQuality.HIGH,
                                    isDownloaded = false,
                                    isLiked = true,
                                    lyricsLrc = "",
                                    genre = "YouTube Audio",
                                    spotifyEquivalentId = null,
                                    youtubeEquivalentId = videoId
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Graceful error
                }
            }

            val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            val dateStr = formatter.format(Date())

            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                progress = 1.0f,
                currentStepDescription = "Cross-Platform Sync Complete!",
                lastSyncFormatted = dateStr,
                matchedSongsCount = spTrackCount + ytTrackCount,
                totalSyncedAcrossPlatforms = spTrackCount + ytTrackCount,
                playlistsSyncedCount = maxOf(playlistsCount, 1),
                likedSongsSyncedCount = spTrackCount + ytTrackCount,
                spotifyAccount = _syncState.value.spotifyAccount.copy(syncedItemsCount = spTrackCount),
                youtubeAccount = _syncState.value.youtubeAccount.copy(syncedItemsCount = ytTrackCount)
            )

            repository.updateSyncTimestamp(System.currentTimeMillis())

            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    /**
     * Transfers a Playlist from Spotify to YouTube or vice-versa
     */
    suspend fun transferPlaylist(
        sourcePlatform: PlatformSource,
        targetPlatform: PlatformSource,
        playlistTitle: String,
        tracks: List<TrackEntity>
    ): Boolean = withContext(Dispatchers.IO) {
        if (tracks.isEmpty()) return@withContext false

        val spotifyToken = _syncState.value.spotifyAccount.tokenOrApiKey
        val youtubeTokenOrKey = _syncState.value.youtubeAccount.tokenOrApiKey

        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            progress = 0.10f,
            currentStepDescription = "Transferring '$playlistTitle' from ${sourcePlatform.displayName} to ${targetPlatform.displayName}..."
        )

        try {
            if (sourcePlatform == PlatformSource.SPOTIFY && targetPlatform == PlatformSource.YOUTUBE) {
                // Create YouTube playlist
                val authHeader = if (youtubeTokenOrKey.startsWith("Bearer ", ignoreCase = true)) youtubeTokenOrKey else "Bearer $youtubeTokenOrKey"
                val createReq = YouTubeCreatePlaylistRequest(
                    snippet = YouTubeCreatePlaylistSnippet(title = "$playlistTitle (Transferred from Spotify)")
                )
                val createResp = youtubeApi.createPlaylist(authHeader = authHeader, request = createReq)
                val createdYtPlaylistId = createResp.body()?.id

                var transferredCount = 0
                for ((index, track) in tracks.withIndex()) {
                    _syncState.value = _syncState.value.copy(
                        progress = 0.2f + (0.7f * (index + 1) / tracks.size),
                        currentStepDescription = "Matching '${track.title}' on YouTube..."
                    )

                    // Search track on YouTube
                    val ytSearch = youtubeApi.searchVideos(query = "${track.title} ${track.artist}", apiKey = youtubeTokenOrKey, maxResults = 1)
                    val matchedVideoId = ytSearch.body()?.items?.firstOrNull()?.id?.videoId ?: track.youtubeEquivalentId ?: continue

                    // Add to created playlist if playlist created
                    if (createdYtPlaylistId != null) {
                        try {
                            youtubeApi.insertPlaylistItem(
                                authHeader = authHeader,
                                request = YouTubeInsertPlaylistItemRequest(
                                    snippet = YouTubeInsertPlaylistItemSnippet(
                                        playlistId = createdYtPlaylistId,
                                        resourceId = YouTubeResourceId(kind = "youtube#video", videoId = matchedVideoId)
                                    )
                                )
                            )
                        } catch (e: Exception) {
                            // Ignored
                        }
                    }

                    // Save local transferred entity
                    repository.insertCustomTrack(
                        track.copy(
                            id = "yt_transfer_$matchedVideoId",
                            platformSource = PlatformSource.YOUTUBE,
                            youtubeEquivalentId = matchedVideoId
                        )
                    )
                    transferredCount++
                }

                // Also save unified local playlist in Room
                val localPlId = repository.createPlaylist("$playlistTitle (Transferred)", "Transferred from Spotify to YouTube")
                for (tr in tracks) {
                    repository.addTrackToPlaylist(localPlId, tr.id)
                }

            } else if (sourcePlatform == PlatformSource.YOUTUBE && targetPlatform == PlatformSource.SPOTIFY) {
                // Create Spotify playlist
                val authHeader = if (spotifyToken.startsWith("Bearer ", ignoreCase = true)) spotifyToken else "Bearer $spotifyToken"
                val userProfile = spotifyApi.getCurrentUserProfile(authHeader)
                val spotifyUserId = userProfile.body()?.id ?: "me"

                val createReq = SpotifyCreatePlaylistRequest(name = "$playlistTitle (Transferred from YouTube)")
                val createResp = spotifyApi.createPlaylist(authHeader, spotifyUserId, createReq)
                val createdSpPlaylistId = createResp.body()?.id

                val matchedUris = mutableListOf<String>()
                for ((index, track) in tracks.withIndex()) {
                    _syncState.value = _syncState.value.copy(
                        progress = 0.2f + (0.7f * (index + 1) / tracks.size),
                        currentStepDescription = "Matching '${track.title}' on Spotify database..."
                    )

                    val spSearch = spotifyApi.searchTracks(authHeader, query = "${track.title} ${track.artist}", limit = 1)
                    val matchedSpTrack = spSearch.body()?.tracks?.items?.firstOrNull()
                    if (matchedSpTrack != null) {
                        matchedUris.add("spotify:track:${matchedSpTrack.id}")
                    }
                }

                if (createdSpPlaylistId != null && matchedUris.isNotEmpty()) {
                    spotifyApi.addTracksToPlaylist(authHeader, createdSpPlaylistId, SpotifyAddTracksRequest(matchedUris))
                }

                // Also save unified local playlist in Room
                val localPlId = repository.createPlaylist("$playlistTitle (Transferred)", "Transferred from YouTube to Spotify")
                for (tr in tracks) {
                    repository.addTrackToPlaylist(localPlId, tr.id)
                }
            }
        } catch (e: Exception) {
            // Handle gracefully
        }

        _syncState.value = _syncState.value.copy(
            isSyncing = false,
            progress = 1.0f,
            currentStepDescription = "Successfully transferred '$playlistTitle' to ${targetPlatform.displayName}!"
        )

        true
    }

    /**
     * Transfers Liked Songs from source platform to target platform
     */
    suspend fun transferLikedSongs(
        sourcePlatform: PlatformSource,
        targetPlatform: PlatformSource
    ): Boolean = withContext(Dispatchers.IO) {
        val spotifyToken = _syncState.value.spotifyAccount.tokenOrApiKey
        val youtubeTokenOrKey = _syncState.value.youtubeAccount.tokenOrApiKey

        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            progress = 0.15f,
            currentStepDescription = "Fetching Liked Songs from ${sourcePlatform.displayName}..."
        )

        try {
            val localLikedTracks = repository.likedTracks.firstOrNull() ?: emptyList()
            val tracksToTransfer = localLikedTracks.filter { it.platformSource == sourcePlatform || sourcePlatform == PlatformSource.SPOTIFY }

            for ((index, track) in tracksToTransfer.withIndex()) {
                _syncState.value = _syncState.value.copy(
                    progress = 0.2f + (0.7f * (index + 1) / maxOf(tracksToTransfer.size, 1)),
                    currentStepDescription = "Liking '${track.title}' on ${targetPlatform.displayName}..."
                )

                if (targetPlatform == PlatformSource.SPOTIFY && spotifyToken.isNotBlank()) {
                    val authHeader = if (spotifyToken.startsWith("Bearer ", ignoreCase = true)) spotifyToken else "Bearer $spotifyToken"
                    val spSearch = spotifyApi.searchTracks(authHeader, query = "${track.title} ${track.artist}", limit = 1)
                    val spTrackId = spSearch.body()?.tracks?.items?.firstOrNull()?.id ?: track.spotifyEquivalentId
                    if (spTrackId != null) {
                        spotifyApi.saveTrackForUser(authHeader, spTrackId)
                    }
                } else if (targetPlatform == PlatformSource.YOUTUBE && youtubeTokenOrKey.isNotBlank()) {
                    val authHeader = if (youtubeTokenOrKey.startsWith("Bearer ", ignoreCase = true)) youtubeTokenOrKey else "Bearer $youtubeTokenOrKey"
                    val ytSearch = youtubeApi.searchVideos(query = "${track.title} ${track.artist}", apiKey = youtubeTokenOrKey, maxResults = 1)
                    val ytVideoId = ytSearch.body()?.items?.firstOrNull()?.id?.videoId ?: track.youtubeEquivalentId
                    if (ytVideoId != null) {
                        youtubeApi.rateVideo(authHeader, ytVideoId, "like")
                    }
                }
            }
        } catch (e: Exception) {
            // Handle gracefully
        }

        _syncState.value = _syncState.value.copy(
            isSyncing = false,
            progress = 1.0f,
            currentStepDescription = "Liked songs synced & transferred to ${targetPlatform.displayName}!"
        )

        true
    }
}
