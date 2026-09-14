package com.example.collab

import android.content.Context
import android.content.Intent
import com.example.data.local.TrackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Participant(
    val id: String,
    val name: String,
    val avatarColorHex: Long,
    val isHost: Boolean = false,
    val platformBadge: String = "Spotify"
)

data class CollabQueueItem(
    val id: String,
    val track: TrackEntity,
    val addedBy: String,
    val votes: Int = 1,
    val hasVoted: Boolean = false
)

data class CollabSessionState(
    val isInSession: Boolean = false,
    val sessionCode: String = "BEAT-42",
    val sessionName: String = "Cross-Platform Midnight Jam",
    val isHost: Boolean = true,
    val participants: List<Participant> = listOf(
        Participant("u1", "You (Host)", 0xFF1ED760, true, "Spotify"),
        Participant("u2", "Maya Lin", 0xFFFF0033, false, "YouTube"),
        Participant("u3", "Alex Rivera", 0xFF89CFF0, false, "Spotify"),
        Participant("u4", "Jordan Chen", 0xFF00C4CC, false, "YouTube")
    ),
    val collaborativeQueue: List<CollabQueueItem> = emptyList(),
    val nowPlayingItem: CollabQueueItem? = null
)

class CollaborativeSessionManager(private val context: Context) {
    private val _sessionState = MutableStateFlow(CollabSessionState())
    val sessionState: StateFlow<CollabSessionState> = _sessionState.asStateFlow()

    fun hostNewSession(sessionName: String = "Cross-Platform Midnight Jam"): String {
        val code = "JAM-" + (100..999).random()
        _sessionState.value = _sessionState.value.copy(
            isInSession = true,
            sessionCode = code,
            sessionName = sessionName,
            isHost = true
        )
        return code
    }

    fun joinSession(code: String) {
        val cleanCode = code.trim().uppercase()
        _sessionState.value = _sessionState.value.copy(
            isInSession = true,
            sessionCode = cleanCode,
            isHost = false
        )
    }

    fun leaveSession() {
        _sessionState.value = _sessionState.value.copy(
            isInSession = false,
            collaborativeQueue = emptyList(),
            nowPlayingItem = null
        )
    }

    fun addTrackToLiveQueue(track: TrackEntity, addedBy: String = "You") {
        val newItem = CollabQueueItem(
            id = "q_" + System.currentTimeMillis(),
            track = track,
            addedBy = addedBy,
            votes = 1,
            hasVoted = true
        )
        val currentQueue = _sessionState.value.collaborativeQueue.toMutableList()
        currentQueue.add(newItem)
        _sessionState.value = _sessionState.value.copy(
            collaborativeQueue = currentQueue.sortedByDescending { it.votes }
        )
    }

    fun toggleUpvote(queueItemId: String) {
        val currentQueue = _sessionState.value.collaborativeQueue.map { item ->
            if (item.id == queueItemId) {
                val newHasVoted = !item.hasVoted
                val newVotes = if (newHasVoted) item.votes + 1 else (item.votes - 1).coerceAtLeast(0)
                item.copy(votes = newVotes, hasVoted = newHasVoted)
            } else {
                item
            }
        }.sortedByDescending { it.votes }

        _sessionState.value = _sessionState.value.copy(collaborativeQueue = currentQueue)
    }

    fun shareSessionInvite() {
        val state = _sessionState.value
        val shareText = """
            🎵 Join my collaborative music session on UnifiedX!
            Room Code: ${state.sessionCode}
            We're playing songs across Spotify & YouTube simultaneously!
            Join here: https://unifiedx.app/jam/${state.sessionCode}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Join my UnifiedX Music Jam!")
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Invite friends to Jam Session").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareTrackSocially(track: TrackEntity, lyricSnippet: String? = null) {
        val sourceBadge = if (track.platformSource == com.example.data.model.PlatformSource.SPOTIFY) "Spotify 🟢" else "YouTube 🔴"
        val snippetText = if (!lyricSnippet.isNullOrBlank()) "\n\"$lyricSnippet\"\n" else ""
        val shareText = """
            🎶 Currently vibing on UnifiedX:
            ${track.title} by ${track.artist}
            Source: $sourceBadge • ${track.audioQuality.badge}
            $snippetText
            Unified Spotify & YouTube Playlists on UnifiedX
            https://unifiedx.app/track/${track.id}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Listening to ${track.title} on UnifiedX")
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share track via...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
