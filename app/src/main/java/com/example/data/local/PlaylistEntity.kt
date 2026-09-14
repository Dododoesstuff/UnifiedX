package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val isUnified: Boolean = true,
    val isCollaborative: Boolean = false,
    val sessionCode: String? = null,
    val isOfflineAvailable: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
