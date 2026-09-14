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
    @Json(name = "description") val description: String? = null
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
        @Query("part") part: String = "snippet",
        @Query("mine") mine: Boolean = true,
        @Query("key") apiKey: String
    ): Response<YouTubeChannelResponse>

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
