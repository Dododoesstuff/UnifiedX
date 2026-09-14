package com.example.data.model.oauth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Encapsulates an OAuth2 token set with access token, optional refresh token,
 * scopes, expiration metadata, and validity helper functions.
 */
@JsonClass(generateAdapter = true)
data class OAuthToken(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String = "Bearer",
    @Json(name = "expires_in") val expiresInSeconds: Long = 3600,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "scope") val scope: String? = null,
    @Json(name = "id_token") val idToken: String? = null,
    @Json(name = "issued_at_millis") val issuedAtMillis: Long = System.currentTimeMillis()
) {
    /**
     * Approximate timestamp in epoch milliseconds when this access token expires.
     */
    val expiresAtMillis: Long
        get() = issuedAtMillis + (expiresInSeconds * 1000L)

    /**
     * Checks if the token is expired, with an optional safety buffer in seconds (default 60s).
     */
    fun isExpired(bufferSeconds: Long = 60): Boolean {
        val now = System.currentTimeMillis()
        val bufferMillis = bufferSeconds * 1000L
        return now >= (expiresAtMillis - bufferMillis)
    }

    /**
     * Remaining validity in seconds. Returns 0 if expired.
     */
    fun remainingSeconds(): Long {
        val remaining = (expiresAtMillis - System.currentTimeMillis()) / 1000L
        return if (remaining > 0) remaining else 0L
    }

    /**
     * Returns a standard Authorization header value: "Bearer <token>"
     */
    fun toAuthorizationHeader(): String {
        return if (accessToken.startsWith("Bearer ", ignoreCase = true)) {
            accessToken
        } else {
            "Bearer $accessToken"
        }
    }

    /**
     * Whether a refresh token is present to renew this access token automatically.
     */
    val canRefresh: Boolean
        get() = !refreshToken.isNullOrBlank()
}
