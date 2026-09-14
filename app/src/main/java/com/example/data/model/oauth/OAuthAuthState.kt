package com.example.data.model.oauth

/**
 * State representation for platform-specific OAuth2 authorization lifecycle.
 */
sealed interface OAuthAuthState {
    /**
     * User has not authorized this platform or has logged out.
     */
    data object Unauthenticated : OAuthAuthState

    /**
     * OAuth authorization flow has been initiated with the specified authorization URL.
     */
    data class Authenticating(
        val authorizationUrl: String,
        val stateNonce: String? = null
    ) : OAuthAuthState

    /**
     * Token is being refreshed in the background.
     */
    data class Refreshing(
        val previousToken: OAuthToken
    ) : OAuthAuthState

    /**
     * Platform has valid OAuth tokens and active authorization.
     */
    data class Authenticated(
        val token: OAuthToken,
        val profile: OAuthUserProfile? = null
    ) : OAuthAuthState

    /**
     * An error occurred during authorization or token refresh.
     */
    data class Error(
        val message: String,
        val isRecoverable: Boolean = true,
        val errorCode: String? = null
    ) : OAuthAuthState
}
