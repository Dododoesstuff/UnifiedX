package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.collab.CollabQueueItem
import com.example.collab.CollabSessionState
import com.example.collab.Participant
import com.example.data.local.TrackEntity
import com.example.data.model.PlatformSource
import com.example.player.PlayerUiState
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun CollabSessionScreen(
    collabState: CollabSessionState,
    allTracks: List<TrackEntity>,
    playerState: PlayerUiState,
    onHostSession: (String) -> Unit,
    onJoinSession: (String) -> Unit,
    onLeaveSession: () -> Unit,
    onAddTrackToQueue: (TrackEntity) -> Unit,
    onUpvoteTrack: (String) -> Unit,
    onShareInvite: () -> Unit,
    onPlayTrack: (TrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var joinCodeInput by remember { mutableStateOf("") }
    var hostNameInput by remember { mutableStateOf("Cross-Platform Jam") }
    var showAddTrackPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .testTag("collab_session_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Collaborative Jam",
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Listen live with friends across Spotify & YouTube",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (collabState.isInSession) {
                        Button(
                            onClick = onLeaveSession,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("leave_session_button")
                        ) {
                            Text("Leave", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Session or Join/Host Controls
        if (!collabState.isInSession) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Host a New Jam Session",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Invite friends to queue songs from both Spotify and YouTube in real time.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = hostNameInput,
                            onValueChange = { hostNameInput = it },
                            label = { Text("Session Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrossPurple,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("host_session_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onHostSession(hostNameInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("host_session_button")
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Jam Session", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Join with Room Code",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter the 6-character room code shared by your friend.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = joinCodeInput,
                                onValueChange = { joinCodeInput = it },
                                placeholder = { Text("e.g. JAM-777", color = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CrossPurple,
                                    unfocusedBorderColor = ObsidianStroke,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("join_code_input")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {
                                    if (joinCodeInput.isNotBlank()) onJoinSession(joinCodeInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("join_session_button")
                            ) {
                                Text("Join", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // In Active Session
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Active Room Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("active_collab_room_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF28134F),
                                        Color(0xFF131D3B)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "LIVE COLLAB SESSION",
                                        color = ElectricViolet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = collabState.sessionName,
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Room Code Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = collabState.sessionCode,
                                        color = SpotifyGreen,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Invite Friends Button
                            Button(
                                onClick = onShareInvite,
                                colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("invite_collab_friends_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Invite Link", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Participant Avatars
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Connected Listeners (${collabState.participants.size})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(collabState.participants) { participant ->
                            ParticipantAvatarItem(participant = participant)
                        }
                    }
                }
            }

            // Collaborative Live Queue Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collaborative Live Queue",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddTrackPicker = !showAddTrackPicker },
                        colors = ButtonDefaults.buttonColors(containerColor = CrossPurple.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("toggle_add_track_to_collab_queue")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CrossPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Song", color = CrossPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quick Track Picker if open
            if (showAddTrackPicker) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Select a Spotify or YouTube track to queue:",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(allTracks) { track ->
                                    Box(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black.copy(alpha = 0.3f))
                                            .clickable {
                                                onAddTrackToQueue(track)
                                                showAddTrackPicker = false
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = track.title,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (track.platformSource == PlatformSource.SPOTIFY) "Spotify 🟢" else "YouTube 🔴",
                                                color = if (track.platformSource == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (collabState.collaborativeQueue.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Queue is empty. Tap 'Add Song' to queue tracks from Spotify or YouTube!",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(collabState.collaborativeQueue) { queueItem ->
                    CollabQueueItemRow(
                        item = queueItem,
                        onUpvote = { onUpvoteTrack(queueItem.id) },
                        onPlay = { onPlayTrack(queueItem.track) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantAvatarItem(participant: Participant) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(participant.avatarColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.name.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = participant.name,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CollabQueueItemRow(
    item: CollabQueueItem,
    onUpvote: () -> Unit,
    onPlay: () -> Unit
) {
    val track = item.track
    val sourceColor = if (track.platformSource == PlatformSource.SPOTIFY) SpotifyGreen else YouTubeRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianCard)
            .clickable(onClick = onPlay)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (track.platformSource == PlatformSource.SPOTIFY) "Spotify • " else "YouTube • ",
                    color = sourceColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Added by ${item.addedBy}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Upvote Pill Button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (item.hasVoted) CrossPurple else Color.White.copy(alpha = 0.08f))
                .clickable(onClick = onUpvote)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Upvote",
                tint = if (item.hasVoted) Color.White else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${item.votes}",
                color = if (item.hasVoted) Color.White else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
