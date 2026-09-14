package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.TrackEntity
import com.example.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Android Auto and Automotive MediaBrowserService.
 * Exposes UnifiedX media browsing hierarchy (Spotify & YouTube playlists, liked tracks, recent music)
 * directly to the in-car head unit display and steering wheel media controls.
 */
class AutoMediaBrowserService : MediaBrowserServiceCompat() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var mediaSession: MediaSessionCompat? = null
    private var notificationManager: NotificationManager? = null
    private var repository: MusicRepository? = null

    companion object {
        const val MEDIA_ROOT_ID = "unifiedx_media_root"
        const val CATEGORY_SPOTIFY = "cat_spotify"
        const val CATEGORY_YOUTUBE = "cat_youtube"
        const val CATEGORY_LIKED = "cat_liked"
        const val CATEGORY_DOWNLOADED = "cat_downloaded"
        const val CATEGORY_ALL = "cat_all"

        const val CHANNEL_ID = "unifiedx_auto_playback_channel"
        const val NOTIFICATION_ID = 1002
    }

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(applicationContext)
        repository = MusicRepository(database)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        setupMediaSession()
        observePlaybackState()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "UnifiedXAutoMediaSession").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

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

                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                    mediaId?.let { id ->
                        serviceScope.launch {
                            val track = repository?.getTrackById(id)
                            if (track != null) {
                                val all = repository?.allTracks?.first() ?: listOf(track)
                                AudioPlayerHolder.playerManager?.playTrack(track, all)
                            }
                        }
                    }
                }

                override fun onStop() {
                    AudioPlayerHolder.playerManager?.pause()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })

            isActive = true
            setSessionToken(sessionToken)
        }
    }

    private fun observePlaybackState() {
        serviceScope.launch {
            AudioPlayerHolder.playerManager?.uiState?.collectLatest { state ->
                updatePlaybackState(state)
                updateMetadata(state.currentTrack)
                if (state.currentTrack != null) {
                    val notification = buildNotification(state)
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    stopForeground(STOP_FOREGROUND_DETACH)
                }
            }
        }
    }

    private fun updateMetadata(track: TrackEntity?) {
        if (track == null) return
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.id)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.durationMs)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.coverUrl)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "${track.artist} (${track.platformSource.displayName})")
            .build()
        mediaSession?.setMetadata(metadata)
    }

    private fun updatePlaybackState(state: PlayerUiState) {
        val actions = PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(actions)
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
                getServicePendingIntent(MediaPlaybackService.ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                getServicePendingIntent(MediaPlaybackService.ACTION_PLAY)
            )
        }

        val prevAction = NotificationCompat.Action(
            android.R.drawable.ic_media_previous,
            "Previous",
            getServicePendingIntent(MediaPlaybackService.ACTION_PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next,
            "Next",
            getServicePendingIntent(MediaPlaybackService.ACTION_NEXT)
        )

        val platformEmoji = if (track?.platformSource?.name == "SPOTIFY") "🟢 Spotify" else "🔴 YouTube"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track?.title ?: "Android Auto Playback")
            .setContentText("${track?.artist ?: "UnifiedX"} • $platformEmoji")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(state.isPlaying)
            .setOnlyAlertOnce(true)
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Android Auto & Car Media",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls car head-unit and Android Auto audio streaming"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        serviceScope.launch {
            val items = mutableListOf<MediaBrowserCompat.MediaItem>()
            when (parentId) {
                MEDIA_ROOT_ID -> {
                    // Top level automotive menus
                    items.add(createBrowsableItem(CATEGORY_ALL, "All Unified Tracks", "Full cross-platform catalog"))
                    items.add(createBrowsableItem(CATEGORY_SPOTIFY, "Spotify Hits", "Sync'd Spotify library"))
                    items.add(createBrowsableItem(CATEGORY_YOUTUBE, "YouTube Audio", "Audio streams from YouTube"))
                    items.add(createBrowsableItem(CATEGORY_LIKED, "Liked Tracks", "Your favorite tracks"))
                    items.add(createBrowsableItem(CATEGORY_DOWNLOADED, "Downloaded / Offline", "Playable with no signal"))
                }
                CATEGORY_ALL -> {
                    val tracks = repository?.allTracks?.first() ?: emptyList()
                    tracks.forEach { items.add(createPlayableItem(it)) }
                }
                CATEGORY_SPOTIFY -> {
                    val tracks = repository?.allTracks?.first()?.filter { it.platformSource.name == "SPOTIFY" } ?: emptyList()
                    tracks.forEach { items.add(createPlayableItem(it)) }
                }
                CATEGORY_YOUTUBE -> {
                    val tracks = repository?.allTracks?.first()?.filter { it.platformSource.name == "YOUTUBE" } ?: emptyList()
                    tracks.forEach { items.add(createPlayableItem(it)) }
                }
                CATEGORY_LIKED -> {
                    val tracks = repository?.likedTracks?.first() ?: emptyList()
                    tracks.forEach { items.add(createPlayableItem(it)) }
                }
                CATEGORY_DOWNLOADED -> {
                    val tracks = repository?.downloadedTracks?.first() ?: emptyList()
                    tracks.forEach { items.add(createPlayableItem(it)) }
                }
            }
            result.sendResult(items)
        }
    }

    private fun createBrowsableItem(id: String, title: String, subtitle: String): MediaBrowserCompat.MediaItem {
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(id)
            .setTitle(title)
            .setSubtitle(subtitle)
            .build()
        return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun createPlayableItem(track: TrackEntity): MediaBrowserCompat.MediaItem {
        val platformTag = if (track.platformSource.name == "SPOTIFY") "🟢 Spotify" else "🔴 YouTube"
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(track.id)
            .setTitle(track.title)
            .setSubtitle("${track.artist} • $platformTag")
            .setDescription(track.album)
            .setIconUri(Uri.parse(track.coverUrl))
            .setMediaUri(Uri.parse(track.streamUrl))
            .build()
        return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        mediaSession?.release()
        mediaSession = null
    }
}
