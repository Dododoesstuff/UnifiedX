package com.example.data.model.oauth

/**
 * Client registration metadata for Spotify and YouTube OAuth2 providers.
 */
data class OAuthClientConfig(
    val platform: OAuthPlatform,
    val clientId: String,
    val clientSecret: String? = null,
    val redirectUri: String = platform.defaultRedirectUri,
    val scopes: List<String> = platform.defaultScopes
)
