package com.example.data.model.oauth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Normalized user profile fetched from Spotify or Google/YouTube profile endpoints.
 */
@JsonClass(generateAdapter = true)
data class OAuthUserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "platform") val platform: OAuthPlatform,
    @Json(name = "followers_count") val followersCount: Int? = null,
    @Json(name = "product_or_tier") val productTier: String? = null
)
