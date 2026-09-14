package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource
import com.example.player.EqPreset
import com.example.player.PlayerUiState
import com.example.sync.SyncState
import com.example.ui.theme.CrossPurple
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.ObsidianStroke
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeRed

@Composable
fun SettingsScreen(
    playerState: PlayerUiState,
    syncState: SyncState,
    userPreferences: UserPreferencesEntity? = null,
    visualizerSettings: com.example.visualizer.VisualizerSettings = com.example.visualizer.VisualizerSettings(),
    visualizerFrequencies: FloatArray = FloatArray(0),
    onUpdateVisualizerSettings: (com.example.visualizer.VisualizerSettings) -> Unit = {},
    onOpenVisualizerStudio: () -> Unit = {},
    onSetAudioQuality: (AudioQuality) -> Unit,
    onSetOfflineModeOnly: (Boolean) -> Unit,
    onSetEqPreset: (EqPreset) -> Unit,
    onUpdateAccountCredential: (PlatformSource, String, String) -> Unit,
    onOpenLinkAccountsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSpotifyKeyDialog by remember { mutableStateOf(false) }
    var spotifyTokenInput by remember { mutableStateOf(syncState.spotifyAccount.tokenOrApiKey) }
    var spotifyUserInput by remember { mutableStateOf(syncState.spotifyAccount.accountUsername) }

    var showYouTubeKeyDialog by remember { mutableStateOf(false) }
    var youtubeKeyInput by remember { mutableStateOf(syncState.youtubeAccount.tokenOrApiKey) }
    var youtubeUserInput by remember { mutableStateOf(syncState.youtubeAccount.accountUsername) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDeep)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Streaming & Library Settings",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Audio fidelity, cross-platform APIs, and offline playback",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Room Database Offline Storage Active",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "User preferences, API keys & playlist cache persist locally without network connection",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenLinkAccountsClick),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CrossPurple.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Link Accounts",
                            tint = CrossPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Guided Service Account Setup",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Connect Spotify & YouTube with step-by-step guidance and test tokens",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = onOpenLinkAccountsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Audio Quality
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Headphones, contentDescription = null, tint = HiResGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "High-Quality Audio Streaming Options",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                AudioQuality.values().forEach { quality ->
                    val isSelected = playerState.streamingQuality == quality
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSetAudioQuality(quality) }
                            .testTag("quality_option_${quality.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CrossPurple.copy(alpha = 0.15f) else ObsidianCard
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = quality.title,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = quality.badge,
                                        color = HiResGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(HiResGold.copy(alpha = 0.18f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = quality.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = CrossPurple
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Premium Offline Mode Toggle
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("offline_mode_toggle_card"),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.OfflinePin,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Offline Listening Mode",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Play only downloaded high-fidelity tracks (Airplane & Data Saver)",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = playerState.isOfflineModeOnly,
                        onCheckedChange = onSetOfflineModeOnly,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SpotifyGreen,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = ObsidianStroke
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }
            }
        }

        // Section: Equalizer Presets
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Equalizer, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hardware Audio Equalizer Profile",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EqPreset.values().forEach { preset ->
                        val isSelected = playerState.activeEqPreset == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CrossPurple else ObsidianCard)
                                .clickable { onSetEqPreset(preset) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Section: Waveform Visualizer Studio
        item {
            Spacer(modifier = Modifier.height(22.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CrossPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Waveform Visualizer Studio",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = visualizerSettings.isEnabled,
                        onCheckedChange = { onUpdateVisualizerSettings(visualizerSettings.copy(isEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CrossPurple,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = ObsidianStroke
                        ),
                        modifier = Modifier.testTag("settings_visualizer_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Live visualizer preview box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ObsidianDeep)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            com.example.visualizer.WaveformVisualizer(
                                frequencies = visualizerFrequencies,
                                settings = visualizerSettings,
                                isPlaying = playerState.isPlaying,
                                platformSource = playerState.currentTrack?.platformSource ?: PlatformSource.SPOTIFY,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Active Style: ${visualizerSettings.style.displayName}",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${visualizerSettings.barCount} Bands • ${visualizerSettings.palette.displayName}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = onOpenVisualizerStudio,
                                colors = ButtonDefaults.buttonColors(containerColor = CrossPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("settings_customize_visualizer_button")
                            ) {
                                Text(
                                    text = "Customize",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Connected Platform APIs (Spotify & YouTube)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = CrossPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connected Streaming Accounts & API Keys",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Spotify Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Spotify Web API",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (syncState.spotifyAccount.isConnected) "Connected 🟢" else "Disconnected",
                                color = if (syncState.spotifyAccount.isConnected) SpotifyGreen else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Account: ${syncState.spotifyAccount.accountUsername} • ${syncState.spotifyAccount.syncedItemsCount} synced items",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (showSpotifyKeyDialog) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = spotifyUserInput,
                                onValueChange = { spotifyUserInput = it },
                                label = { Text("Spotify Username") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SpotifyGreen,
                                    unfocusedBorderColor = ObsidianStroke,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = spotifyTokenInput,
                                onValueChange = { spotifyTokenInput = it },
                                label = { Text("OAuth Bearer Token / API Client") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SpotifyGreen,
                                    unfocusedBorderColor = ObsidianStroke,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onUpdateAccountCredential(PlatformSource.SPOTIFY, spotifyTokenInput, spotifyUserInput)
                                    showSpotifyKeyDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
                            ) {
                                Text("Save Spotify Credentials", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Configure Spotify Token / Key",
                                color = SpotifyGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { showSpotifyKeyDialog = true }
                                    .testTag("configure_spotify_credentials")
                            )
                        }
                    }
                }

                // YouTube Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(YouTubeRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "YouTube Data API v3",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (syncState.youtubeAccount.isConnected) "Connected 🔴" else "Disconnected",
                                color = if (syncState.youtubeAccount.isConnected) YouTubeRed else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Channel: ${syncState.youtubeAccount.accountUsername} • ${syncState.youtubeAccount.syncedItemsCount} synced items",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (showYouTubeKeyDialog) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = youtubeUserInput,
                                onValueChange = { youtubeUserInput = it },
                                label = { Text("YouTube Channel / User") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = YouTubeRed,
                                    unfocusedBorderColor = ObsidianStroke,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = youtubeKeyInput,
                                onValueChange = { youtubeKeyInput = it },
                                label = { Text("YouTube Data API Key") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = YouTubeRed,
                                    unfocusedBorderColor = ObsidianStroke,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onUpdateAccountCredential(PlatformSource.YOUTUBE, youtubeKeyInput, youtubeUserInput)
                                    showYouTubeKeyDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                            ) {
                                Text("Save YouTube Key", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Configure YouTube API Key",
                                color = YouTubeRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { showYouTubeKeyDialog = true }
                                    .testTag("configure_youtube_credentials")
                            )
                        }
                    }
                }
            }
        }
    }
}
