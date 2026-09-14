package com.example.data.model

enum class DownloadStatus {
    NOT_DOWNLOADED,
    PENDING,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
}

data class TrackDownloadState(
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val progressPercent: Int = 0,
    val errorMessage: String? = null
)
