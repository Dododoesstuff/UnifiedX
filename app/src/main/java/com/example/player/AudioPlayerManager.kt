package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.LyricsLine
import com.example.data.model.LyricsParser
import com.example.data.repository.LyricsFetchResult
import com.example.data.repository.LyricsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RepeatMode {
    OFF, ALL, ONE
}

enum class EqPreset(val label: String, val bassMultiplier: Float, val trebleMultiplier: Float) {
    HI_FI_STUDIO("Studio Hi-Fi", 1.0f, 1.0f),
    BASS_BOOST("Club Bass Boost", 1.35f, 0.9f),
    VOCAL_CLARITY("Vocal Acoustic", 0.85f, 1.25f),
    ELECTRONIC("Electronic Dynamic", 1.2f, 1.15f)
}

data class PlayerUiState(
    val currentTrack: TrackEntity? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val streamingQuality: AudioQuality = AudioQuality.LOSSLESS,
    val activeEqPreset: EqPreset = EqPreset.HI_FI_STUDIO,
    val queue: List<TrackEntity> = emptyList(),
    val parsedLyrics: List<LyricsLine> = emptyList(),
    val activeLyricIndex: Int = -1,
    val isLyricsLoading: Boolean = false,
    val lyricsSource: String = "Local Cache",
    val isLyricsSynced: Boolean = true,
    val plainLyrics: String? = null,
    val isOfflineModeOnly: Boolean = false,
    val autoCrossfade: Boolean = true,
    val crossfadeDurationSeconds: Int = 4,
    val isCrossfading: Boolean = false,
    val errorMessage: String? = null
)

class AudioPlayerManager(
    private val context: Context,
    private val lyricsRepository: LyricsRepository = LyricsRepository()
) {
    private val TAG = "AudioPlayerManager"
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var lyricsFetchJob: Job? = null
    private var fadeOutJob: Job? = null
    private var fadeInJob: Job? = null
    private var hasTriggeredAutoCrossfadeNearEnd: Boolean = false
    private var simulatedPositionMs: Long = 0L
    private var isSimulatingAudio: Boolean = false

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun setOfflineModeOnly(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isOfflineModeOnly = enabled)
    }

    fun setStreamingQuality(quality: AudioQuality) {
        _uiState.value = _uiState.value.copy(streamingQuality = quality)
    }

    fun setEqPreset(preset: EqPreset) {
        _uiState.value = _uiState.value.copy(activeEqPreset = preset)
    }

    fun setAutoCrossfade(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoCrossfade = enabled)
    }

    fun setCrossfadeDuration(seconds: Int) {
        _uiState.value = _uiState.value.copy(crossfadeDurationSeconds = seconds.coerceIn(1, 12))
    }

    fun playTrack(track: TrackEntity, newQueue: List<TrackEntity> = emptyList()) {
        if (_uiState.value.isOfflineModeOnly && !track.isDownloaded) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "'${track.title}' is not downloaded. Switch off Offline Mode or download this track."
            )
            return
        }

        val queueToUse = if (newQueue.isNotEmpty()) newQueue else {
            if (_uiState.value.queue.contains(track)) _uiState.value.queue else listOf(track)
        }

        val localLyrics = LyricsParser.parseLrc(track.lyricsLrc)
        val isAutoCrossfade = _uiState.value.autoCrossfade && _uiState.value.crossfadeDurationSeconds > 0
        val isCurrentlyPlaying = _uiState.value.isPlaying && _uiState.value.currentTrack != null
        val fadeDurationMs = (_uiState.value.crossfadeDurationSeconds * 1000L).coerceIn(500L, 12000L)

        val previousPlayer = mediaPlayer
        hasTriggeredAutoCrossfadeNearEnd = false

        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            queue = queueToUse,
            parsedLyrics = localLyrics,
            activeLyricIndex = 0,
            isLyricsLoading = true,
            lyricsSource = if (localLyrics.isNotEmpty()) "Local Cache" else "Fetching...",
            isLyricsSynced = true,
            plainLyrics = null,
            durationMs = track.durationMs,
            currentPositionMs = 0L,
            errorMessage = null,
            isPlaying = true,
            isCrossfading = isAutoCrossfade && isCurrentlyPlaying
        )

        simulatedPositionMs = 0L
        isSimulatingAudio = false

        // Fetch time-synced lyrics from API in background
        fetchLyricsFromApi(track)

        // Smooth crossfade: fade out previous active player
        if (isAutoCrossfade && isCurrentlyPlaying && previousPlayer != null) {
            fadeOutJob?.cancel()
            fadeOutJob = scope.launch {
                val steps = 20
                val stepDelay = fadeDurationMs / steps
                for (i in steps downTo 0) {
                    val vol = i.toFloat() / steps.toFloat()
                    try {
                        previousPlayer.setVolume(vol, vol)
                    } catch (e: Exception) {
                        break
                    }
                    delay(stepDelay)
                }
                try {
                    previousPlayer.stop()
                    previousPlayer.release()
                } catch (e: Exception) {
                    Log.e(TAG, "Error releasing faded out player", e)
                }
            }
        } else {
            releaseMediaPlayer()
        }

        scope.launch {
            val audioSource = InstantAudioEngine.getPlayableAudioPath(context, track)

            try {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(audioSource)
                    if (isAutoCrossfade && isCurrentlyPlaying) {
                        setVolume(0f, 0f)
                    }
                    setOnPreparedListener { mp ->
                        mp.start()
                        _uiState.value = _uiState.value.copy(
                            isPlaying = true,
                            durationMs = track.durationMs,
                            errorMessage = null
                        )
                        if (isAutoCrossfade && isCurrentlyPlaying) {
                            fadeInJob?.cancel()
                            fadeInJob = scope.launch {
                                val steps = 20
                                val stepDelay = fadeDurationMs / steps
                                for (i in 0..steps) {
                                    val vol = i.toFloat() / steps.toFloat()
                                    try {
                                        mp.setVolume(vol, vol)
                                    } catch (e: Exception) {
                                        break
                                    }
                                    delay(stepDelay)
                                }
                                _uiState.value = _uiState.value.copy(isCrossfading = false)
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(isCrossfading = false)
                        }
                    }
                    setOnCompletionListener { mp ->
                        if (simulatedPositionMs < _uiState.value.durationMs) {
                            try {
                                mp.seekTo(0)
                                mp.start()
                            } catch (e: Exception) {
                                onTrackFinished()
                            }
                        } else {
                            onTrackFinished()
                        }
                    }
                    setOnErrorListener { mp, what, extra ->
                        Log.w(TAG, "MediaPlayer recovered from $what / $extra, resuming smooth playback")
                        try {
                            mp.reset()
                            mp.setDataSource(audioSource)
                            mp.prepareAsync()
                        } catch (e: Exception) {
                            isSimulatingAudio = true
                        }
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer = player
            } catch (e: Exception) {
                Log.w(TAG, "Error initializing MediaPlayer, using simulated playback", e)
                isSimulatingAudio = true
                _uiState.value = _uiState.value.copy(isCrossfading = false)
            }
        }

        startProgressTracking()
    }

    fun reloadLyrics() {
        val currentTrack = _uiState.value.currentTrack ?: return
        fetchLyricsFromApi(currentTrack, forceRefresh = true)
    }

    private fun fetchLyricsFromApi(track: TrackEntity, forceRefresh: Boolean = false) {
        lyricsFetchJob?.cancel()
        lyricsFetchJob = scope.launch {
            _uiState.value = _uiState.value.copy(isLyricsLoading = true)
            val result = lyricsRepository.getSyncedLyrics(
                trackId = track.id,
                trackTitle = track.title,
                artist = track.artist,
                album = track.album,
                durationMs = track.durationMs,
                fallbackLrc = track.lyricsLrc
            )
            when (result) {
                is LyricsFetchResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        parsedLyrics = result.lines,
                        lyricsSource = result.source,
                        isLyricsSynced = result.isSynced,
                        plainLyrics = result.plainLyrics,
                        isLyricsLoading = false
                    )
                    updateLyricsPosition(_uiState.value.currentPositionMs)
                }
                is LyricsFetchResult.Empty -> {
                    _uiState.value = _uiState.value.copy(
                        isLyricsLoading = false,
                        lyricsSource = "Unavailable"
                    )
                }
                is LyricsFetchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        parsedLyrics = result.fallbackLines,
                        isLyricsLoading = false,
                        lyricsSource = "Local Cache"
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val currentTrack = _uiState.value.currentTrack ?: return
        if (_uiState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing player", e)
        }
        _uiState.value = _uiState.value.copy(isPlaying = false)
        stopProgressTracking()
    }

    fun resume() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming player", e)
            isSimulatingAudio = true
        }
        _uiState.value = _uiState.value.copy(isPlaying = true)
        startProgressTracking()
    }

    fun seekTo(positionMs: Long) {
        val bounded = positionMs.coerceIn(0L, _uiState.value.durationMs)
        simulatedPositionMs = bounded
        try {
            if (mediaPlayer != null) {
                val loopDuration = 30000L
                val internalPos = (bounded % loopDuration).toInt()
                mediaPlayer?.seekTo(internalPos)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Seek error", e)
        }
        updateLyricsPosition(bounded)
        _uiState.value = _uiState.value.copy(currentPositionMs = bounded)
    }

    fun skipNext() {
        val state = _uiState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val currentIndex = queue.indexOfFirst { it.id == state.currentTrack?.id }
        if (currentIndex != -1 && currentIndex + 1 < queue.size) {
            playTrack(queue[currentIndex + 1], queue)
        } else if (state.repeatMode == RepeatMode.ALL && queue.isNotEmpty()) {
            playTrack(queue.first(), queue)
        } else {
            pause()
            seekTo(0L)
        }
    }

    fun skipPrevious() {
        val state = _uiState.value
        if (state.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }
        val queue = state.queue
        val currentIndex = queue.indexOfFirst { it.id == state.currentTrack?.id }
        if (currentIndex > 0) {
            playTrack(queue[currentIndex - 1], queue)
        } else {
            seekTo(0L)
        }
    }

    fun toggleShuffle() {
        val isShuffle = !_uiState.value.isShuffle
        val currentQueue = _uiState.value.queue
        val newQueue = if (isShuffle) {
            val currentTrack = _uiState.value.currentTrack
            listOfNotNull(currentTrack) + (currentQueue.filterNot { it.id == currentTrack?.id }.shuffled())
        } else {
            currentQueue
        }
        _uiState.value = _uiState.value.copy(isShuffle = isShuffle, queue = newQueue)
    }

    fun toggleRepeat() {
        val nextMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _uiState.value = _uiState.value.copy(repeatMode = nextMode)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun onTrackFinished() {
        when (_uiState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0L)
                resume()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                skipNext()
            }
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive && _uiState.value.isPlaying) {
                simulatedPositionMs += 200L
                val pos = simulatedPositionMs
                if (pos >= _uiState.value.durationMs && _uiState.value.durationMs > 0) {
                    onTrackFinished()
                    break
                }

                val state = _uiState.value
                if (state.autoCrossfade && state.crossfadeDurationSeconds > 0 && state.durationMs > 0) {
                    val remainingMs = state.durationMs - pos
                    val crossfadeThresholdMs = state.crossfadeDurationSeconds * 1000L
                    if (remainingMs in 1L..crossfadeThresholdMs && !hasTriggeredAutoCrossfadeNearEnd && state.queue.isNotEmpty()) {
                        hasTriggeredAutoCrossfadeNearEnd = true
                        skipNext()
                    }
                }

                updateLyricsPosition(pos)
                _uiState.value = _uiState.value.copy(currentPositionMs = pos)
                delay(200L)
            }
        }
    }

    private fun updateLyricsPosition(posMs: Long) {
        val lyrics = _uiState.value.parsedLyrics
        if (lyrics.isEmpty()) return
        var activeIdx = -1
        for (i in lyrics.indices) {
            if (lyrics[i].timestampMs <= posMs) {
                activeIdx = i
            } else {
                break
            }
        }
        if (activeIdx != _uiState.value.activeLyricIndex) {
            _uiState.value = _uiState.value.copy(activeLyricIndex = activeIdx)
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releaseMediaPlayer() {
        fadeOutJob?.cancel()
        fadeOutJob = null
        fadeInJob?.cancel()
        fadeInJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        }
        mediaPlayer = null
    }

    fun updateLikedState(trackId: String, isLiked: Boolean) {
        val curr = _uiState.value.currentTrack
        val updatedCurrent = if (curr?.id == trackId) {
            curr.copy(isLiked = isLiked)
        } else {
            curr
        }
        val updatedQueue = _uiState.value.queue.map {
            if (it.id == trackId) it.copy(isLiked = isLiked) else it
        }
        _uiState.value = _uiState.value.copy(
            currentTrack = updatedCurrent,
            queue = updatedQueue
        )
    }

    fun updateDownloadedState(trackId: String, isDownloaded: Boolean) {
        val curr = _uiState.value.currentTrack
        val updatedCurrent = if (curr?.id == trackId) {
            curr.copy(isDownloaded = isDownloaded)
        } else {
            curr
        }
        val updatedQueue = _uiState.value.queue.map {
            if (it.id == trackId) it.copy(isDownloaded = isDownloaded) else it
        }
        _uiState.value = _uiState.value.copy(
            currentTrack = updatedCurrent,
            queue = updatedQueue
        )
    }

    fun release() {
        stopProgressTracking()
        releaseMediaPlayer()
    }
}
