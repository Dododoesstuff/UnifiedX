package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY title ASC")
    fun getDownloadedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY title ASC")
    fun getLikedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE platformSource = :source ORDER BY title ASC")
    fun getTracksBySource(source: PlatformSource): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchTracks(query: String): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("UPDATE tracks SET isLiked = :isLiked WHERE id = :id")
    suspend fun setLiked(id: String, isLiked: Boolean)

    @Query("UPDATE tracks SET isDownloaded = :isDownloaded, downloadedBytes = :bytes WHERE id = :id")
    suspend fun setDownloaded(id: String, isDownloaded: Boolean, bytes: Long)

    @Query("UPDATE tracks SET audioQuality = :quality WHERE id = :id")
    suspend fun setAudioQuality(id: String, quality: AudioQuality)

    @Query("UPDATE tracks SET lastSyncedAt = :timestamp")
    suspend fun updateAllSyncTimestamp(timestamp: Long)

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int

    @Query("SELECT COUNT(*) FROM tracks WHERE isDownloaded = 1")
    suspend fun getDownloadedCount(): Int
}
