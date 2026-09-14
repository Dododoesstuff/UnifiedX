package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.RemoteViews
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.MainActivity
import com.example.R
import com.example.data.local.TrackEntity
import com.example.player.AudioPlayerHolder
import com.example.player.MediaPlaybackService
import com.example.player.PlayerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AppWidgetProvider for the UnifiedX Home Screen Player Widget.
 * Displays real-time track metadata (title, artist, platform badge) and provides
 * instant playback controls (Previous, Play/Pause, Next) with album art rendering.
 */
class MusicAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val playerState = AudioPlayerHolder.playerManager?.uiState?.value
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, playerState)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_WIDGET_PLAY_PAUSE -> {
                AudioPlayerHolder.playerManager?.togglePlayPause()
                updateAllWidgets(context)
            }
            ACTION_WIDGET_PREVIOUS -> {
                AudioPlayerHolder.playerManager?.skipPrevious()
                updateAllWidgets(context)
            }
            ACTION_WIDGET_NEXT -> {
                AudioPlayerHolder.playerManager?.skipNext()
                updateAllWidgets(context)
            }
            ACTION_WIDGET_UPDATE_STATE -> {
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_PLAY_PAUSE = "com.example.widget.ACTION_PLAY_PAUSE"
        const val ACTION_WIDGET_PREVIOUS = "com.example.widget.ACTION_PREVIOUS"
        const val ACTION_WIDGET_NEXT = "com.example.widget.ACTION_NEXT"
        const val ACTION_WIDGET_UPDATE_STATE = "com.example.widget.ACTION_UPDATE_STATE"

        private val widgetScope = CoroutineScope(Dispatchers.IO + Job())

        fun updateAllWidgets(context: Context, state: PlayerUiState? = null) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MusicAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isEmpty()) return

            val playerState = state ?: AudioPlayerHolder.playerManager?.uiState?.value
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, playerState)
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            playerState: PlayerUiState?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_music_player)

            // Click entire widget to launch MainActivity
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val appPendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // PendingIntents for controls
            views.setOnClickPendingIntent(
                R.id.widget_btn_previous,
                getBroadcastPendingIntent(context, ACTION_WIDGET_PREVIOUS, 101)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_play_pause,
                getBroadcastPendingIntent(context, ACTION_WIDGET_PLAY_PAUSE, 102)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_next,
                getBroadcastPendingIntent(context, ACTION_WIDGET_NEXT, 103)
            )

            val track = playerState?.currentTrack
            if (track != null) {
                views.setTextViewText(R.id.widget_track_title, track.title)
                views.setTextViewText(R.id.widget_track_artist, track.artist)

                val sourceBadge = if (track.platformSource.name == "SPOTIFY") "🟢 Spotify" else "🔴 YouTube"
                views.setTextViewText(R.id.widget_track_source, sourceBadge)

                val playPauseIcon = if (playerState.isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                }
                views.setImageViewResource(R.id.widget_btn_play_pause, playPauseIcon)

                // Load artwork asynchronously using Coil
                if (track.coverUrl.isNotBlank()) {
                    loadArtworkAsync(context, track.coverUrl, appWidgetManager, appWidgetId, views)
                } else {
                    views.setImageViewResource(R.id.widget_album_art, R.mipmap.ic_launcher)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } else {
                views.setTextViewText(R.id.widget_track_title, "No track playing")
                views.setTextViewText(R.id.widget_track_artist, "Tap to open UnifiedX")
                views.setTextViewText(R.id.widget_track_source, "UnifiedX Audio")
                views.setImageViewResource(R.id.widget_btn_play_pause, android.R.drawable.ic_media_play)
                views.setImageViewResource(R.id.widget_album_art, R.mipmap.ic_launcher)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        private fun getBroadcastPendingIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent {
            val intent = Intent(context, MusicAppWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun loadArtworkAsync(
            context: Context,
            url: String,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews
        ) {
            widgetScope.launch {
                try {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .allowHardware(false) // Software bitmap required for RemoteViews
                        .build()
                    val result = (loader.execute(request) as? SuccessResult)?.drawable
                    val bitmap = (result as? BitmapDrawable)?.bitmap
                    if (bitmap != null) {
                        views.setImageViewBitmap(R.id.widget_album_art, bitmap)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    } else {
                        views.setImageViewResource(R.id.widget_album_art, R.mipmap.ic_launcher)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    Log.e("MusicAppWidget", "Error loading widget artwork", e)
                    views.setImageViewResource(R.id.widget_album_art, R.mipmap.ic_launcher)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
