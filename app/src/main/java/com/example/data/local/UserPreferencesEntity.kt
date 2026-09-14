package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AudioQuality

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: String = "default_user_prefs",
    val streamingQuality: AudioQuality = AudioQuality.HIGH,
    val downloadQuality: AudioQuality = AudioQuality.LOSSLESS,
    val isOfflineModeOnly: Boolean = false,
    val selectedEqualizerPreset: String = "Studio Hi-Fi",
    val spotifyToken: String = "sp_tok_demo_active_98234",
    val isSpotifyLinked: Boolean = true,
    val youtubeApiKey: String = "yt_api_key_demo_unified_581",
    val isYoutubeLinked: Boolean = true,
    val autoCrossfade: Boolean = true,
    val crossfadeSeconds: Int = 4,
    val cacheSizeLimitMb: Int = 2048,
    val volumeNormalization: Boolean = true,
    val autoDownloadLikedTracks: Boolean = false,
    val lastLibrarySyncTimestamp: Long = System.currentTimeMillis()
)
