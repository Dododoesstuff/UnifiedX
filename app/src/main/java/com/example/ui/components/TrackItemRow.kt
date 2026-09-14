package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.PlatformSource
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun TrackItemRow(
    track: TrackEntity,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    onTrackClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isSpotify = track.platformSource == PlatformSource.SPOTIFY
    val platformAccent = if (isSpotify) SpotifyGreen else YouTubeRed

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isCurrentTrack) CrossPurple.copy(alpha = 0.12f) else Color.Transparent
            )
            .clickable(onClick = onTrackClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Modern Artwork Thumbnail
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianCard),
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
                        tint = platformAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Discreet Platform Dot Badge (Bottom-End corner)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(platformAccent)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Metadata Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = if (isCurrentTrack) CrossPurple else TextPrimary,
                fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 14.5.sp,
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

                if (track.isDownloaded) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Downloaded offline",
                        tint = SpotifyGreen.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Like Button
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .size(38.dp)
                .testTag("track_like_${track.id}")
        ) {
            Icon(
                imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (track.isLiked) "Liked" else "Like",
                tint = if (track.isLiked) YouTubeRed else TextSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(19.dp)
            )
        }

        // More Options Dropdown Menu
        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .size(38.dp)
                    .testTag("track_more_${track.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(19.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(ObsidianCard)
            ) {
                DropdownMenuItem(
                    text = { Text("Add to Playlist", color = TextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onAddToPlaylistClick()
                    }
                )
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
