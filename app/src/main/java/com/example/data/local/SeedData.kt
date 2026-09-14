package com.example.data.local

import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource

object SeedData {
    val initialTracks = listOf(
        TrackEntity(
            id = "sp_001",
            title = "Midnight Echoes",
            artist = "Aura Nova",
            album = "Neon Horizon (Spotify Original)",
            durationMs = 214000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:4cOdK2wGLETKBW3PvgPWqT",
            coverUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = true,
            downloadedBytes = 14200000L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Instrumental synth intro)
                [00:05.50] Floating in the neon light
                [00:10.20] City shadows fade into the night
                [00:15.80] Whispers on the frequency
                [00:20.40] Bringing all your memories back to me
                [00:26.10] Can you feel the bassline resonate?
                [00:31.50] We don't have to hesitate
                [00:36.80] Midnight echoes through the avenue
                [00:42.20] Every beat is pulling me to you
                [00:48.00] (Electronic synth pulse)
                [00:58.50] Synchronized across the stars
                [01:04.00] No matter where we are
                [01:09.60] In this unified frequency
                [01:15.00] You're the melody inside of me
            """.trimIndent(),
            spotifyEquivalentId = "sp_001",
            youtubeEquivalentId = "yt_101",
            genre = "Electronic",
            playCount = 142
        ),
        TrackEntity(
            id = "yt_101",
            title = "Cyber Sunset (Live Session)",
            artist = "Kavinsky Wave",
            album = "YouTube Live Sessions 4K",
            durationMs = 198000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:dQw4w9WgXcQ",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Drum and bass warm up)
                [00:07.00] Watching purple skies descend
                [00:12.40] Digital horizons never end
                [00:18.00] High definition audio stream
                [00:23.50] Live from the studio, chasing a dream
                [00:29.80] All the comments rolling by
                [00:35.20] Underneath this cyber sky
                [00:41.00] We connect two worlds as one
                [00:46.50] Long before the morning sun
                [00:53.00] (Synthesizer crescendo)
                [01:04.20] Streaming live across the globe
                [01:10.00] Resonance in stereo
            """.trimIndent(),
            spotifyEquivalentId = "sp_001",
            youtubeEquivalentId = "yt_101",
            genre = "Synthwave",
            playCount = 98
        ),
        TrackEntity(
            id = "sp_002",
            title = "Velvet Rain",
            artist = "Maya Lin & The Strings",
            album = "Acoustic Resonance",
            durationMs = 230000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:5x7bJ0h1iYhJ29LqG",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = true,
            downloadedBytes = 18400000L,
            isLiked = false,
            lyricsLrc = """
                [00:00.00] (Soft piano chords)
                [00:06.80] Raindrops on the window pane
                [00:12.00] Washing out the gentle pain
                [00:17.50] In every chord and whispered sigh
                [00:23.00] Clouds are drifting through the sky
                [00:29.50] Acoustic strings begin to chime
                [00:35.20] Freezing all the hands of time
                [00:41.40] Soft velvet rain, hold me near
                [00:47.00] Making everything so clear
            """.trimIndent(),
            spotifyEquivalentId = "sp_002",
            youtubeEquivalentId = "yt_102",
            genre = "Acoustic",
            playCount = 67
        ),
        TrackEntity(
            id = "yt_102",
            title = "Coffee Shop Lo-Fi Beats",
            artist = "Chilled Cow Studio",
            album = "Rainy Day Study Mix",
            durationMs = 185000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:jfKfPfyJRdk",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = true,
            downloadedBytes = 7800000L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Vinyl crackle and warm Rhodes chords)
                [00:10.00] Sip of coffee, page by page
                [00:20.50] Safe inside this quiet stage
                [00:31.00] Lo-Fi rhythm keeps the pace
                [00:42.00] Slowing down the daily chase
                [00:54.00] (Mellow brass solo)
                [01:06.00] Midnight studying till dawn
                [01:18.00] While the rest of earth is gone
            """.trimIndent(),
            spotifyEquivalentId = "sp_003",
            youtubeEquivalentId = "yt_102",
            genre = "Lo-Fi",
            playCount = 230
        ),
        TrackEntity(
            id = "sp_003",
            title = "Solar Flare",
            artist = "Solaris Project",
            album = "Cosmic Odyssey",
            durationMs = 240000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:124JkL99asdx8",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = false,
            lyricsLrc = """
                [00:00.00] (Bass sweep)
                [00:08.50] Burst of energy in the dark
                [00:14.20] Igniting every single spark
                [00:20.00] Higher than the troposphere
                [00:26.10] Nothing left for us to fear
                [00:32.40] Solar flare is breaking through
                [00:38.20] Shining golden over you
            """.trimIndent(),
            spotifyEquivalentId = "sp_003",
            youtubeEquivalentId = "yt_103",
            genre = "Electronic",
            playCount = 89
        ),
        TrackEntity(
            id = "yt_103",
            title = "Streetlights in Tokyo",
            artist = "Kenji Takahashi",
            album = "Shinjuku After Dark",
            durationMs = 210000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:7bXN3zHwP8s",
            coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = true,
            downloadedBytes = 9100000L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Ambient subway chimes)
                [00:07.50] Walking through the neon signs
                [00:13.80] Reading between all the lines
                [00:20.20] Shinjuku nights are burning bright
                [00:26.50] Tokyo under midnight light
                [00:33.00] Cross the street and take my hand
                [00:39.50] Music only we understand
            """.trimIndent(),
            spotifyEquivalentId = "sp_004",
            youtubeEquivalentId = "yt_103",
            genre = "City Pop",
            playCount = 175
        ),
        TrackEntity(
            id = "sp_004",
            title = "Gravity Well",
            artist = "The Quantum Band",
            album = "Hyperspace",
            durationMs = 225000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:98124jkasdb8",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Heavy bass drop)
                [00:09.00] Pulling me into your orbit now
                [00:15.50] Gravity won't let me down
                [00:22.00] Floating free in zero g
                [00:28.50] Endless deep velocity
            """.trimIndent(),
            spotifyEquivalentId = "sp_004",
            youtubeEquivalentId = "yt_104",
            genre = "Indie Rock",
            playCount = 112
        ),
        TrackEntity(
            id = "yt_104",
            title = "Acoustic Campfire Covers",
            artist = "The Wildwood Collective",
            album = "Pine & Stars Live EP",
            durationMs = 205000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:camp9238jds",
            coverUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = false,
            lyricsLrc = """
                [00:00.00] (Acoustic guitar strumming)
                [00:07.00] Fire crackles in the breeze
                [00:13.20] Wind is whistling through the trees
                [00:19.50] Sing along to songs we know
                [00:26.00] In this warm and embers glow
            """.trimIndent(),
            spotifyEquivalentId = "sp_002",
            youtubeEquivalentId = "yt_104",
            genre = "Folk",
            playCount = 54
        ),
        // Additional Spotify Catalog Tracks
        TrackEntity(
            id = "sp_005",
            title = "Starboy (Nightfall Remix)",
            artist = "The Weeknd & Daft Punk",
            album = "Starboy (Deluxe)",
            durationMs = 230000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:7MXVkk9YM5IZUtovv4fscV",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Synth bass intro)
                [00:08.00] I'm tryin' to put you in the worst mood, ah
                [00:12.00] P1 cleaner than your church shoes, ah
                [00:16.00] Point-one-point-two just to hurt you, ah
                [00:20.00] All red Lamb' just to tease you, ah
                [00:24.00] Look what you've done, I'm a motherfuckin' starboy
            """.trimIndent(),
            spotifyEquivalentId = "sp_005",
            youtubeEquivalentId = "yt_105",
            genre = "R&B / Pop",
            playCount = 310
        ),
        TrackEntity(
            id = "sp_006",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationMs = 200000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:0VjIjW4GlUZAMYd2vXMi3b",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = true,
            downloadedBytes = 16200000L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (80s Synth opening)
                [00:06.00] Yeah, I've been tryna call
                [00:10.00] I've been on my own for long enough
                [00:14.00] Maybe you can show me how to love, maybe
                [00:20.00] I'm going through withdrawals
                [00:25.00] You don't even have to do too much
                [00:29.00] I said, ooh, I'm blinded by the lights
            """.trimIndent(),
            spotifyEquivalentId = "sp_006",
            youtubeEquivalentId = "yt_106",
            genre = "Synthwave / Pop",
            playCount = 490
        ),
        TrackEntity(
            id = "sp_007",
            title = "Levitating",
            artist = "Dua Lipa",
            album = "Future Nostalgia",
            durationMs = 203000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:463SpQwh87Xi3HG9u599Ob",
            coverUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Funky bass groove)
                [00:05.00] If you wanna run away with me, I know a galaxy
                [00:10.00] And I can take you for a ride
                [00:15.00] I had a premonition that we fell into a rhythm
                [00:20.00] Where the music don't stop for life
                [00:25.00] You want me, I want you, baby
                [00:30.00] My sugarboo, I'm levitating
            """.trimIndent(),
            spotifyEquivalentId = "sp_007",
            youtubeEquivalentId = "yt_107",
            genre = "Disco Pop",
            playCount = 280
        ),
        TrackEntity(
            id = "sp_008",
            title = "As It Was",
            artist = "Harry Styles",
            album = "Harry's House",
            durationMs = 167000L,
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:4Dvkj6JhhA12EX05fT7y2e",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = true,
            downloadedBytes = 14800000L,
            isLiked = false,
            lyricsLrc = """
                [00:00.00] Come on, Harry, we wanna say goodnight to you
                [00:05.00] Holdin' me back
                [00:09.00] Gravity's holdin' me back
                [00:13.00] I want you to hold out the palm of your hand
                [00:17.00] Why don't we leave it at that?
                [00:21.00] You know it's not the same as it was
            """.trimIndent(),
            spotifyEquivalentId = "sp_008",
            youtubeEquivalentId = "yt_108",
            genre = "Indie Pop",
            playCount = 340
        ),
        // Additional YouTube Catalog Tracks & Live Studio Recordings
        TrackEntity(
            id = "yt_105",
            title = "Bad Guy (Live at Glastonbury)",
            artist = "Billie Eilish",
            album = "BBC Music Live at Worthy Farm",
            durationMs = 214000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:DyDfgMOUjCI",
            coverUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = true,
            downloadedBytes = 8900000L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Crowd cheering, live 808 sub bass)
                [00:06.00] White shirt now red, my bloody nose
                [00:10.00] Sleepin', you're on your tippy toes
                [00:14.00] Creepin' around like no one knows
                [00:18.00] Think you're so criminal
                [00:23.00] So you're a tough guy, like it really rough guy
                [00:28.00] I'm that bad type, make your mama sad type
                [00:33.00] I'm the bad guy... duh!
            """.trimIndent(),
            spotifyEquivalentId = "sp_009",
            youtubeEquivalentId = "yt_105",
            genre = "Alternative Pop",
            playCount = 420
        ),
        TrackEntity(
            id = "yt_106",
            title = "Bohemian Rhapsody (Live at Wembley Stadium)",
            artist = "Queen",
            album = "Queen Live Tribute Collection 4K",
            durationMs = 354000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:fJ9rUzIMcZQ",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Piano intro live)
                [00:10.00] Is this the real life? Is this just fantasy?
                [00:19.00] Caught in a landslide, no escape from reality
                [00:28.00] Open your eyes, look up to the skies and see
                [00:37.00] I'm just a poor boy, I need no sympathy
                [00:46.00] Because I'm easy come, easy go, little high, little low
                [00:55.00] Anyway the wind blows doesn't really matter to me
            """.trimIndent(),
            spotifyEquivalentId = "sp_010",
            youtubeEquivalentId = "yt_106",
            genre = "Classic Rock",
            playCount = 610
        ),
        TrackEntity(
            id = "yt_107",
            title = "Stay (Acoustic Studio Version)",
            artist = "The Kid LAROI & Justin Bieber",
            album = "Vevo Studio Sessions",
            durationMs = 142000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:kTJczUoc26U",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = true,
            downloadedBytes = 7200000L,
            isLiked = false,
            lyricsLrc = """
                [00:00.00] (Acoustic guitar strums)
                [00:04.00] I do the same thing I told you that I never would
                [00:08.00] I told you I'd change, even when I knew I never could
                [00:12.00] I know that I can't find nobody else as good as you
                [00:17.00] I need you to stay, need you to stay, hey
            """.trimIndent(),
            spotifyEquivalentId = "sp_011",
            youtubeEquivalentId = "yt_107",
            genre = "Pop Rap / Acoustic",
            playCount = 390
        ),
        TrackEntity(
            id = "yt_108",
            title = "Synthwave 1984 (Analog Live Modular Set)",
            artist = "Timecop1983 & The Midnight",
            album = "YouTube Red Bull Soundstage 4K",
            durationMs = 280000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:8ZhwZZq82aA",
            coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            audioQuality = AudioQuality.HIGH,
            isDownloaded = false,
            downloadedBytes = 0L,
            isLiked = true,
            lyricsLrc = """
                [00:00.00] (Modular analog filter sweep)
                [00:15.00] Driving through the sunset neon highway
                [00:25.00] Listening to radio waves from yesterday
                [00:38.00] We were young and wild and free
                [00:50.00] Forever locked in 1984 memory
            """.trimIndent(),
            spotifyEquivalentId = "sp_001",
            youtubeEquivalentId = "yt_108",
            genre = "Synthwave",
            playCount = 270
        )
    )

    val initialPlaylists = listOf(
        PlaylistEntity(
            id = "pl_unified_top",
            title = "⚡ Spotify & YouTube Fusion",
            description = "Seamless blend of high-definition Spotify tracks and live YouTube studio recordings.",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            isUnified = true,
            isCollaborative = true,
            sessionCode = "JAM777"
        ),
        PlaylistEntity(
            id = "pl_night_drive",
            title = "🌙 Midnight Synths & Lo-Fi",
            description = "Cross-platform chill vibes for nighttime focus and night drives.",
            coverUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
            isUnified = true,
            isCollaborative = false
        ),
        PlaylistEntity(
            id = "pl_offline_vault",
            title = "📥 Offline Premium Vault",
            description = "High-bitrate lossless audio tracks cached locally on your device for offline flights and travels.",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            isUnified = true,
            isOfflineAvailable = true
        )
    )

    val initialCrossRefs = listOf(
        // Playlist 1 (Fusion)
        PlaylistTrackCrossRef("pl_unified_top", "sp_001", 0),
        PlaylistTrackCrossRef("pl_unified_top", "yt_101", 1),
        PlaylistTrackCrossRef("pl_unified_top", "sp_002", 2),
        PlaylistTrackCrossRef("pl_unified_top", "yt_103", 3),

        // Playlist 2 (Midnight)
        PlaylistTrackCrossRef("pl_night_drive", "sp_001", 0),
        PlaylistTrackCrossRef("pl_night_drive", "yt_102", 1),
        PlaylistTrackCrossRef("pl_night_drive", "yt_103", 2),

        // Playlist 3 (Offline)
        PlaylistTrackCrossRef("pl_offline_vault", "sp_001", 0),
        PlaylistTrackCrossRef("pl_offline_vault", "sp_002", 1),
        PlaylistTrackCrossRef("pl_offline_vault", "yt_102", 2),
        PlaylistTrackCrossRef("pl_offline_vault", "yt_103", 3)
    )

    val initialUserPreferences = UserPreferencesEntity(
        id = "default_user_prefs",
        streamingQuality = AudioQuality.HIGH,
        downloadQuality = AudioQuality.LOSSLESS,
        isOfflineModeOnly = false,
        selectedEqualizerPreset = "Studio Hi-Fi",
        spotifyToken = "sp_oauth_live_9921",
        isSpotifyLinked = true,
        youtubeApiKey = "AIzaSy_YouTubeDataV3_LiveDemo",
        isYoutubeLinked = true,
        autoCrossfade = true,
        crossfadeSeconds = 4,
        cacheSizeLimitMb = 2048,
        volumeNormalization = true,
        autoDownloadLikedTracks = false,
        lastLibrarySyncTimestamp = System.currentTimeMillis()
    )

    val initialCachedPlaylists = listOf(
        CachedPlaylistMetadataEntity(
            playlistId = "pl_unified_top",
            title = "🔥 Spotify & YouTube Fusion Mix",
            description = "Seamless crossover blending trending hits from Spotify Charts & YouTube Trending",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            totalTrackCount = 4,
            downloadedTrackCount = 3,
            totalDurationMs = 862000L,
            cachedSizeBytes = 47800000L,
            platformSource = PlatformSource.SPOTIFY,
            isOfflinePinned = true,
            isFullyDownloaded = false,
            lastCachedTimestamp = System.currentTimeMillis()
        ),
        CachedPlaylistMetadataEntity(
            playlistId = "pl_offline_vault",
            title = "📥 Offline Premium Vault",
            description = "High-bitrate lossless audio tracks cached locally on your device for offline flights and travels.",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            totalTrackCount = 4,
            downloadedTrackCount = 4,
            totalDurationMs = 852000L,
            cachedSizeBytes = 67200000L,
            platformSource = PlatformSource.SPOTIFY,
            isOfflinePinned = true,
            isFullyDownloaded = true,
            lastCachedTimestamp = System.currentTimeMillis()
        ),
        CachedPlaylistMetadataEntity(
            playlistId = "pl_night_drive",
            title = "🌙 Midnight Synths & Lo-Fi",
            description = "Cross-platform chill vibes for nighttime focus and night drives.",
            coverUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
            totalTrackCount = 3,
            downloadedTrackCount = 1,
            totalDurationMs = 622000L,
            cachedSizeBytes = 14200000L,
            platformSource = PlatformSource.YOUTUBE,
            isOfflinePinned = false,
            isFullyDownloaded = false,
            lastCachedTimestamp = System.currentTimeMillis()
        )
    )
}
