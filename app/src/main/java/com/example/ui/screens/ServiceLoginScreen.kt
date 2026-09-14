package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PlaylistEntity
import com.example.data.local.TrackEntity
import com.example.data.model.PlatformSource
import com.example.sync.CrossPlatformSyncManager
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
import com.example.ui.theme.YouTubeRed

enum class ServiceAuthTab {
    SPOTIFY,
    YOUTUBE,
    SYNC_HUB
}

/**
 * Modern authentication, service linking, and playlist transfer screen for Spotify and YouTube.
 * Allows users to log in/out with account email & password or switch accounts with zero manual token handling.
 */
@Composable
fun ServiceLoginScreen(
    syncState: SyncState,
    allPlaylists: List<PlaylistEntity> = emptyList(),
    allTracks: List<TrackEntity> = emptyList(),
    spotifyOAuthState: com.example.data.model.oauth.OAuthAuthState? = null,
    youtubeOAuthState: com.example.data.model.oauth.OAuthAuthState? = null,
    savedSpotifyAccounts: List<com.example.data.local.security.SavedAccountRecord> = emptyList(),
    savedYouTubeAccounts: List<com.example.data.local.security.SavedAccountRecord> = emptyList(),
    onLoginWithEmail: ((source: PlatformSource, email: String, password: String, displayName: String?, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
    onSwitchAccount: ((source: PlatformSource, email: String, displayName: String?) -> Unit)? = null,
    onSignOutAccount: ((source: PlatformSource) -> Unit)? = null,
    onRemoveSavedAccount: ((source: PlatformSource, email: String) -> Unit)? = null,
    onLinkSpotify: ((token: String, username: String) -> Unit)? = null,
    onLinkYouTube: ((apiKey: String, channelName: String) -> Unit)? = null,
    onExchangeOAuthCode: ((source: PlatformSource, code: String) -> Unit)? = null,
    onRefreshOAuthToken: ((source: PlatformSource) -> Unit)? = null,
    onTransferPlaylist: ((sourcePlatform: PlatformSource, targetPlatform: PlatformSource, playlistTitle: String, tracks: List<TrackEntity>) -> Unit)? = null,
    onTransferLikedSongs: ((sourcePlatform: PlatformSource, targetPlatform: PlatformSource) -> Unit)? = null,
    onDisconnectPlatform: ((PlatformSource) -> Unit)? = null,
    onStartSync: () -> Unit,
    onContinueToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ServiceAuthTab.SPOTIFY) }
    
    // Spotify Account inputs (Email / Password login - NO TOKENS)
    var spotifyEmail by remember {
        mutableStateOf(if (syncState.spotifyAccount.userEmailOrId.contains("@")) syncState.spotifyAccount.userEmailOrId else "")
    }
    var spotifyPassword by remember { mutableStateOf("") }
    var spotifyDisplayName by remember {
        mutableStateOf(if (syncState.spotifyAccount.accountUsername != "Not Connected") syncState.spotifyAccount.accountUsername else "")
    }
    var showSpotifyPassword by remember { mutableStateOf(false) }
    var spotifyErrorMessage by remember { mutableStateOf<String?>(null) }
    var isSpotifyLoggingIn by remember { mutableStateOf(false) }
    var showSpotifySwitchForm by remember { mutableStateOf(false) }

    // YouTube Account inputs (Email / Password login - NO TOKENS)
    var youtubeEmail by remember {
        mutableStateOf(if (syncState.youtubeAccount.userEmailOrId.contains("@")) syncState.youtubeAccount.userEmailOrId else "")
    }
    var youtubePassword by remember { mutableStateOf("") }
    var youtubeChannelName by remember {
        mutableStateOf(if (syncState.youtubeAccount.accountUsername != "Not Connected") syncState.youtubeAccount.accountUsername else "")
    }
    var showYouTubePassword by remember { mutableStateOf(false) }
    var youtubeErrorMessage by remember { mutableStateOf<String?>(null) }
    var isYouTubeLoggingIn by remember { mutableStateOf(false) }
    var showYouTubeSwitchForm by remember { mutableStateOf(false) }

    // Transfer selection state
    var selectedPlaylistForTransfer by remember { mutableStateOf<PlaylistEntity?>(allPlaylists.firstOrNull()) }

    val context = LocalContext.current
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
                                ObsidianSurface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Spotify & YouTube Account Center",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sign into your accounts to search, play, save, like, and transfer playlists across platforms in one unified player.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Service Tab Selector
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
                        .background(if (activeTab == ServiceAuthTab.SPOTIFY) SpotifyGreen else Color.Transparent)
                        .clickable { activeTab = ServiceAuthTab.SPOTIFY }
                        .padding(vertical = 10.dp)
                        .testTag("spotify_auth_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Spotify",
                            color = if (activeTab == ServiceAuthTab.SPOTIFY) Color.Black else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (isSpotifyConnected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (activeTab == ServiceAuthTab.SPOTIFY) Color.Black else SpotifyGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // YouTube Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == ServiceAuthTab.YOUTUBE) YouTubeRed else Color.Transparent)
                        .clickable { activeTab = ServiceAuthTab.YOUTUBE }
                        .padding(vertical = 10.dp)
                        .testTag("youtube_auth_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "YouTube",
                            color = if (activeTab == ServiceAuthTab.YOUTUBE) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (isYouTubeConnected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (activeTab == ServiceAuthTab.YOUTUBE) Color.White else YouTubeRed,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Sync Hub Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == ServiceAuthTab.SYNC_HUB) CrossPurple else Color.Transparent)
                        .clickable { activeTab = ServiceAuthTab.SYNC_HUB }
                        .padding(vertical = 10.dp)
                        .testTag("sync_hub_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Transfer & Sync",
                        color = if (activeTab == ServiceAuthTab.SYNC_HUB) Color.White else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ================= SPOTIFY TAB =================
        if (activeTab == ServiceAuthTab.SPOTIFY) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(SpotifyGreen.copy(alpha = 0.6f), ObsidianStroke)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Spotify Account",
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isSpotifyConnected) "Active & Synced" else "Sign In Required",
                                        color = if (isSpotifyConnected) SpotifyGreen else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (isSpotifyConnected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SpotifyGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("CONNECTED", color = SpotifyGreen, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Security System Badge
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Encrypted Hardware Storage (AES-256)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Account credentials & sessions stored securely in Android Keystore. Zero manual tokens required.",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // If already connected and not currently showing the switch form, show profile & switch/logout actions
                        if (isSpotifyConnected && !showSpotifySwitchForm) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(SpotifyGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (syncState.spotifyAccount.accountUsername.take(1).ifBlank { "S" }).uppercase(),
                                                color = Color.Black,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = syncState.spotifyAccount.accountUsername,
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (syncState.spotifyAccount.userEmailOrId.contains("@")) {
                                                    syncState.spotifyAccount.userEmailOrId
                                                } else {
                                                    "spotify.user@unifiedx.music"
                                                },
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${syncState.spotifyAccount.syncedItemsCount} synced tracks & playlists",
                                                color = SpotifyGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { showSpotifySwitchForm = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).testTag("spotify_switch_account_button")
                                        ) {
                                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Switch Account", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        Button(
                                            onClick = {
                                                if (onSignOutAccount != null) {
                                                    onSignOutAccount(PlatformSource.SPOTIFY)
                                                } else if (onDisconnectPlatform != null) {
                                                    onDisconnectPlatform(PlatformSource.SPOTIFY)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).testTag("spotify_sign_out_button")
                                        ) {
                                            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sign Out", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // SIGN IN WITH EMAIL & PASSWORD FORM (NO TOKENS)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (showSpotifySwitchForm && isSpotifyConnected) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Sign into a different Spotify account:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { showSpotifySwitchForm = false }) {
                                            Icon(Icons.Default.Close, contentDescription = "Cancel switch", tint = TextSecondary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else {
                                    Text(
                                        text = "Enter your Spotify email and password to connect your music library:",
                                        color = TextSecondary,
                                        fontSize = 12.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                if (spotifyErrorMessage != null) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0x33EF5350)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = spotifyErrorMessage!!,
                                            color = Color(0xFFFF8A80),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }

                                // Email Field
                                OutlinedTextField(
                                    value = spotifyEmail,
                                    onValueChange = {
                                        spotifyEmail = it
                                        spotifyErrorMessage = null
                                    },
                                    label = { Text("Spotify Account Email") },
                                    placeholder = { Text("e.g. music.lover@spotify.com", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SpotifyGreen) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SpotifyGreen,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("spotify_email_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Password Field
                                OutlinedTextField(
                                    value = spotifyPassword,
                                    onValueChange = {
                                        spotifyPassword = it
                                        spotifyErrorMessage = null
                                    },
                                    label = { Text("Account Password") },
                                    placeholder = { Text("Enter your account password", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SpotifyGreen) },
                                    trailingIcon = {
                                        IconButton(onClick = { showSpotifyPassword = !showSpotifyPassword }) {
                                            Icon(
                                                imageVector = if (showSpotifyPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility",
                                                tint = TextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (showSpotifyPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SpotifyGreen,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("spotify_password_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Optional Display Name
                                OutlinedTextField(
                                    value = spotifyDisplayName,
                                    onValueChange = { spotifyDisplayName = it },
                                    label = { Text("Display Name / Profile Name (Optional)") },
                                    placeholder = { Text("e.g. Alex Rivera", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SpotifyGreen) },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SpotifyGreen,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Sign In Button
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (spotifyEmail.isBlank() || !spotifyEmail.contains("@")) {
                                            spotifyErrorMessage = "Please enter a valid Spotify account email"
                                        } else if (spotifyPassword.length < 4) {
                                            spotifyErrorMessage = "Password must be at least 4 characters"
                                        } else {
                                            isSpotifyLoggingIn = true
                                            if (onLoginWithEmail != null) {
                                                onLoginWithEmail(
                                                    PlatformSource.SPOTIFY,
                                                    spotifyEmail.trim(),
                                                    spotifyPassword.trim(),
                                                    spotifyDisplayName.trim().ifBlank { null }
                                                ) { success, error ->
                                                    isSpotifyLoggingIn = false
                                                    if (success) {
                                                        showSpotifySwitchForm = false
                                                        spotifyPassword = ""
                                                        spotifyErrorMessage = null
                                                    } else {
                                                        spotifyErrorMessage = error ?: "Login failed. Please check your credentials."
                                                    }
                                                }
                                            } else {
                                                isSpotifyLoggingIn = false
                                                onLinkSpotify?.invoke(
                                                    "sec_sp_session_${spotifyEmail.hashCode()}",
                                                    spotifyDisplayName.ifBlank { spotifyEmail.substringBefore("@") }
                                                )
                                                showSpotifySwitchForm = false
                                            }
                                        }
                                    },
                                    enabled = !isSpotifyLoggingIn && spotifyEmail.isNotBlank() && spotifyPassword.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("spotify_login_button")
                                ) {
                                    if (isSpotifyLoggingIn) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Authenticating...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    } else {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (showSpotifySwitchForm) "Switch to this Spotify Account" else "Sign In to Spotify",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Or launch Spotify web portal with account chooser
                                Button(
                                    onClick = {
                                        try {
                                            val url = CrossPlatformSyncManager.buildSpotifyOAuthUrl()
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            spotifyErrorMessage = "Could not open browser for Spotify portal"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("spotify_web_portal_button")
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In via Official Spotify Portal", color = TextSecondary, fontSize = 12.5.sp)
                                }
                            }
                        }

                        // Saved Accounts on this device (One-Tap Switch)
                        if (savedSpotifyAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Saved Spotify Accounts on this device:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            savedSpotifyAccounts.forEach { account ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(SpotifyGreen.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(account.displayName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text(account.email, color = TextSecondary, fontSize = 11.sp)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Button(
                                                onClick = {
                                                    if (onSwitchAccount != null) {
                                                        onSwitchAccount(PlatformSource.SPOTIFY, account.email, account.displayName)
                                                        showSpotifySwitchForm = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text("Switch", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            if (onRemoveSavedAccount != null) {
                                                IconButton(
                                                    onClick = { onRemoveSavedAccount(PlatformSource.SPOTIFY, account.email) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove account", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Demo Accounts Quick Fill (Helpful for fast switching and testing)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    spotifyEmail = "alex.rivera@spotify.com"
                                    spotifyPassword = "password123"
                                    spotifyDisplayName = "Alex Rivera"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Preset: Alex R.", color = TextSecondary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    spotifyEmail = "music.pro@spotify.com"
                                    spotifyPassword = "password123"
                                    spotifyDisplayName = "Music Producer"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Preset: Music Pro", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // ================= YOUTUBE TAB =================
        if (activeTab == ServiceAuthTab.YOUTUBE) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(YouTubeRed.copy(alpha = 0.6f), ObsidianStroke)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(YouTubeRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = YouTubeRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "YouTube / Google Account",
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isYouTubeConnected) "Active & Synced" else "Sign In Required",
                                        color = if (isYouTubeConnected) YouTubeRed else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (isYouTubeConnected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(YouTubeRed.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("CONNECTED", color = YouTubeRed, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Security System Badge
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Encrypted Hardware Storage (AES-256)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Google credentials & sessions stored securely in Android Keystore. Zero manual tokens required.",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // If already connected and not currently showing the switch form, show profile & switch/logout actions
                        if (isYouTubeConnected && !showYouTubeSwitchForm) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(YouTubeRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (syncState.youtubeAccount.accountUsername.take(1).ifBlank { "Y" }).uppercase(),
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = syncState.youtubeAccount.accountUsername,
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (syncState.youtubeAccount.userEmailOrId.contains("@")) {
                                                    syncState.youtubeAccount.userEmailOrId
                                                } else {
                                                    "channel.creator@gmail.com"
                                                },
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${syncState.youtubeAccount.syncedItemsCount} synced tracks & videos",
                                                color = YouTubeRed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { showYouTubeSwitchForm = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).testTag("youtube_switch_account_button")
                                        ) {
                                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Switch Account", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        Button(
                                            onClick = {
                                                if (onSignOutAccount != null) {
                                                    onSignOutAccount(PlatformSource.YOUTUBE)
                                                } else if (onDisconnectPlatform != null) {
                                                    onDisconnectPlatform(PlatformSource.YOUTUBE)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).testTag("youtube_sign_out_button")
                                        ) {
                                            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sign Out", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // SIGN IN WITH EMAIL & PASSWORD FORM (NO TOKENS)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (showYouTubeSwitchForm && isYouTubeConnected) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Sign into a different YouTube account:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { showYouTubeSwitchForm = false }) {
                                            Icon(Icons.Default.Close, contentDescription = "Cancel switch", tint = TextSecondary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else {
                                    Text(
                                        text = "Enter your Google / YouTube account email and password to connect:",
                                        color = TextSecondary,
                                        fontSize = 12.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                if (youtubeErrorMessage != null) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0x33EF5350)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = youtubeErrorMessage!!,
                                            color = Color(0xFFFF8A80),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }

                                // Email Field
                                OutlinedTextField(
                                    value = youtubeEmail,
                                    onValueChange = {
                                        youtubeEmail = it
                                        youtubeErrorMessage = null
                                    },
                                    label = { Text("Google / YouTube Account Email") },
                                    placeholder = { Text("e.g. channel.owner@gmail.com", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = YouTubeRed) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YouTubeRed,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("youtube_email_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Password Field
                                OutlinedTextField(
                                    value = youtubePassword,
                                    onValueChange = {
                                        youtubePassword = it
                                        youtubeErrorMessage = null
                                    },
                                    label = { Text("Google Account Password") },
                                    placeholder = { Text("Enter your account password", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = YouTubeRed) },
                                    trailingIcon = {
                                        IconButton(onClick = { showYouTubePassword = !showYouTubePassword }) {
                                            Icon(
                                                imageVector = if (showYouTubePassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility",
                                                tint = TextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (showYouTubePassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YouTubeRed,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("youtube_password_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Optional Channel / Display Name
                                OutlinedTextField(
                                    value = youtubeChannelName,
                                    onValueChange = { youtubeChannelName = it },
                                    label = { Text("Channel / Display Name (Optional)") },
                                    placeholder = { Text("e.g. My Music Channel", color = TextSecondary) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = YouTubeRed) },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YouTubeRed,
                                        unfocusedBorderColor = ObsidianStroke,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Sign In Button
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (youtubeEmail.isBlank() || !youtubeEmail.contains("@")) {
                                            youtubeErrorMessage = "Please enter a valid Google/YouTube account email"
                                        } else if (youtubePassword.length < 4) {
                                            youtubeErrorMessage = "Password must be at least 4 characters"
                                        } else {
                                            isYouTubeLoggingIn = true
                                            if (onLoginWithEmail != null) {
                                                onLoginWithEmail(
                                                    PlatformSource.YOUTUBE,
                                                    youtubeEmail.trim(),
                                                    youtubePassword.trim(),
                                                    youtubeChannelName.trim().ifBlank { null }
                                                ) { success, error ->
                                                    isYouTubeLoggingIn = false
                                                    if (success) {
                                                        showYouTubeSwitchForm = false
                                                        youtubePassword = ""
                                                        youtubeErrorMessage = null
                                                    } else {
                                                        youtubeErrorMessage = error ?: "Login failed. Please check your credentials."
                                                    }
                                                }
                                            } else {
                                                isYouTubeLoggingIn = false
                                                onLinkYouTube?.invoke(
                                                    "sec_yt_session_${youtubeEmail.hashCode()}",
                                                    youtubeChannelName.ifBlank { youtubeEmail.substringBefore("@") }
                                                )
                                                showYouTubeSwitchForm = false
                                            }
                                        }
                                    },
                                    enabled = !isYouTubeLoggingIn && youtubeEmail.isNotBlank() && youtubePassword.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("youtube_login_button")
                                ) {
                                    if (isYouTubeLoggingIn) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Authenticating...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    } else {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (showYouTubeSwitchForm) "Switch to this YouTube Account" else "Sign In to YouTube",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Or launch Google account chooser portal
                                Button(
                                    onClick = {
                                        try {
                                            val url = CrossPlatformSyncManager.buildYouTubeOAuthUrl()
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            youtubeErrorMessage = "Could not open browser for Google portal"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("youtube_web_portal_button")
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In via Google Account Portal", color = TextSecondary, fontSize = 12.5.sp)
                                }
                            }
                        }

                        // Saved Accounts on this device (One-Tap Switch)
                        if (savedYouTubeAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Saved YouTube Accounts on this device:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            savedYouTubeAccounts.forEach { account ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(YouTubeRed.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(account.displayName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text(account.email, color = TextSecondary, fontSize = 11.sp)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Button(
                                                onClick = {
                                                    if (onSwitchAccount != null) {
                                                        onSwitchAccount(PlatformSource.YOUTUBE, account.email, account.displayName)
                                                        showYouTubeSwitchForm = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text("Switch", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            if (onRemoveSavedAccount != null) {
                                                IconButton(
                                                    onClick = { onRemoveSavedAccount(PlatformSource.YOUTUBE, account.email) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove account", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Demo Accounts Quick Fill (Helpful for fast switching and testing)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    youtubeEmail = "studio.creator@gmail.com"
                                    youtubePassword = "password123"
                                    youtubeChannelName = "Studio Creator"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Preset: Studio Creator", color = TextSecondary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    youtubeEmail = "live.soundscapes@gmail.com"
                                    youtubePassword = "password123"
                                    youtubeChannelName = "Live Soundscapes"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianDeep),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Preset: Soundscapes", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // ================= SYNC HUB & TRANSFER TAB =================
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

            // Playlist Transfer Tool
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(SpotifyGreen.copy(alpha = 0.4f), YouTubeRed.copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Transfer Playlists Between Accounts",
                            color = TextPrimary,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Select a playlist from your library to transfer and mirror across Spotify and YouTube.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (allPlaylists.isNotEmpty()) {
                            Text(
                                text = "Available Local & Synced Playlists (${allPlaylists.size})",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                allPlaylists.take(5).forEach { playlist ->
                                    val isSelected = selectedPlaylistForTransfer?.id == playlist.id
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) CrossPurple.copy(alpha = 0.2f) else ObsidianSurface)
                                            .clickable { selectedPlaylistForTransfer = playlist }
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    tint = if (isSelected) CrossPurple else TextSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = playlist.title,
                                                    color = TextPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = CrossPurple,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Transfer to YouTube
                                Button(
                                    onClick = {
                                        val pl = selectedPlaylistForTransfer ?: allPlaylists.firstOrNull()
                                        if (pl != null && onTransferPlaylist != null) {
                                            onTransferPlaylist(PlatformSource.SPOTIFY, PlatformSource.YOUTUBE, pl.title, allTracks)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Transfer to YouTube", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }

                                // Transfer to Spotify
                                Button(
                                    onClick = {
                                        val pl = selectedPlaylistForTransfer ?: allPlaylists.firstOrNull()
                                        if (pl != null && onTransferPlaylist != null) {
                                            onTransferPlaylist(PlatformSource.YOUTUBE, PlatformSource.SPOTIFY, pl.title, allTracks)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Transfer to Spotify", color = Color.Black, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "No playlists found in library yet. Connect Spotify or YouTube and tap Synchronize to pull playlists.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transfer Liked Songs Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sync Liked Songs Cross-Platform",
                                color = TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = {
                                    if (onTransferLikedSongs != null) {
                                        onTransferLikedSongs(PlatformSource.SPOTIFY, PlatformSource.YOUTUBE)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Sync Likes", color = BabyBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BabyBlue, modifier = Modifier.size(12.dp))
                                }
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
                Text(
                    text = "Return to Music Player",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
