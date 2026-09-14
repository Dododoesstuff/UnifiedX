package com.example.data.model

enum class PlatformSource(
    val displayName: String,
    val brandColorHex: Long,
    val shortBadge: String
) {
    SPOTIFY("Spotify", 0xFF1ED760, "Spotify"),
    YOUTUBE("YouTube", 0xFFFF0033, "YouTube"),
    LOCAL("Local Offline", 0xFF89CFF0, "Offline")
}
