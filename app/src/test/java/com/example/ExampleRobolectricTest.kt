package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CachedPlaylistMetadataEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("UnifiedX", appName)
    }

    @Test
    fun `test user preferences persistence in Room`() = runBlocking {
        val prefsDao = db.userPreferencesDao()
        val prefs = UserPreferencesEntity(
            id = "default_user_prefs",
            streamingQuality = AudioQuality.LOSSLESS,
            downloadQuality = AudioQuality.LOSSLESS,
            isOfflineModeOnly = true,
            selectedEqualizerPreset = "Club Bass Boost",
            spotifyToken = "sp_test_token_123",
            youtubeApiKey = "yt_test_key_456"
        )
        prefsDao.upsertUserPreferences(prefs)

        val retrieved = prefsDao.getUserPreferencesSync("default_user_prefs")
        assertNotNull(retrieved)
        assertEquals(AudioQuality.LOSSLESS, retrieved?.streamingQuality)
        assertTrue(retrieved?.isOfflineModeOnly == true)
        assertEquals("Club Bass Boost", retrieved?.selectedEqualizerPreset)
        assertEquals("sp_test_token_123", retrieved?.spotifyToken)

        // Test update offline mode query
        prefsDao.updateOfflineMode(false, "default_user_prefs")
        val updated = prefsDao.getUserPreferencesSync("default_user_prefs")
        assertEquals(false, updated?.isOfflineModeOnly)
    }

    @Test
    fun `test cached playlist metadata persistence in Room for offline access`() = runBlocking {
        val cacheDao = db.cachedPlaylistMetadataDao()
        val metadata = CachedPlaylistMetadataEntity(
            playlistId = "pl_test_offline",
            title = "Offline Commute Mix",
            description = "Cached tracks for offline listening",
            coverUrl = "https://example.com/cover.jpg",
            totalTrackCount = 10,
            downloadedTrackCount = 10,
            totalDurationMs = 2400000L,
            cachedSizeBytes = 125000000L,
            platformSource = PlatformSource.SPOTIFY,
            isOfflinePinned = true,
            isFullyDownloaded = true
        )
        cacheDao.insertOrUpdateMetadata(metadata)

        val retrieved = cacheDao.getCachedMetadataByIdSync("pl_test_offline")
        assertNotNull(retrieved)
        assertEquals("Offline Commute Mix", retrieved?.title)
        assertEquals(10, retrieved?.totalTrackCount)
        assertEquals(10, retrieved?.downloadedTrackCount)
        assertTrue(retrieved?.isOfflinePinned == true)
        assertTrue(retrieved?.isFullyDownloaded == true)
        assertEquals(125000000L, retrieved?.cachedSizeBytes)

        // Test unpinning from offline
        cacheDao.setOfflinePinned("pl_test_offline", false)
        val unpinned = cacheDao.getCachedMetadataByIdSync("pl_test_offline")
        assertEquals(false, unpinned?.isOfflinePinned)
    }
}

