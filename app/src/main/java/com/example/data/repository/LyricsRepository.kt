package com.example.data.repository

import android.util.Log
import com.example.data.model.LyricsLine
import com.example.data.model.LyricsParser
import com.example.data.remote.LyricsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

sealed interface LyricsFetchResult {
    data class Success(
        val lines: List<LyricsLine>,
        val source: String,
        val isSynced: Boolean,
        val plainLyrics: String? = null
    ) : LyricsFetchResult

    data class Empty(val message: String) : LyricsFetchResult
    data class Error(val message: String, val fallbackLines: List<LyricsLine>) : LyricsFetchResult
}

class LyricsRepository(
    private val apiService: LyricsApiService = LyricsApiService.create()
) {
    private val TAG = "LyricsRepository"
    private val cache = ConcurrentHashMap<String, LyricsFetchResult.Success>()

    private fun cacheKey(trackTitle: String, artist: String): String =
        "${trackTitle.trim().lowercase()}__${artist.trim().lowercase()}"

    suspend fun getSyncedLyrics(
        trackId: String,
        trackTitle: String,
        artist: String,
        album: String? = null,
        durationMs: Long = 0L,
        fallbackLrc: String = ""
    ): LyricsFetchResult = withContext(Dispatchers.IO) {
        val key = cacheKey(trackTitle, artist)
        cache[key]?.let {
            return@withContext it
        }

        // Try to fetch from remote Synced Lyrics API (LRCLIB)
        try {
            val durationSeconds = if (durationMs > 0) durationMs / 1000L else null
            val response = apiService.getLyrics(
                trackName = trackTitle,
                artistName = artist,
                albumName = album,
                durationSeconds = durationSeconds
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val syncedLrc = body.syncedLyrics
                if (!syncedLrc.isNullOrBlank()) {
                    val parsed = LyricsParser.parseLrc(syncedLrc)
                    if (parsed.isNotEmpty()) {
                        val result = LyricsFetchResult.Success(
                            lines = parsed,
                            source = "LRCLIB Synced API",
                            isSynced = true,
                            plainLyrics = body.plainLyrics
                        )
                        cache[key] = result
                        return@withContext result
                    }
                } else if (!body.plainLyrics.isNullOrBlank()) {
                    // Plain unsynced lyrics from API
                    val plainLines = body.plainLyrics.lines()
                        .filter { it.isNotBlank() }
                        .mapIndexed { index, text ->
                            LyricsLine(timestampMs = index * 4000L, text = text.trim())
                        }
                    val result = LyricsFetchResult.Success(
                        lines = plainLines,
                        source = "LRCLIB Plain API",
                        isSynced = false,
                        plainLyrics = body.plainLyrics
                    )
                    cache[key] = result
                    return@withContext result
                }
            } else {
                // If exact get failed, try query search
                val searchResponse = apiService.searchLyrics(query = "$trackTitle $artist")
                if (searchResponse.isSuccessful && !searchResponse.body().isNullOrEmpty()) {
                    val candidate = searchResponse.body()!!.firstOrNull { !it.syncedLyrics.isNullOrBlank() }
                        ?: searchResponse.body()!!.first()

                    val candidateSynced = candidate.syncedLyrics
                    if (!candidateSynced.isNullOrBlank()) {
                        val parsed = LyricsParser.parseLrc(candidateSynced)
                        if (parsed.isNotEmpty()) {
                            val result = LyricsFetchResult.Success(
                                lines = parsed,
                                source = "LRCLIB Search API",
                                isSynced = true,
                                plainLyrics = candidate.plainLyrics
                            )
                            cache[key] = result
                            return@withContext result
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch remote lyrics from API for $trackTitle: ${e.message}")
        }

        // Fallback to bundled / cached LRC
        if (fallbackLrc.isNotBlank()) {
            val parsedFallback = LyricsParser.parseLrc(fallbackLrc)
            if (parsedFallback.isNotEmpty()) {
                val result = LyricsFetchResult.Success(
                    lines = parsedFallback,
                    source = "Local Synced Cache",
                    isSynced = true
                )
                cache[key] = result
                return@withContext result
            }
        }

        LyricsFetchResult.Empty("No synchronized lyrics found for \"$trackTitle\".")
    }

    fun clearCache() {
        cache.clear()
    }
}
