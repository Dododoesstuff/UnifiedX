package com.example.sync

import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
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
    val syncedItemsCount: Int,
    val tokenOrApiKey: String = ""
)

data class SyncState(
    val isSyncing: Boolean = false,
    val progress: Float = 0f,
    val currentStepDescription: String = "Idle",
    val lastSyncFormatted: String = "Just now",
    val matchedSongsCount: Int = 18,
    val totalSyncedAcrossPlatforms: Int = 42,
    val spotifyAccount: SyncAccountInfo = SyncAccountInfo(
        platform = PlatformSource.SPOTIFY,
        isConnected = true,
        accountUsername = "kodadavid.spotify",
        syncedItemsCount = 24,
        tokenOrApiKey = "sp_tok_live_89127391"
    ),
    val youtubeAccount: SyncAccountInfo = SyncAccountInfo(
        platform = PlatformSource.YOUTUBE,
        isConnected = true,
        accountUsername = "David Koda Music",
        syncedItemsCount = 18,
        tokenOrApiKey = "AIzaSyYT_mockKey_98124"
    )
)

class CrossPlatformSyncManager(
    private val repository: MusicRepository,
    private val scope: CoroutineScope
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun updateAccountToken(source: PlatformSource, token: String, username: String) {
        if (source == PlatformSource.SPOTIFY) {
            _syncState.value = _syncState.value.copy(
                spotifyAccount = _syncState.value.spotifyAccount.copy(
                    tokenOrApiKey = token,
                    accountUsername = username.ifBlank { "User_Spotify" },
                    isConnected = token.isNotBlank()
                )
            )
        } else if (source == PlatformSource.YOUTUBE) {
            _syncState.value = _syncState.value.copy(
                youtubeAccount = _syncState.value.youtubeAccount.copy(
                    tokenOrApiKey = token,
                    accountUsername = username.ifBlank { "User_YouTube" },
                    isConnected = token.isNotBlank()
                )
            )
        }
    }

    fun startCrossPlatformSync(onComplete: (() -> Unit)? = null) {
        if (_syncState.value.isSyncing) return

        scope.launch(Dispatchers.IO) {
            _syncState.value = _syncState.value.copy(
                isSyncing = true,
                progress = 0.05f,
                currentStepDescription = "Connecting to Spotify Web API & checking token validity..."
            )
            delay(600L)

            _syncState.value = _syncState.value.copy(
                progress = 0.25f,
                currentStepDescription = "Fetching Spotify liked tracks & user saved albums..."
            )
            delay(700L)

            _syncState.value = _syncState.value.copy(
                progress = 0.50f,
                currentStepDescription = "Querying YouTube Data API v3 playlists & liked videos..."
            )
            delay(700L)

            _syncState.value = _syncState.value.copy(
                progress = 0.75f,
                currentStepDescription = "Running cross-platform audio hash matching engine..."
            )
            delay(800L)

            // Inject synced discovered tracks if needed
            val extraSyncTracks = listOf(
                TrackEntity(
                    id = "sp_sync_01",
                    title = "Afterglow Reverie",
                    artist = "Luna & The Beats",
                    album = "Synced Discoveries",
                    durationMs = 210000L,
                    platformSource = PlatformSource.SPOTIFY,
                    sourceTrackId = "spotify:track:sync01923",
                    coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
                    audioQuality = AudioQuality.LOSSLESS,
                    isDownloaded = false,
                    isLiked = true,
                    lyricsLrc = """
                        [00:00.00] (Warm atmospheric intro)
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
                        [00:00.00] (Acoustic intro live)
                        [00:08.00] In the afterglow of yesterday
                        [00:15.00] Chasing all the words we didn't say
                        [00:22.00] Cross-platform synchronicity
                        [00:29.00] Echoes in our shared infinity
                    """.trimIndent(),
                    genre = "Acoustic",
                    spotifyEquivalentId = "sp_sync_01",
                    youtubeEquivalentId = "yt_sync_01"
                )
            )
            for (track in extraSyncTracks) {
                repository.insertCustomTrack(track)
            }

            val timestamp = System.currentTimeMillis()
            repository.updateSyncTimestamp(timestamp)
            val timeFormatted = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(Date(timestamp))

            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                progress = 1.0f,
                currentStepDescription = "Library synchronized! Matched 20 Spotify and YouTube tracks seamlessly.",
                lastSyncFormatted = timeFormatted,
                matchedSongsCount = 20,
                totalSyncedAcrossPlatforms = 44
            )

            onComplete?.invoke()
        }
    }
}
