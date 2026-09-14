package com.example.data.repository.oauth

import com.example.data.model.oauth.OAuthAuthState
import com.example.data.model.oauth.OAuthPlatform
import com.example.data.model.oauth.OAuthToken
import com.example.data.model.oauth.OAuthUserProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface governing OAuth2 authentication lifecycles for
 * Spotify and YouTube APIs, including token exchange, secure persistence,
 * token refresh logic, and user profile management.
 */
interface OAuthRepository {

    /**
     * Real-time observable authentication state for Spotify.
     */
    val spotifyAuthState: StateFlow<OAuthAuthState>

    /**
     * Real-time observable authentication state for YouTube / Google.
     */
    val youtubeAuthState: StateFlow<OAuthAuthState>

    /**
     * Gets state flow for the requested platform.
     */
    fun getAuthState(platform: OAuthPlatform): StateFlow<OAuthAuthState>

    /**
     * Constructs the web browser authorization URL for initiating the OAuth2 flow.
     */
    fun buildAuthorizationUrl(
        platform: OAuthPlatform,
        clientId: String? = null,
        redirectUri: String? = null,
        stateNonce: String? = null
    ): String

    /**
     * Exchanges an authorization code received from redirect callback for access & refresh tokens.
     */
    suspend fun exchangeAuthorizationCode(
        platform: OAuthPlatform,
        code: String,
        redirectUri: String? = null,
        clientId: String? = null,
        clientSecret: String? = null
    ): Result<OAuthToken>

    /**
     * Refreshes the OAuth2 access token using the stored refresh token.
     */
    suspend fun refreshAccessToken(
        platform: OAuthPlatform,
        force: Boolean = false
    ): Result<OAuthToken>

    /**
     * Returns a valid access token. If the existing token is expired or close to expiry,
     * it automatically triggers the token refresh logic.
     */
    suspend fun getValidAccessToken(platform: OAuthPlatform): Result<String>

    /**
     * Saves a direct or manually provided token into secure storage and marks the platform authenticated.
     */
    suspend fun saveDirectAccessToken(
        platform: OAuthPlatform,
        tokenString: String,
        username: String? = null,
        expiresInSeconds: Long = 3600L,
        refreshToken: String? = null
    )

    /**
     * Fetches or re-fetches the user profile from the platform API using the valid access token.
     */
    suspend fun fetchUserProfile(platform: OAuthPlatform): Result<OAuthUserProfile>

    /**
     * Signs out the user from the given platform, clears EncryptedSharedPreferences, and resets state.
     */
    suspend fun signOut(platform: OAuthPlatform)

    /**
     * Authenticates user using account email & password. Provisions a secure session
     * encrypted in hardware-backed EncryptedSharedPreferences without exposing tokens to the user.
     */
    suspend fun loginWithAccountEmail(
        platform: OAuthPlatform,
        email: String,
        password: String,
        displayName: String? = null
    ): Result<OAuthUserProfile>

    /**
     * Returns list of accounts previously saved on this device.
     */
    fun getSavedAccounts(platform: OAuthPlatform): List<com.example.data.local.security.SavedAccountRecord>

    /**
     * Removes an account from the saved list.
     */
    fun removeSavedAccount(platform: OAuthPlatform, email: String)

    /**
     * Switches the active session to one of the saved accounts.
     */
    suspend fun switchAccount(
        platform: OAuthPlatform,
        email: String,
        displayName: String? = null
    ): Result<OAuthUserProfile>
}
