package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.DownloadStatus
import com.example.data.model.PlatformSource
import com.example.data.model.TrackDownloadState
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppPrimary
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HiResGold
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun TrackItemRow(
    track: TrackEntity,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    downloadState: TrackDownloadState = if (track.isDownloaded) TrackDownloadState(DownloadStatus.DOWNLOADED) else TrackDownloadState(DownloadStatus.NOT_DOWNLOADED),
    onTrackClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onShareClick: () -> Unit,
    onRetryDownload: (() -> Unit)? = null,
    onSwitchPlatformCounterpart: ((TrackEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isSpotify = track.platformSource == PlatformSource.SPOTIFY
    val platformAccent = if (isSpotify) SpotifyGreen else YouTubeRed

    val rowShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(rowShape)
            .border(
                border = BorderStroke(
                    width = if (isCurrentTrack) 1.5.dp else 1.dp,
                    color = if (isCurrentTrack) AppPrimary else AppBorder
                ),
                shape = rowShape
            )
            .background(
                if (isCurrentTrack) AppSurfaceElevated else AppSurface
            )
            .clickable(onClick = onTrackClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, AppBorder, RoundedCornerShape(8.dp))
                .background(AppBorder),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = "${track.title} artwork",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // Playing Overlay with Equalizer Indicator
            if (isCurrentTrack) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Playing" else "Paused",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Platform Source Indicator Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(2.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(platformAccent)
                    .border(1.dp, Color.Black, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Metadata Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = if (isCurrentTrack) AppPrimary else TextPrimary,
                fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = track.artist,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (track.audioQuality == AudioQuality.LOSSLESS) {
                    Text(
                        text = "LOSSLESS",
                        color = HiResGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(HiResGold.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                if (onSwitchPlatformCounterpart != null && (track.spotifyEquivalentId != null || track.youtubeEquivalentId != null)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSpotify) YouTubeRed.copy(alpha = 0.18f) else SpotifyGreen.copy(alpha = 0.18f))
                            .clickable { onSwitchPlatformCounterpart(track) }
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (isSpotify) "⇄ YouTube 🔴" else "⇄ Spotify 🟢",
                            color = if (isSpotify) YouTubeRed else SpotifyGreen,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                when (downloadState.status) {
                    DownloadStatus.DOWNLOADED -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Downloaded offline",
                                tint = AppPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Downloaded",
                                color = AppPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DownloadStatus.DOWNLOADING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { (downloadState.progressPercent / 100f).coerceIn(0.05f, 1f) },
                                modifier = Modifier.size(10.dp),
                                color = AppPrimary,
                                strokeWidth = 1.5.dp
                            )
                            Text(
                                text = "Downloading ${downloadState.progressPercent}%",
                                color = AppPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DownloadStatus.PENDING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(HiResGold.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = "Pending download",
                                tint = HiResGold,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Pending",
                                color = HiResGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DownloadStatus.ERROR -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ErrorRed.copy(alpha = 0.15f))
                                .clickable { onRetryDownload?.invoke() ?: onDownloadClick() }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Download error",
                                tint = ErrorRed,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Failed (Retry)",
                                color = ErrorRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DownloadStatus.NOT_DOWNLOADED -> {
                        // Un-downloaded state
                    }
                }
            }
        }

        // Like Button
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .size(36.dp)
                .testTag("track_like_${track.id}")
        ) {
            Icon(
                imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (track.isLiked) "Liked" else "Like",
                tint = if (track.isLiked) AppPrimary else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        // Download Quick Action Button
        when (downloadState.status) {
            DownloadStatus.DOWNLOADING -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("track_download_status_${track.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = AppPrimary,
                        strokeWidth = 2.dp
                    )
                }
            }
            DownloadStatus.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("track_download_status_${track.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Pending download",
                        tint = HiResGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DownloadStatus.ERROR -> {
                IconButton(
                    onClick = { onRetryDownload?.invoke() ?: onDownloadClick() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("track_download_retry_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Retry download",
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DownloadStatus.DOWNLOADED -> {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("track_download_remove_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DownloadDone,
                        contentDescription = "Downloaded offline. Tap to remove.",
                        tint = AppPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DownloadStatus.NOT_DOWNLOADED -> {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("track_download_start_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DownloadForOffline,
                        contentDescription = "Download track",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // More Options Dropdown Menu
        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("track_more_${track.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(AppSurfaceElevated)
            ) {
                DropdownMenuItem(
                    text = { Text("Add to Playlist", color = TextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onAddToPlaylistClick()
                    }
                )
                if (onSwitchPlatformCounterpart != null) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isSpotify) "Switch to YouTube Music" else "Switch to Spotify",
                                color = if (isSpotify) YouTubeRed else SpotifyGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onSwitchPlatformCounterpart(track)
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (track.isDownloaded) "Remove Download" else "Download Offline",
                            color = TextPrimary
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onDownloadClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share Track", color = TextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onShareClick()
                    }
                )
            }
        }
    }
}
