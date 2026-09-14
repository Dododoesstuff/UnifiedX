package com.example.data.model

enum class AudioQuality(
    val title: String,
    val description: String,
    val badge: String,
    val isPremiumOnly: Boolean = false
) {
    NORMAL("Normal Quality", "160 kbps Ogg / AAC stream", "160 kbps", false),
    HIGH("High Quality", "320 kbps Extreme Fidelity", "320 kbps", false),
    LOSSLESS("Master Hi-Res", "24-bit / 96kHz Lossless FLAC", "Hi-Res FLAC", true)
}
