package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Models for Spotify Web API
data class SpotifyUserResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "product") val product: String? = null
)

data class SpotifyTrackItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "duration_ms") val durationMs: Long = 180000L,
    @Json(name = "preview_url") val previewUrl: String? = null,
    @Json(name = "artists") val artists: List<SpotifyArtistItem> = emptyList(),
    @Json(name = "album") val album: SpotifyAlbumItem? = null
)

data class SpotifyArtistItem(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String
)

data class SpotifyAlbumItem(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "images") val images: List<SpotifyImageItem> = emptyList()
)

data class SpotifyImageItem(
    @Json(name = "url") val url: String,
    @Json(name = "height") val height: Int? = null,
    @Json(name = "width") val width: Int? = null
)

data class SpotifySavedTrackObject(
    @Json(name = "added_at") val addedAt: String? = null,
    @Json(name = "track") val track: SpotifyTrackItem
)

data class SpotifySavedTracksResponse(
    @Json(name = "total") val total: Int = 0,
    @Json(name = "items") val items: List<SpotifySavedTrackObject> = emptyList()
)

data class SpotifyPlaylistItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "images") val images: List<SpotifyImageItem> = emptyList(),
    @Json(name = "tracks") val tracks: SpotifyPlaylistTracksInfo? = null
)

data class SpotifyPlaylistTracksInfo(
    @Json(name = "total") val total: Int = 0
)

data class SpotifyPlaylistsResponse(
    @Json(name = "items") val items: List<SpotifyPlaylistItem> = emptyList()
)

data class SpotifyPlaylistTrackObject(
    @Json(name = "track") val track: SpotifyTrackItem
)

data class SpotifyPlaylistTracksResponse(
    @Json(name = "items") val items: List<SpotifyPlaylistTrackObject> = emptyList()
)

data class SpotifyCreatePlaylistRequest(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String = "Transferred via Cross-Platform Sync",
    @Json(name = "public") val isPublic: Boolean = false
)

data class SpotifyAddTracksRequest(
    @Json(name = "uris") val uris: List<String>
)

data class SpotifySearchTrackWrapper(
    @Json(name = "items") val items: List<SpotifyTrackItem> = emptyList()
)

data class SpotifySearchResponse(
    @Json(name = "tracks") val tracks: SpotifySearchTrackWrapper? = null
)

interface SpotifyApiService {
    @GET("v1/me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") authHeader: String
    ): Response<SpotifyUserResponse>

    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 50
    ): Response<SpotifySavedTracksResponse>

    @retrofit2.http.PUT("v1/me/tracks")
    suspend fun saveTrackForUser(
        @Header("Authorization") authHeader: String,
        @Query("ids") ids: String
    ): Response<Unit>

    @retrofit2.http.DELETE("v1/me/tracks")
    suspend fun removeTrackForUser(
        @Header("Authorization") authHeader: String,
        @Query("ids") ids: String
    ): Response<Unit>

    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 50
    ): Response<SpotifyPlaylistsResponse>

    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Header("Authorization") authHeader: String,
        @retrofit2.http.Path("playlist_id") playlistId: String
    ): Response<SpotifyPlaylistTracksResponse>

    @retrofit2.http.POST("v1/users/{user_id}/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String,
        @retrofit2.http.Path("user_id") userId: String,
        @retrofit2.http.Body request: SpotifyCreatePlaylistRequest
    ): Response<SpotifyPlaylistItem>

    @retrofit2.http.POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Header("Authorization") authHeader: String,
        @retrofit2.http.Path("playlist_id") playlistId: String,
        @retrofit2.http.Body request: SpotifyAddTracksRequest
    ): Response<Unit>

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    ): Response<SpotifySearchResponse>

    companion object {
        private const val BASE_URL = "https://api.spotify.com/"

        fun create(): SpotifyApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SpotifyApiService::class.java)
        }
    }
}
