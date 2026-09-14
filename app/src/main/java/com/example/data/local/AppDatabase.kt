package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromPlatformSource(value: PlatformSource): String = value.name

    @TypeConverter
    fun toPlatformSource(value: String): PlatformSource =
        try { PlatformSource.valueOf(value) } catch (e: Exception) { PlatformSource.SPOTIFY }

    @TypeConverter
    fun fromAudioQuality(value: AudioQuality): String = value.name

    @TypeConverter
    fun toAudioQuality(value: String): AudioQuality =
        try { AudioQuality.valueOf(value) } catch (e: Exception) { AudioQuality.HIGH }
}

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        UserPreferencesEntity::class,
        CachedPlaylistMetadataEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun cachedPlaylistMetadataDao(): CachedPlaylistMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unifiedx_music_db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial tracks, playlists, preferences, and cached metadata
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).seedInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        trackDao().insertTracks(SeedData.initialTracks)
        for (playlist in SeedData.initialPlaylists) {
            playlistDao().insertPlaylist(playlist)
        }
        for (ref in SeedData.initialCrossRefs) {
            playlistDao().insertCrossRef(ref)
        }
        userPreferencesDao().upsertUserPreferences(SeedData.initialUserPreferences)
        cachedPlaylistMetadataDao().insertAllMetadata(SeedData.initialCachedPlaylists)
    }
}
