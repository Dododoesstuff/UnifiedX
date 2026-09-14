package com.example.data.model

data class LyricsLine(
    val timestampMs: Long,
    val text: String
)

object LyricsParser {
    fun parseLrc(lrcContent: String): List<LyricsLine> {
        val lines = mutableListOf<LyricsLine>()
        val regex = Regex("""\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?\](.*)""")
        
        lrcContent.lineSequence().forEach { line ->
            val match = regex.find(line.trim())
            if (match != null) {
                val minutes = match.groupValues[1].toLongOrNull() ?: 0L
                val seconds = match.groupValues[2].toLongOrNull() ?: 0L
                val fractionStr = match.groupValues[3]
                val millis = when {
                    fractionStr.isEmpty() -> 0L
                    fractionStr.length == 2 -> (fractionStr.toLongOrNull() ?: 0L) * 10
                    else -> fractionStr.take(3).toLongOrNull() ?: 0L
                }
                val totalMs = minutes * 60_000L + seconds * 1000L + millis
                val text = match.groupValues[4].trim()
                if (text.isNotEmpty()) {
                    lines.add(LyricsLine(totalMs, text))
                }
            }
        }
        return lines.sortedBy { it.timestampMs }
    }

    fun getActiveIndex(lines: List<LyricsLine>, currentPositionMs: Long): Int {
        if (lines.isEmpty()) return -1
        var activeIdx = -1
        for (i in lines.indices) {
            if (lines[i].timestampMs <= currentPositionMs) {
                activeIdx = i
            } else {
                break
            }
        }
        return activeIdx
    }
}
