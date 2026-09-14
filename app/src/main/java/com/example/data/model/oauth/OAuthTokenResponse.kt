package com.example.data.model.oauth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Raw JSON response received from OAuth2 token endpoints (Spotify, Google / YouTube).
 */
@JsonClass(generateAdapter = true)
data class OAuthTokenResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String = "Bearer",
    @Json(name = "expires_in") val expiresIn: Long = 3600,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "scope") val scope: String? = null,
    @Json(name = "id_token") val idToken: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "error_description") val errorDescription: String? = null
) {
    fun toOAuthToken(fallbackRefreshToken: String? = null): OAuthToken? {
        val token = accessToken?.trim()
        if (token.isNullOrBlank()) return null
        return OAuthToken(
            accessToken = token,
            tokenType = tokenType.ifBlank { "Bearer" },
            expiresInSeconds = expiresIn,
            refreshToken = refreshToken?.trim()?.ifBlank { null } ?: fallbackRefreshToken,
            scope = scope,
            idToken = idToken,
            issuedAtMillis = System.currentTimeMillis()
        )
    }
}
