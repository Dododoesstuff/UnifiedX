package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.TrackEntity
import com.example.widget.MusicAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Foreground Service that keeps music playback alive when the app is minimized or closed,
 * and publishes Media Notifications with Lock Screen and background playback controls.
 */
class MediaPlaybackService : Service() {

    private val binder = LocalBinder()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var mediaSession: MediaSessionCompat? = null
    private var notificationManager: NotificationManager? = null

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlaybackService = this@MediaPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        setupMediaSession()
        observePlaybackState()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "UnifiedXMediaSession").apply {
            isActive = true
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    AudioPlayerHolder.playerManager?.resume()
                }

                override fun onPause() {
                    AudioPlayerHolder.playerManager?.pause()
                }

                override fun onSkipToNext() {
                    AudioPlayerHolder.playerManager?.skipNext()
                }

                override fun onSkipToPrevious() {
                    AudioPlayerHolder.playerManager?.skipPrevious()
                }

                override fun onSeekTo(pos: Long) {
                    AudioPlayerHolder.playerManager?.seekTo(pos)
                }

                override fun onStop() {
                    AudioPlayerHolder.playerManager?.pause()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
        }
    }

    private fun observePlaybackState() {
        serviceScope.launch {
            AudioPlayerHolder.playerManager?.uiState?.collectLatest { state ->
                updateMetadata(state)
                updatePlaybackState(state)
                MusicAppWidgetProvider.updateAllWidgets(this@MediaPlaybackService, state)
                if (state.currentTrack != null) {
                    val notification = buildNotification(state)
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    stopForeground(STOP_FOREGROUND_DETACH)
                }
            }
        }
    }

    private fun updateMetadata(state: PlayerUiState) {
        val track = state.currentTrack ?: return
        
        // Find the active lyric line for real-time display
        val activeLyric = if (state.activeLyricIndex in state.parsedLyrics.indices) {
            state.parsedLyrics[state.activeLyricIndex].text
        } else {
            "${track.artist} (${track.platformSource.displayName})"
        }

        val metadata = android.support.v4.media.MediaMetadataCompat.Builder()
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.id)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
            .putLong(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION, track.durationMs)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.coverUrl)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title)
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, activeLyric) // Real-time synced lyric
            .putString(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, track.album)
            .build()

        mediaSession?.setMetadata(metadata)
    }

    private fun updatePlaybackState(state: PlayerUiState) {
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(
                if (state.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                state.currentPositionMs,
                1.0f
            )
            .build()
        mediaSession?.setPlaybackState(playbackState)
    }

    private fun buildNotification(state: PlayerUiState): Notification {
        val track = state.currentTrack

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (state.isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                getServicePendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                getServicePendingIntent(ACTION_PLAY)
            )
        }

        val prevAction = NotificationCompat.Action(
            android.R.drawable.ic_media_previous,
            "Previous",
            getServicePendingIntent(ACTION_PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next,
            "Next",
            getServicePendingIntent(ACTION_NEXT)
        )

        val platformEmoji = if (track?.platformSource?.name == "SPOTIFY") "🟢 Spotify" else "🔴 YouTube"
        val subtitle = "${track?.artist ?: "UnifiedX"} • $platformEmoji"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track?.title ?: "Playing Music")
            .setContentText(subtitle)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(state.isPlaying)
            .addAction(prevAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun getServicePendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MediaPlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> AudioPlayerHolder.playerManager?.resume()
            ACTION_PAUSE -> AudioPlayerHolder.playerManager?.pause()
            ACTION_NEXT -> AudioPlayerHolder.playerManager?.skipNext()
            ACTION_PREVIOUS -> AudioPlayerHolder.playerManager?.skipPrevious()
            ACTION_STOP -> {
                AudioPlayerHolder.playerManager?.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls background music playback for Spotify & YouTube"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        mediaSession?.release()
        mediaSession = null
    }

    companion object {
        const val CHANNEL_ID = "unifiedx_music_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.example.player.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.player.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.player.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.player.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.player.ACTION_STOP"

        fun startService(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, MediaPlaybackService::class.java)
            context.stopService(intent)
        }
    }
}

/**
 * Singleton holder so background MediaPlaybackService and ViewModel can coordinate seamlessly.
 */
object AudioPlayerHolder {
    var playerManager: AudioPlayerManager? = null
}
