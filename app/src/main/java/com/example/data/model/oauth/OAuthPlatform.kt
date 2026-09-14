package com.example.data.model.oauth

import com.example.data.model.PlatformSource

enum class OAuthPlatform(
    val platformSource: PlatformSource,
    val displayName: String,
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val defaultScopes: List<String>,
    val defaultClientId: String,
    val defaultRedirectUri: String = "https://spotify-youtube-sync.app/callback"
) {
    SPOTIFY(
        platformSource = PlatformSource.SPOTIFY,
        displayName = "Spotify",
        authorizationEndpoint = "https://accounts.spotify.com/authorize",
        tokenEndpoint = "https://accounts.spotify.com/api/token",
        defaultScopes = listOf(
            "user-read-private",
            "user-read-email",
            "playlist-read-private",
            "playlist-read-collaborative",
            "playlist-modify-public",
            "playlist-modify-private",
            "user-library-read",
            "user-library-modify",
            "user-read-playback-state",
            "user-modify-playback-state"
        ),
        defaultClientId = "spotify_developer_client_id"
    ),

    YOUTUBE(
        platformSource = PlatformSource.YOUTUBE,
        displayName = "YouTube",
        authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth",
        tokenEndpoint = "https://oauth2.googleapis.com/token",
        defaultScopes = listOf(
            "https://www.googleapis.com/auth/youtube",
            "https://www.googleapis.com/auth/youtube.readonly",
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
        ),
        defaultClientId = "google_cloud_oauth_client_id"
    )
}
