package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedPlaylistMetadataDao {
    @Query("SELECT * FROM cached_playlist_metadata ORDER BY isOfflinePinned DESC, lastCachedTimestamp DESC")
    fun getAllCachedMetadata(): Flow<List<CachedPlaylistMetadataEntity>>

    @Query("SELECT * FROM cached_playlist_metadata WHERE isOfflinePinned = 1 ORDER BY lastCachedTimestamp DESC")
    fun getOfflinePinnedPlaylists(): Flow<List<CachedPlaylistMetadataEntity>>

    @Query("SELECT * FROM cached_playlist_metadata WHERE playlistId = :playlistId LIMIT 1")
    fun getCachedMetadataById(playlistId: String): Flow<CachedPlaylistMetadataEntity?>

    @Query("SELECT * FROM cached_playlist_metadata WHERE playlistId = :playlistId LIMIT 1")
    suspend fun getCachedMetadataByIdSync(playlistId: String): CachedPlaylistMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMetadata(metadata: CachedPlaylistMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMetadata(list: List<CachedPlaylistMetadataEntity>)

    @Query("UPDATE cached_playlist_metadata SET isOfflinePinned = :pinned WHERE playlistId = :playlistId")
    suspend fun setOfflinePinned(playlistId: String, pinned: Boolean)

    @Query("UPDATE cached_playlist_metadata SET downloadedTrackCount = :downloadedCount, isFullyDownloaded = :isFullyDownloaded, cachedSizeBytes = :cachedBytes WHERE playlistId = :playlistId")
    suspend fun updateDownloadStats(playlistId: String, downloadedCount: Int, isFullyDownloaded: Boolean, cachedBytes: Long)

    @Query("DELETE FROM cached_playlist_metadata WHERE playlistId = :playlistId")
    suspend fun deleteCachedMetadata(playlistId: String)

    @Query("DELETE FROM cached_playlist_metadata WHERE isOfflinePinned = 0 AND lastCachedTimestamp < :olderThanMs")
    suspend fun clearExpiredCache(olderThanMs: Long)

    @Query("SELECT COUNT(*) FROM cached_playlist_metadata")
    suspend fun getCachedPlaylistCount(): Int
}
