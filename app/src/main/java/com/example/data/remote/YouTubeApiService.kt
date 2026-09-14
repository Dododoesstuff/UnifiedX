package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class YouTubeSearchResponse(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "etag") val etag: String? = null,
    @Json(name = "nextPageToken") val nextPageToken: String? = null,
    @Json(name = "items") val items: List<YouTubeSearchResultItem> = emptyList()
)

data class YouTubeSearchResultItem(
    @Json(name = "id") val id: YouTubeIdInfo,
    @Json(name = "snippet") val snippet: YouTubeSnippetInfo
)

data class YouTubeIdInfo(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "videoId") val videoId: String? = null
)

data class YouTubeSnippetInfo(
    @Json(name = "publishedAt") val publishedAt: String? = null,
    @Json(name = "channelId") val channelId: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnailsInfo? = null,
    @Json(name = "channelTitle") val channelTitle: String
)

data class YouTubeThumbnailsInfo(
    @Json(name = "default") val default: YouTubeThumbnailItem? = null,
    @Json(name = "medium") val medium: YouTubeThumbnailItem? = null,
    @Json(name = "high") val high: YouTubeThumbnailItem? = null
)

data class YouTubeThumbnailItem(
    @Json(name = "url") val url: String,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

data class YouTubeChannelResponse(
    @Json(name = "items") val items: List<YouTubeChannelItem> = emptyList()
)

data class YouTubeChannelItem(
    @Json(name = "id") val id: String,
    @Json(name = "snippet") val snippet: YouTubeChannelSnippet
)

data class YouTubeChannelSnippet(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnailsInfo? = null
)

data class YouTubePlaylistResponse(
    @Json(name = "items") val items: List<YouTubePlaylistItem> = emptyList()
)

data class YouTubePlaylistItem(
    @Json(name = "id") val id: String,
    @Json(name = "snippet") val snippet: YouTubeSnippetInfo
)

data class YouTubePlaylistItemListResponse(
    @Json(name = "items") val items: List<YouTubePlaylistItemDetail> = emptyList()
)

data class YouTubePlaylistItemDetail(
    @Json(name = "id") val id: String,
    @Json(name = "snippet") val snippet: YouTubePlaylistItemSnippet
)

data class YouTubePlaylistItemSnippet(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "resourceId") val resourceId: YouTubeResourceId? = null,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnailsInfo? = null
)

data class YouTubeResourceId(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "videoId") val videoId: String? = null
)

data class YouTubeVideoListResponse(
    @Json(name = "items") val items: List<YouTubeVideoItem> = emptyList()
)

data class YouTubeVideoItem(
    @Json(name = "id") val id: String,
    @Json(name = "snippet") val snippet: YouTubeSnippetInfo
)

data class YouTubeCreatePlaylistRequest(
    @Json(name = "snippet") val snippet: YouTubeCreatePlaylistSnippet
)

data class YouTubeCreatePlaylistSnippet(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String = "Transferred via Cross-Platform Sync"
)

data class YouTubeInsertPlaylistItemRequest(
    @Json(name = "snippet") val snippet: YouTubeInsertPlaylistItemSnippet
)

data class YouTubeInsertPlaylistItemSnippet(
    @Json(name = "playlistId") val playlistId: String,
    @Json(name = "resourceId") val resourceId: YouTubeResourceId
)

interface YouTubeApiService {
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("videoCategoryId") videoCategoryId: String = "10", // 10 is Music
        @Query("maxResults") maxResults: Int = 15,
        @Query("key") apiKey: String
    ): Response<YouTubeSearchResponse>

    @GET("youtube/v3/channels")
    suspend fun getMyChannel(
        @retrofit2.http.Header("Authorization") authHeader: String? = null,
        @Query("part") part: String = "snippet",
        @Query("mine") mine: Boolean = true,
        @Query("key") apiKey: String? = null
    ): Response<YouTubeChannelResponse>

    @GET("youtube/v3/playlists")
    suspend fun getMyPlaylists(
        @retrofit2.http.Header("Authorization") authHeader: String? = null,
        @Query("part") part: String = "snippet",
        @Query("mine") mine: Boolean = true,
        @Query("maxResults") maxResults: Int = 50,
        @Query("key") apiKey: String? = null
    ): Response<YouTubePlaylistResponse>

    @GET("youtube/v3/playlistItems")
    suspend fun getPlaylistItems(
        @retrofit2.http.Header("Authorization") authHeader: String? = null,
        @Query("part") part: String = "snippet",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("key") apiKey: String? = null
    ): Response<YouTubePlaylistItemListResponse>

    @GET("youtube/v3/videos")
    suspend fun getLikedVideos(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Query("part") part: String = "snippet",
        @Query("myRating") myRating: String = "like",
        @Query("maxResults") maxResults: Int = 50
    ): Response<YouTubeVideoListResponse>

    @retrofit2.http.POST("youtube/v3/videos/rate")
    suspend fun rateVideo(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Query("id") videoId: String,
        @Query("rating") rating: String // "like" or "none"
    ): Response<Unit>

    @retrofit2.http.POST("youtube/v3/playlists")
    suspend fun createPlaylist(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Query("part") part: String = "snippet",
        @retrofit2.http.Body request: YouTubeCreatePlaylistRequest
    ): Response<YouTubePlaylistItem>

    @retrofit2.http.POST("youtube/v3/playlistItems")
    suspend fun insertPlaylistItem(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Query("part") part: String = "snippet",
        @retrofit2.http.Body request: YouTubeInsertPlaylistItemRequest
    ): Response<Unit>

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/"

        fun create(): YouTubeApiService {
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
                .create(YouTubeApiService::class.java)
        }
    }
}
