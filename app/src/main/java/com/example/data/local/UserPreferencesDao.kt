package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    fun getUserPreferences(id: String = "default_user_prefs"): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    suspend fun getUserPreferencesSync(id: String = "default_user_prefs"): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserPreferences(preferences: UserPreferencesEntity)

    @Query("UPDATE user_preferences SET isOfflineModeOnly = :offlineOnly WHERE id = :id")
    suspend fun updateOfflineMode(offlineOnly: Boolean, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET streamingQuality = :quality WHERE id = :id")
    suspend fun updateStreamingQuality(quality: String, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET downloadQuality = :quality WHERE id = :id")
    suspend fun updateDownloadQuality(quality: String, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET selectedEqualizerPreset = :preset WHERE id = :id")
    suspend fun updateEqualizerPreset(preset: String, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET spotifyToken = :token, isSpotifyLinked = :linked WHERE id = :id")
    suspend fun updateSpotifyConnection(token: String, linked: Boolean, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET youtubeApiKey = :apiKey, isYoutubeLinked = :linked WHERE id = :id")
    suspend fun updateYoutubeConnection(apiKey: String, linked: Boolean, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET autoCrossfade = :autoCrossfade, crossfadeSeconds = :seconds WHERE id = :id")
    suspend fun updateCrossfadeSettings(autoCrossfade: Boolean, seconds: Int, id: String = "default_user_prefs")

    @Query("UPDATE user_preferences SET lastLibrarySyncTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastSync(timestamp: Long, id: String = "default_user_prefs")

    @Query("SELECT COUNT(*) FROM user_preferences")
    suspend fun getPreferencesCount(): Int
}
