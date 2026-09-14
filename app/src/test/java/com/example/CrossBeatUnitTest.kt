package com.example

import com.example.data.model.AudioQuality
import com.example.data.model.LyricsParser
import com.example.data.model.PlatformSource
import org.junit.Assert.assertEquals
import org.junit.Test

class CrossBeatUnitTest {

    @Test
    fun testLyricsParsing() {
        val lrc = """
            [00:05.50] Midnight neon glowing bright
            [00:12.00] Echoes of our thoughts tonight
        """.trimIndent()

        val parsed = LyricsParser.parseLrc(lrc)
        assertEquals(2, parsed.size)
        assertEquals(5500L, parsed[0].timestampMs)
        assertEquals("Midnight neon glowing bright", parsed[0].text)
        assertEquals(12000L, parsed[1].timestampMs)
        assertEquals("Echoes of our thoughts tonight", parsed[1].text)
    }

    @Test
    fun testActiveLyricIndex() {
        val lrc = """
            [00:05.00] Line one
            [00:10.00] Line two
            [00:15.00] Line three
        """.trimIndent()
        val parsed = LyricsParser.parseLrc(lrc)

        assertEquals(0, LyricsParser.getActiveIndex(parsed, 6000L))
        assertEquals(1, LyricsParser.getActiveIndex(parsed, 11000L))
        assertEquals(2, LyricsParser.getActiveIndex(parsed, 20000L))
    }

    @Test
    fun testPlatformSourceAndAudioQuality() {
        assertEquals("Spotify", PlatformSource.SPOTIFY.displayName)
        assertEquals("YouTube", PlatformSource.YOUTUBE.displayName)

        assertEquals("Hi-Res FLAC", AudioQuality.LOSSLESS.badge)
        assertEquals("320 kbps", AudioQuality.HIGH.badge)
    }
}
