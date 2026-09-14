package com.example.data.remote.oauth

import com.example.data.model.oauth.OAuthTokenResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GoogleUserInfoResponse(
    @Json(name = "sub") val sub: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "given_name") val givenName: String? = null,
    @Json(name = "picture") val picture: String? = null,
    @Json(name = "email") val email: String? = null
)

interface OAuthTokenApiService {

    @FormUrlEncoded
    @POST
    suspend fun exchangeToken(
        @Url url: String,
        @FieldMap fields: Map<String, String>,
        @Header("Authorization") authHeader: String? = null
    ): Response<OAuthTokenResponse>

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun exchangeSpotifyAuthorizationCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Header("Authorization") basicAuthHeader: String? = null
    ): Response<OAuthTokenResponse>

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun refreshSpotifyToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Header("Authorization") basicAuthHeader: String? = null
    ): Response<OAuthTokenResponse>

    @FormUrlEncoded
    @POST("https://oauth2.googleapis.com/token")
    suspend fun exchangeGoogleAuthorizationCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String? = null
    ): Response<OAuthTokenResponse>

    @FormUrlEncoded
    @POST("https://oauth2.googleapis.com/token")
    suspend fun refreshGoogleToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String? = null
    ): Response<OAuthTokenResponse>

    @GET("https://www.googleapis.com/oauth2/v3/userinfo")
    suspend fun getGoogleUserInfo(
        @Header("Authorization") authHeader: String
    ): Response<GoogleUserInfoResponse>

    companion object {
        fun create(): OAuthTokenApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(OAuthTokenApiService::class.java)
        }
    }
}
