package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.PlatformSource
import com.example.sync.AccountPresets
import com.example.sync.DemoProfilePreset
import com.example.sync.SyncState
import com.example.ui.theme.BabyBlue
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardHover
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.YouTubeRed

enum class ServiceAuthTab {
    SPOTIFY,
    YOUTUBE,
    SYNC_HUB
}

/**
 * Reimagined, high-polish authentication and service linking screen.
 * Supports 1-click verified account connections, custom API tokens, and real-time cross-platform sync.
 */
@Composable
fun ServiceLoginScreen(
    syncState: SyncState,
    onLinkSpotify: (token: String, username: String) -> Unit,
    onLinkYouTube: (apiKey: String, channelName: String) -> Unit,
    onApplyPreset: ((DemoProfilePreset) -> Unit)? = null,
    onDisconnectPlatform: ((PlatformSource) -> Unit)? = null,
    onStartSync: () -> Unit,
    onContinueToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ServiceAuthTab.SPOTIFY) }
    
    // Spotify custom inputs
    var spotifyToken by remember { mutableStateOf(syncState.spotifyAccount.tokenOrApiKey) }
    var spotifyUsername by remember { mutableStateOf(syncState.spotifyAccount.accountUsername) }
    var showSpotifyToken by remember { mutableStateOf(false) }
    var showCustomSpotifyInput by remember { mutableStateOf(false) }

    // YouTube custom inputs
    var youtubeApiKey by remember { mutableStateOf(syncState.youtubeAccount.tokenOrApiKey) }
    var youtubeChannelName by remember { mutableStateOf(syncState.youtubeAccount.accountUsername) }
    var showYouTubeKey by remember { mutableStateOf(false) }
    var showCustomYouTubeInput by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val isSpotifyConnected = syncState.spotifyAccount.isConnected && syncState.spotifyAccount.tokenOrApiKey.isNotBlank()
    val isYouTubeConnected = syncState.youtubeAccount.isConnected && syncState.youtubeAccount.tokenOrApiKey.isNotBlank()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .imePadding()
            .testTag("service_login_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onContinueToApp,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ObsidianSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isSpotifyConnected && isYouTubeConnected) SpotifyGreen else HiResGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpotifyConnected && isYouTubeConnected) "2 of 2 Services Linked" else if (isSpotifyConnected || isYouTubeConnected) "1 of 2 Services Linked" else "No Services Linked",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onStartSync,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ObsidianSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Quick Sync",
                        tint = CrossPurple
                    )
                }
            }
        }

        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CrossPurple.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unifiedx_logo_modern),
                    contentDescription = "UnifiedX",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Connect Accounts & Sync",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sign in to Spotify & YouTube to synchronize your playlists, stream lossless audio, and match liked songs in one unified vault.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Navigation Tab Pills
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
                val isSpotifyActive = activeTab == ServiceAuthTab.SPOTIFY
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSpotifyActive) SpotifyGreen.copy(alpha = 0.16f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSpotifyActive) SpotifyGreen else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { activeTab = ServiceAuthTab.SPOTIFY }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isSpotifyConnected) SpotifyGreen else TextTertiary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Spotify",
                            color = if (isSpotifyActive) SpotifyGreen else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // YouTube Tab
                val isYouTubeActive = activeTab == ServiceAuthTab.YOUTUBE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isYouTubeActive) YouTubeRed.copy(alpha = 0.16f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isYouTubeActive) YouTubeRed else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { activeTab = ServiceAuthTab.YOUTUBE }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isYouTubeConnected) YouTubeRed else TextTertiary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YouTube",
                            color = if (isYouTubeActive) YouTubeRed else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Sync Hub Tab
                val isSyncActive = activeTab == ServiceAuthTab.SYNC_HUB
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSyncActive) CrossPurple.copy(alpha = 0.20f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSyncActive) CrossPurple else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { activeTab = ServiceAuthTab.SYNC_HUB }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = if (isSyncActive) CrossPurple else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sync Hub",
                            color = if (isSyncActive) CrossPurple else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // ================= SPOTIFY TAB =================
        if (activeTab == ServiceAuthTab.SPOTIFY) {
            // Connected Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("spotify_account_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(
                                if (isSpotifyConnected) SpotifyGreen.copy(alpha = 0.5f) else ObsidianStroke,
                                ObsidianStroke
                            )
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (syncState.spotifyAccount.avatarUrl != null) {
                                        AsyncImage(
                                            model = syncState.spotifyAccount.avatarUrl,
                                            contentDescription = "Spotify Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = SpotifyGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isSpotifyConnected) syncState.spotifyAccount.accountUsername else "Spotify Disconnected",
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isSpotifyConnected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = SpotifyGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isSpotifyConnected) syncState.spotifyAccount.profileTier else "Connect to stream lossless tracks",
                                        color = if (isSpotifyConnected) SpotifyGreen else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (isSpotifyConnected && onDisconnectPlatform != null) {
                                OutlinedButton(
                                    onClick = { onDisconnectPlatform(PlatformSource.SPOTIFY) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Disconnect", fontSize = 11.sp)
                                }
                            }
                        }

                        if (isSpotifyConnected) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = ObsidianStroke.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LibraryMusic,
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${syncState.spotifyAccount.syncedItemsCount} Synced Spotify Tracks",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = HiResGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Hi-Res Master",
                                        color = HiResGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Demo Sign-In Profiles
            item {
                Text(
                    text = "Sign In with Verified Spotify Profile",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccountPresets.spotifyPresets.forEach { preset ->
                        val isSelected = isSpotifyConnected && syncState.spotifyAccount.accountUsername == preset.username
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (onApplyPreset != null) {
                                        onApplyPreset(preset)
                                    } else {
                                        onLinkSpotify(preset.tokenOrKey, preset.username)
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SpotifyGreen.copy(alpha = 0.12f) else ObsidianSurface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        if (isSelected) SpotifyGreen else ObsidianStroke,
                                        if (isSelected) SpotifyGreen.copy(alpha = 0.5f) else ObsidianStroke
                                    )
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = preset.avatarUrl,
                                        contentDescription = preset.username,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = preset.username,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = preset.tier,
                                            color = SpotifyGreen,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SpotifyGreen)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Active",
                                            color = ObsidianDeep,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (onApplyPreset != null) {
                                                onApplyPreset(preset)
                                            } else {
                                                onLinkSpotify(preset.tokenOrKey, preset.username)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Connect", color = Color.Black, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom Token Input Collapsible
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomSpotifyInput = !showCustomSpotifyInput }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manual Spotify OAuth Bearer Token",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = if (showCustomSpotifyInput) "Hide ▲" else "Enter Token ▼",
                        color = SpotifyGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = showCustomSpotifyInput) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurface)
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = spotifyUsername,
                            onValueChange = { spotifyUsername = it },
                            label = { Text("Display Name") },
                            placeholder = { Text("e.g. DJ Spotify Lossless", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SpotifyGreen) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianCard,
                                unfocusedContainerColor = ObsidianCard
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = spotifyToken,
                            onValueChange = { spotifyToken = it },
                            label = { Text("Spotify OAuth Access Token") },
                            placeholder = { Text("Bearer BQ...", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = SpotifyGreen) },
                            trailingIcon = {
                                IconButton(onClick = { showSpotifyToken = !showSpotifyToken }) {
                                    Icon(
                                        imageVector = if (showSpotifyToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle token",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showSpotifyToken) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianCard,
                                unfocusedContainerColor = ObsidianCard
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onLinkSpotify(spotifyToken, spotifyUsername)
                                focusManager.clearFocus()
                            },
                            enabled = spotifyToken.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save & Authenticate Spotify", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // ================= YOUTUBE TAB =================
        if (activeTab == ServiceAuthTab.YOUTUBE) {
            // Connected Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("youtube_account_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(
                                if (isYouTubeConnected) YouTubeRed.copy(alpha = 0.5f) else ObsidianStroke,
                                ObsidianStroke
                            )
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(YouTubeRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (syncState.youtubeAccount.avatarUrl != null) {
                                        AsyncImage(
                                            model = syncState.youtubeAccount.avatarUrl,
                                            contentDescription = "YouTube Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = YouTubeRed,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isYouTubeConnected) syncState.youtubeAccount.accountUsername else "YouTube Disconnected",
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isYouTubeConnected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = YouTubeRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isYouTubeConnected) syncState.youtubeAccount.userEmailOrId.ifBlank { "YouTube Music Studio 4K" } else "Connect to stream live tracks & acoustic sessions",
                                        color = if (isYouTubeConnected) YouTubeRed else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (isYouTubeConnected && onDisconnectPlatform != null) {
                                OutlinedButton(
                                    onClick = { onDisconnectPlatform(PlatformSource.YOUTUBE) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Disconnect", fontSize = 11.sp)
                                }
                            }
                        }

                        if (isYouTubeConnected) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = ObsidianStroke.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = YouTubeRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${syncState.youtubeAccount.syncedItemsCount} Synced Live Videos & Streams",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BabyBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "4K Audio HDR",
                                        color = BabyBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Demo Channel Presets
            item {
                Text(
                    text = "Sign In with Verified YouTube Channel",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccountPresets.youtubePresets.forEach { preset ->
                        val isSelected = isYouTubeConnected && syncState.youtubeAccount.accountUsername == preset.username
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (onApplyPreset != null) {
                                        onApplyPreset(preset)
                                    } else {
                                        onLinkYouTube(preset.tokenOrKey, preset.username)
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) YouTubeRed.copy(alpha = 0.12f) else ObsidianSurface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        if (isSelected) YouTubeRed else ObsidianStroke,
                                        if (isSelected) YouTubeRed.copy(alpha = 0.5f) else ObsidianStroke
                                    )
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = preset.avatarUrl,
                                        contentDescription = preset.username,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = preset.username,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = preset.emailOrHandle,
                                            color = YouTubeRed,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(YouTubeRed)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Active",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (onApplyPreset != null) {
                                                onApplyPreset(preset)
                                            } else {
                                                onLinkYouTube(preset.tokenOrKey, preset.username)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Connect", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom YouTube Data API Key
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomYouTubeInput = !showCustomYouTubeInput }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = YouTubeRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manual YouTube Data API Key v3",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = if (showCustomYouTubeInput) "Hide ▲" else "Enter Key ▼",
                        color = YouTubeRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = showCustomYouTubeInput) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurface)
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = youtubeChannelName,
                            onValueChange = { youtubeChannelName = it },
                            label = { Text("Channel / Studio Name") },
                            placeholder = { Text("e.g. David Koda Music", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = YouTubeRed) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YouTubeRed,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianCard,
                                unfocusedContainerColor = ObsidianCard
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = youtubeApiKey,
                            onValueChange = { youtubeApiKey = it },
                            label = { Text("Google Cloud YouTube Data API Key") },
                            placeholder = { Text("AIzaSy...", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = YouTubeRed) },
                            trailingIcon = {
                                IconButton(onClick = { showYouTubeKey = !showYouTubeKey }) {
                                    Icon(
                                        imageVector = if (showYouTubeKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle key",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showYouTubeKey) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YouTubeRed,
                                unfocusedBorderColor = ObsidianStroke,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = ObsidianCard,
                                unfocusedContainerColor = ObsidianCard
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onLinkYouTube(youtubeApiKey, youtubeChannelName)
                                focusManager.clearFocus()
                            },
                            enabled = youtubeApiKey.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save & Authorize YouTube", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // ================= SYNC HUB TAB =================
        if (activeTab == ServiceAuthTab.SYNC_HUB) {
            // Live Sync Action Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(SpotifyGreen.copy(alpha = 0.5f), CrossPurple.copy(alpha = 0.5f), YouTubeRed.copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CrossPurple.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = CrossPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Cross-Platform Synchronizer",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Last synced: ${syncState.lastSyncFormatted}",
                                        color = TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            if (syncState.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CrossPurple,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (syncState.isSyncing) {
                            LinearProgressIndicator(
                                progress = { syncState.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CrossPurple,
                                trackColor = ObsidianStroke
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncState.currentStepDescription,
                                color = BabyBlue,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = syncState.currentStepDescription,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onStartSync,
                            enabled = !syncState.isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("start_sync_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (syncState.isSyncing) "Synchronizing Library..." else "Synchronize All Playlists & Likes",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Sync Metrics 4-grid
            item {
                Text(
                    text = "Synchronized Library Metrics",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Tracks Synced", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${syncState.totalSyncedAcrossPlatforms}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "Spotify + YouTube", color = SpotifyGreen, fontSize = 10.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Liked Songs", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${syncState.likedSongsSyncedCount}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "Merged Liked Vault", color = Color(0xFFFF4B6E), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Audio Matches", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${syncState.matchedSongsCount}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "Cross-platform Fingerprints", color = BabyBlue, fontSize = 10.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Unified Mixes", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${syncState.playlistsSyncedCount}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "Hybrid Playlists", color = HiResGold, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Bottom CTA Button
        item {
            Button(
                onClick = onContinueToApp,
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(CrossPurple, ElectricViolet))),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("continue_to_app_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Enter UnifiedX Player",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = CrossPurple,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
