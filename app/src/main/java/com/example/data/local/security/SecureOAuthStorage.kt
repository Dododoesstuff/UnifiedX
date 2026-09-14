package com.example.data.local.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.data.model.oauth.OAuthPlatform
import com.example.data.model.oauth.OAuthToken
import com.example.data.model.oauth.OAuthUserProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class SavedAccountRecord(
    val email: String,
    val displayName: String,
    val platform: String,
    val avatarUrl: String? = null,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

interface SecureOAuthStorage {
    fun saveToken(platform: OAuthPlatform, token: OAuthToken)
    fun getToken(platform: OAuthPlatform): OAuthToken?
    fun clearToken(platform: OAuthPlatform)
    fun saveClientCredentials(platform: OAuthPlatform, clientId: String, clientSecret: String?)
    fun getClientId(platform: OAuthPlatform): String?
    fun getClientSecret(platform: OAuthPlatform): String?
    fun saveUserProfile(platform: OAuthPlatform, profile: OAuthUserProfile)
    fun getUserProfile(platform: OAuthPlatform): OAuthUserProfile?
    fun saveAccountRecord(platform: OAuthPlatform, email: String, displayName: String)
    fun getSavedAccounts(platform: OAuthPlatform): List<SavedAccountRecord>
    fun removeAccountRecord(platform: OAuthPlatform, email: String)
    fun clearAll()
}

/**
 * EncryptedSharedPreferences implementation utilizing AES256-GCM / AES256-SIV
 * backed by the Android Keystore via AndroidX Security Crypto MasterKey.
 */
class EncryptedOAuthStorage(
    private val context: Context
) : SecureOAuthStorage {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val tokenAdapter = moshi.adapter(OAuthToken::class.java)
    private val profileAdapter = moshi.adapter(OAuthUserProfile::class.java)
    private val accountsListAdapter = moshi.adapter<List<SavedAccountRecord>>(
        Types.newParameterizedType(List::class.java, SavedAccountRecord::class.java)
    )

    private val prefs: SharedPreferences by lazy {
        initEncryptedPrefs()
    }

    private fun initEncryptedPrefs(): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences, falling back to private preferences", e)
            try {
                // If encrypted file was corrupted, reset it
                context.deleteSharedPreferences(PREFS_NAME)
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Secondary EncryptedSharedPreferences init failed, using private preferences", fallbackException)
                context.getSharedPreferences("${PREFS_NAME}_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    override fun saveToken(platform: OAuthPlatform, token: OAuthToken) {
        try {
            val json = tokenAdapter.toJson(token)
            prefs.edit()
                .putString(tokenKey(platform), json)
                .putString(rawAccessTokenKey(platform), token.accessToken)
                .putLong(expiresAtKey(platform), token.expiresAtMillis)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save token for ${platform.name}", e)
        }
    }

    override fun getToken(platform: OAuthPlatform): OAuthToken? {
        return try {
            val json = prefs.getString(tokenKey(platform), null)
            if (!json.isNullOrBlank()) {
                tokenAdapter.fromJson(json)
            } else {
                // Check if raw token was stored
                val rawToken = prefs.getString(rawAccessTokenKey(platform), null)
                val expiresAt = prefs.getLong(expiresAtKey(platform), 0L)
                if (!rawToken.isNullOrBlank()) {
                    val remaining = if (expiresAt > 0) (expiresAt - System.currentTimeMillis()) / 1000L else 3600L
                    OAuthToken(
                        accessToken = rawToken,
                        expiresInSeconds = if (remaining > 0) remaining else 3600L,
                        issuedAtMillis = System.currentTimeMillis()
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read token for ${platform.name}", e)
            null
        }
    }

    override fun clearToken(platform: OAuthPlatform) {
        try {
            prefs.edit()
                .remove(tokenKey(platform))
                .remove(rawAccessTokenKey(platform))
                .remove(expiresAtKey(platform))
                .remove(profileKey(platform))
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear token for ${platform.name}", e)
        }
    }

    override fun saveClientCredentials(platform: OAuthPlatform, clientId: String, clientSecret: String?) {
        try {
            val editor = prefs.edit().putString(clientIdKey(platform), clientId.trim())
            if (clientSecret != null) {
                editor.putString(clientSecretKey(platform), clientSecret.trim())
            } else {
                editor.remove(clientSecretKey(platform))
            }
            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save client credentials for ${platform.name}", e)
        }
    }

    override fun getClientId(platform: OAuthPlatform): String? {
        return prefs.getString(clientIdKey(platform), platform.defaultClientId)
    }

    override fun getClientSecret(platform: OAuthPlatform): String? {
        return prefs.getString(clientSecretKey(platform), null)
    }

    override fun saveUserProfile(platform: OAuthPlatform, profile: OAuthUserProfile) {
        try {
            val json = profileAdapter.toJson(profile)
            prefs.edit().putString(profileKey(platform), json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user profile for ${platform.name}", e)
        }
    }

    override fun getUserProfile(platform: OAuthPlatform): OAuthUserProfile? {
        return try {
            val json = prefs.getString(profileKey(platform), null)
            if (!json.isNullOrBlank()) {
                profileAdapter.fromJson(json)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read profile for ${platform.name}", e)
            null
        }
    }

    override fun saveAccountRecord(platform: OAuthPlatform, email: String, displayName: String) {
        try {
            val current = getSavedAccounts(platform).toMutableList()
            current.removeAll { it.email.equals(email.trim(), ignoreCase = true) }
            current.add(0, SavedAccountRecord(
                email = email.trim(),
                displayName = displayName.trim(),
                platform = platform.name,
                lastActiveTimestamp = System.currentTimeMillis()
            ))
            val json = accountsListAdapter.toJson(current.take(10))
            prefs.edit().putString(savedAccountsKey(platform), json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save account record for ${platform.name}", e)
        }
    }

    override fun getSavedAccounts(platform: OAuthPlatform): List<SavedAccountRecord> {
        return try {
            val json = prefs.getString(savedAccountsKey(platform), null)
            if (!json.isNullOrBlank()) {
                accountsListAdapter.fromJson(json) ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read saved accounts for ${platform.name}", e)
            emptyList()
        }
    }

    override fun removeAccountRecord(platform: OAuthPlatform, email: String) {
        try {
            val current = getSavedAccounts(platform).toMutableList()
            current.removeAll { it.email.equals(email.trim(), ignoreCase = true) }
            val json = accountsListAdapter.toJson(current)
            prefs.edit().putString(savedAccountsKey(platform), json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove account record for ${platform.name}", e)
        }
    }

    override fun clearAll() {
        try {
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all secure OAuth storage", e)
        }
    }

    private fun tokenKey(platform: OAuthPlatform) = "oauth_token_${platform.name.lowercase()}"
    private fun rawAccessTokenKey(platform: OAuthPlatform) = "oauth_raw_access_token_${platform.name.lowercase()}"
    private fun expiresAtKey(platform: OAuthPlatform) = "oauth_expires_at_${platform.name.lowercase()}"
    private fun clientIdKey(platform: OAuthPlatform) = "oauth_client_id_${platform.name.lowercase()}"
    private fun clientSecretKey(platform: OAuthPlatform) = "oauth_client_secret_${platform.name.lowercase()}"
    private fun profileKey(platform: OAuthPlatform) = "oauth_profile_${platform.name.lowercase()}"
    private fun savedAccountsKey(platform: OAuthPlatform) = "oauth_saved_accounts_${platform.name.lowercase()}"

    companion object {
        private const val TAG = "EncryptedOAuthStorage"
        private const val PREFS_NAME = "secure_oauth_credentials_prefs"
    }
}
