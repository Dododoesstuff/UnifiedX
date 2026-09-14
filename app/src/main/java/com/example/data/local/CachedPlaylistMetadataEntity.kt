package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PlatformSource

@Entity(tableName = "cached_playlist_metadata")
data class CachedPlaylistMetadataEntity(
    @PrimaryKey
    val playlistId: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val localCoverPath: String? = null,
    val totalTrackCount: Int = 0,
    val downloadedTrackCount: Int = 0,
    val totalDurationMs: Long = 0L,
    val cachedSizeBytes: Long = 0L,
    val platformSource: PlatformSource = PlatformSource.SPOTIFY,
    val isOfflinePinned: Boolean = false,
    val isFullyDownloaded: Boolean = false,
    val lastCachedTimestamp: Long = System.currentTimeMillis(),
    val cacheEtag: String? = null
)
