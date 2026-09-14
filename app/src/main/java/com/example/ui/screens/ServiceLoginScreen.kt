package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PlatformSource
import com.example.sync.SyncState
import com.example.ui.theme.BabyBlue
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.YouTubeRed

/**
 * Modern, guided authentication and service linking screen.
 * Allows users to link their Spotify and YouTube accounts or continue as guest / test mode.
 */
@Composable
fun ServiceLoginScreen(
    syncState: SyncState,
    onLinkSpotify: (token: String, username: String) -> Unit,
    onLinkYouTube: (apiKey: String, channelName: String) -> Unit,
    onStartSync: () -> Unit,
    onContinueToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf<PlatformSource?>(PlatformSource.SPOTIFY) }
    
    // Spotify input states
    var spotifyToken by remember { mutableStateOf(syncState.spotifyAccount.tokenOrApiKey) }
    var spotifyUsername by remember { mutableStateOf(syncState.spotifyAccount.accountUsername) }
    var showSpotifyToken by remember { mutableStateOf(false) }

    // YouTube input states
    var youtubeApiKey by remember { mutableStateOf(syncState.youtubeAccount.tokenOrApiKey) }
    var youtubeChannelName by remember { mutableStateOf(syncState.youtubeAccount.accountUsername) }
    var showYouTubeKey by remember { mutableStateOf(false) }

    var showGuideTip by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isSpotifyConnected = syncState.spotifyAccount.isConnected && syncState.spotifyAccount.tokenOrApiKey.isNotBlank()
    val isYouTubeConnected = syncState.youtubeAccount.isConnected && syncState.youtubeAccount.tokenOrApiKey.isNotBlank()
    val bothConnected = isSpotifyConnected && isYouTubeConnected

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .imePadding()
            .testTag("service_login_screen"),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Hero Branding
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CrossPurple.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unifiedx_logo_modern),
                    contentDescription = "UnifiedX",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Link Music Services",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Connect your Spotify & YouTube accounts to enjoy synchronized playlists, lossless audio, and real-time collaborative listening.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Connection Summary Pill Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Spotify Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { activeTab = PlatformSource.SPOTIFY }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isSpotifyConnected) SpotifyGreen else TextTertiary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Spotify",
                                color = if (activeTab == PlatformSource.SPOTIFY) SpotifyGreen else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSpotifyConnected) "Linked" else "Not Linked",
                                color = if (isSpotifyConnected) SpotifyGreen else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(ObsidianStroke)
                    )

                    // YouTube Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { activeTab = PlatformSource.YOUTUBE }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isYouTubeConnected) YouTubeRed else TextTertiary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "YouTube",
                                color = if (activeTab == PlatformSource.YOUTUBE) YouTubeRed else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isYouTubeConnected) "Linked" else "Not Linked",
                                color = if (isYouTubeConnected) YouTubeRed else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Service Selection Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Spotify Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == PlatformSource.SPOTIFY) SpotifyGreen.copy(alpha = 0.15f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (activeTab == PlatformSource.SPOTIFY) SpotifyGreen else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { activeTab = PlatformSource.SPOTIFY }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🟢", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Spotify Account",
                            color = if (activeTab == PlatformSource.SPOTIFY) SpotifyGreen else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // YouTube Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == PlatformSource.YOUTUBE) YouTubeRed.copy(alpha = 0.15f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (activeTab == PlatformSource.YOUTUBE) YouTubeRed else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { activeTab = PlatformSource.YOUTUBE }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔴", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YouTube Data API",
                            color = if (activeTab == PlatformSource.YOUTUBE) YouTubeRed else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Linking Card
        item {
            if (activeTab == PlatformSource.SPOTIFY) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("spotify_login_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SpotifyGreen.copy(alpha = 0.4f), ObsidianStroke)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Spotify Authorization",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Saved tracks, playlists & profile sync",
                                        color = TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            if (isSpotifyConnected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Connected",
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username input
                        OutlinedTextField(
                            value = spotifyUsername,
                            onValueChange = { spotifyUsername = it },
                            label = { Text("Spotify User / Display Name") },
                            placeholder = { Text("e.g. kodadavid.spotify", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SpotifyGreen)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("spotify_username_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Access Token / OAuth Bearer Token Input
                        OutlinedTextField(
                            value = spotifyToken,
                            onValueChange = { spotifyToken = it },
                            label = { Text("Spotify Access Token / Bearer Token") },
                            placeholder = { Text("Bearer sp_tok_...", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = SpotifyGreen)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showSpotifyToken = !showSpotifyToken }) {
                                    Icon(
                                        imageVector = if (showSpotifyToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle token visibility",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showSpotifyToken) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("spotify_token_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Connect Button & Quick Demo Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    onLinkSpotify(spotifyToken, spotifyUsername)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("save_spotify_account_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Link Spotify", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    spotifyUsername = "kodadavid.spotify"
                                    spotifyToken = "sp_tok_live_89127391"
                                    onLinkSpotify("sp_tok_live_89127391", "kodadavid.spotify")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Auto Fill Demo", color = SpotifyGreen, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("youtube_login_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(YouTubeRed.copy(alpha = 0.4f), ObsidianStroke)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(YouTubeRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = YouTubeRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "YouTube Data API v3",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Video search, channel music & playlists",
                                        color = TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            if (isYouTubeConnected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Connected",
                                    tint = YouTubeRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Channel / Username input
                        OutlinedTextField(
                            value = youtubeChannelName,
                            onValueChange = { youtubeChannelName = it },
                            label = { Text("Channel / Account Name") },
                            placeholder = { Text("e.g. David Koda Music", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = YouTubeRed)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YouTubeRed,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("youtube_channel_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // YouTube API Key input
                        OutlinedTextField(
                            value = youtubeApiKey,
                            onValueChange = { youtubeApiKey = it },
                            label = { Text("Google Cloud / YouTube API Key") },
                            placeholder = { Text("AIzaSy...", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = YouTubeRed)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showYouTubeKey = !showYouTubeKey }) {
                                    Icon(
                                        imageVector = if (showYouTubeKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle key visibility",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showYouTubeKey) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YouTubeRed,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("youtube_key_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Connect Button & Quick Demo Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    onLinkYouTube(youtubeApiKey, youtubeChannelName)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("save_youtube_account_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Link YouTube", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    youtubeChannelName = "David Koda Music"
                                    youtubeApiKey = "AIzaSyYT_mockKey_98124"
                                    onLinkYouTube("AIzaSyYT_mockKey_98124", "David Koda Music")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Auto Fill Demo", color = YouTubeRed, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cross-Platform Sync Action Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BabyBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cross-Platform Library Sync",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onStartSync,
                            enabled = !syncState.isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sync_now_login_button")
                        ) {
                            if (syncState.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Syncing...", color = Color.White, fontSize = 11.5.sp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Catalog", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }
                        }
                    }

                    if (syncState.isSyncing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = syncState.currentStepDescription,
                            color = ElectricViolet,
                            fontSize = 11.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Matched ${syncState.matchedSongsCount} tracks across Spotify & YouTube • Last sync: ${syncState.lastSyncFormatted}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Primary Proceed Button
        item {
            Button(
                onClick = onContinueToApp,
                colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_to_unified_player_button")
            ) {
                Text(
                    text = if (bothConnected) "Enter UnifiedX Player" else "Continue with Available Sources",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guest / Offline Mode Option
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onContinueToApp() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Browse Offline Vault & Stored Playlists",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // How to get keys guide dialog trigger
        item {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showGuideTip = !showGuideTip }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = HiResGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showGuideTip) "Hide Developer Guide" else "How to obtain Spotify & YouTube API Keys?",
                    color = HiResGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(
                visible = showGuideTip,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔑 Spotify Web API:",
                            color = SpotifyGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Visit developer.spotify.com/dashboard, create an app, and generate a user token or use the 'Auto Fill Demo' button above for instant evaluation.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        HorizontalDivider(color = ObsidianStroke, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔑 YouTube Data API v3:",
                            color = YouTubeRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Visit console.cloud.google.com, enable 'YouTube Data API v3', generate an API Key, and paste it here. All credentials are encrypted and stored locally in Room.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
