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
import com.example.ui.theme.AeroCyanGlow
import com.example.ui.theme.AeroGelButton
import com.example.ui.theme.AeroGlassCard
import com.example.ui.theme.AeroIceWhite
import com.example.ui.theme.AeroWallpaperBackground
import com.example.ui.theme.AeroWindowHeader
import com.example.ui.theme.HiResGold
import com.example.ui.theme.LiquidCyanShimmer
import com.example.ui.theme.LiquidElectricBlue
import com.example.ui.theme.SpotifyGreen
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
    onUpdateCrossfadeSettings: (Boolean, Int) -> Unit = { _, _ -> },
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

    AeroWallpaperBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                AeroWindowHeader(
                    title = "Settings & Integrations",
                    subtitle = "Audio Quality, Accounts & Offline Storage"
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AeroGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = LiquidCyanShimmer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Room Database Offline Storage Active",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "User preferences, API keys & playlist cache persist locally",
                                    color = AeroIceWhite.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AeroGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenLinkAccountsClick)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Link Accounts",
                                tint = LiquidCyanShimmer,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Guided Service Account Setup",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Connect Spotify & YouTube with step-by-step guidance",
                                    color = AeroIceWhite.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            }
                            AeroGelButton(
                                onClick = onOpenLinkAccountsClick
                            ) {
                                Text("Open", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Section: Audio Quality
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Headphones, contentDescription = null, tint = HiResGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Streaming Quality",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AudioQuality.values().forEach { quality ->
                        val isSelected = playerState.streamingQuality == quality
                        AeroGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("quality_option_${quality.name}"),
                            onClick = { onSetAudioQuality(quality) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = quality.title,
                                            color = Color.White,
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
                                                .background(HiResGold.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = quality.description,
                                        color = AeroIceWhite.copy(alpha = 0.75f),
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = LiquidCyanShimmer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: Premium Offline Mode Toggle
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AeroGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("offline_mode_toggle_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                tint = LiquidCyanShimmer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Offline Listening Mode",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Play only downloaded high-fidelity tracks",
                                    color = AeroIceWhite.copy(alpha = 0.75f),
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Switch(
                            checked = playerState.isOfflineModeOnly,
                            onCheckedChange = onSetOfflineModeOnly,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LiquidCyanShimmer,
                                uncheckedThumbColor = AeroIceWhite.copy(alpha = 0.6f),
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("offline_mode_switch")
                        )
                    }
                }
            }

            // Section: Equalizer Presets
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Equalizer, contentDescription = null, tint = LiquidCyanShimmer, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hardware Audio Equalizer",
                            color = Color.White,
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
                            AeroGlassCard(
                                modifier = Modifier.weight(1f),
                                onClick = { onSetEqPreset(preset) },
                                contentPadding = 10.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset.label,
                                        color = if (isSelected) LiquidCyanShimmer else AeroIceWhite.copy(alpha = 0.75f),
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: Cross-fade Transitions
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = com.example.ui.theme.AppPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cross-fade Transitions",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AeroGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crossfade_settings_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Auto Cross-fade",
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Smooth track transitions without silence",
                                        color = com.example.ui.theme.TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }

                                Switch(
                                    checked = playerState.autoCrossfade,
                                    onCheckedChange = { enabled ->
                                        onUpdateCrossfadeSettings(enabled, playerState.crossfadeDurationSeconds)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = com.example.ui.theme.AppPrimary,
                                        uncheckedThumbColor = com.example.ui.theme.TextSecondary,
                                        uncheckedTrackColor = Color(0x33FFFFFF)
                                    ),
                                    modifier = Modifier.testTag("auto_crossfade_switch")
                                )
                            }

                            if (playerState.autoCrossfade) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Duration: ${playerState.crossfadeDurationSeconds} seconds",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(2, 4, 6, 8, 10).forEach { sec ->
                                        val isSelected = playerState.crossfadeDurationSeconds == sec
                                        AeroGlassCard(
                                            modifier = Modifier.weight(1f),
                                            onClick = { onUpdateCrossfadeSettings(true, sec) },
                                            contentPadding = 8.dp
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${sec}s",
                                                    color = if (isSelected) com.example.ui.theme.AppPrimary else com.example.ui.theme.TextSecondary,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section: Waveform Visualizer Studio
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = LiquidCyanShimmer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Waveform Visualizer Studio",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = visualizerSettings.isEnabled,
                            onCheckedChange = { onUpdateVisualizerSettings(visualizerSettings.copy(isEnabled = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LiquidCyanShimmer,
                                uncheckedThumbColor = AeroIceWhite.copy(alpha = 0.6f),
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("settings_visualizer_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AeroGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            // Live visualizer preview box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF061833))
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
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${visualizerSettings.barCount} Bands • ${visualizerSettings.palette.displayName}",
                                        color = AeroIceWhite.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }

                                AeroGelButton(
                                    onClick = onOpenVisualizerStudio,
                                    modifier = Modifier.testTag("settings_customize_visualizer_button")
                                ) {
                                    Text(
                                        text = "Customize",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: Connected Platform APIs
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = LiquidCyanShimmer, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connected Streaming Accounts & API Keys",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Spotify Card
                    AeroGlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column {
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
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (syncState.spotifyAccount.isConnected) "Connected 🟢" else "Disconnected",
                                    color = if (syncState.spotifyAccount.isConnected) SpotifyGreen else AeroIceWhite.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Account: ${syncState.spotifyAccount.accountUsername} • ${syncState.spotifyAccount.syncedItemsCount} synced items",
                                color = AeroIceWhite.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (showSpotifyKeyDialog) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = spotifyUserInput,
                                    onValueChange = { spotifyUserInput = it },
                                    label = { Text("Spotify Username", color = AeroIceWhite) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SpotifyGreen,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = spotifyTokenInput,
                                    onValueChange = { spotifyTokenInput = it },
                                    label = { Text("OAuth Bearer Token / API Client", color = AeroIceWhite) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SpotifyGreen,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
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
                    AeroGlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column {
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
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (syncState.youtubeAccount.isConnected) "Connected 🔴" else "Disconnected",
                                    color = if (syncState.youtubeAccount.isConnected) YouTubeRed else AeroIceWhite.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Channel: ${syncState.youtubeAccount.accountUsername} • ${syncState.youtubeAccount.syncedItemsCount} synced items",
                                color = AeroIceWhite.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (showYouTubeKeyDialog) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = youtubeUserInput,
                                    onValueChange = { youtubeUserInput = it },
                                    label = { Text("YouTube Channel / User", color = AeroIceWhite) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YouTubeRed,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = youtubeKeyInput,
                                    onValueChange = { youtubeKeyInput = it },
                                    label = { Text("YouTube Data API Key", color = AeroIceWhite) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YouTubeRed,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
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
}
