package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val platformSource: PlatformSource,
    val sourceTrackId: String,
    val coverUrl: String,
    val streamUrl: String,
    val audioQuality: AudioQuality = AudioQuality.HIGH,
    val isDownloaded: Boolean = false,
    val downloadedBytes: Long = 0L,
    val isLiked: Boolean = false,
    val lyricsLrc: String = "",
    val spotifyEquivalentId: String? = null,
    val youtubeEquivalentId: String? = null,
    val genre: String = "Pop",
    val playCount: Int = 0,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
