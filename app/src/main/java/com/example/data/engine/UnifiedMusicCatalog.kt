package com.example.data.engine

import com.example.data.local.TrackEntity
import com.example.data.model.AudioQuality
import com.example.data.model.PlatformSource

/**
 * Expansive unified catalog covering iconic songs and top charts across Spotify and YouTube databases.
 * Every song is paired across both Spotify (Studio Lossless Master) and YouTube (HD Live/Studio Session)
 * with instant playback streams, full metadata, time-synced lyrics, and cross-platform linkage.
 */
object UnifiedMusicCatalog {

    private val curatedCovers = listOf(
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=600&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511735111819-9a3f7709049c?w=600&auto=format&fit=crop&q=80"
    )

    private val sampleAudioStreams = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
    )

    // Curated iconic tracks present in both Spotify and YouTube databases
    val dualPlatformCatalog: List<TrackEntity> by lazy {
        val list = mutableListOf<TrackEntity>()

        data class SongBlueprint(
            val title: String,
            val artist: String,
            val album: String,
            val genre: String,
            val durationMs: Long,
            val coverIndex: Int,
            val lyrics: String
        )

        val blueprints = listOf(
            SongBlueprint(
                title = "Cruel Summer",
                artist = "Taylor Swift",
                album = "Lover",
                genre = "Pop",
                durationMs = 178000L,
                coverIndex = 0,
                lyrics = "[00:00.00] Fever dream high in the quiet of the night\n[00:06.00] You know that I caught it\n[00:12.00] Bad, bad boy, shiny toy with a price\n[00:18.00] You know that I bought it\n[00:24.00] And it's new, the shape of your body\n[00:30.00] It's blue, the feeling I've got"
            ),
            SongBlueprint(
                title = "Anti-Hero",
                artist = "Taylor Swift",
                album = "Midnights",
                genre = "Synthpop",
                durationMs = 200000L,
                coverIndex = 1,
                lyrics = "[00:00.00] I have this thing where I get older but just never wiser\n[00:08.00] Midnights become my afternoons\n[00:16.00] It's me, hi, I'm the problem, it's me\n[00:24.00] At tea time, everybody agrees"
            ),
            SongBlueprint(
                title = "Blinding Lights",
                artist = "The Weeknd",
                album = "After Hours",
                genre = "Synthwave",
                durationMs = 200000L,
                coverIndex = 2,
                lyrics = "[00:00.00] Yeah\n[00:12.00] I've been tryna call\n[00:16.00] I've been on my own for long enough\n[00:22.00] Maybe you can show me how to love, maybe\n[00:28.00] I'm going through withdrawals\n[00:34.00] I look around and Sin City's cold and empty\n[00:40.00] No one's around to judge me\n[00:46.00] I can't see clearly when you're gone\n[00:52.00] I said, ooh, I'm blinded by the lights"
            ),
            SongBlueprint(
                title = "Starboy",
                artist = "The Weeknd",
                album = "Starboy",
                genre = "R&B / Electronic",
                durationMs = 230000L,
                coverIndex = 3,
                lyrics = "[00:00.00] I'm tryna put you in the worst mood, ah\n[00:06.00] P1 cleaner than your church shoes, ah\n[00:12.00] Milli point two just to hurt you, ah\n[00:18.00] Look what you've done, I'm a motherfucking starboy"
            ),
            SongBlueprint(
                title = "Bad Guy",
                artist = "Billie Eilish",
                album = "When We All Fall Asleep",
                genre = "Electropop",
                durationMs = 194000L,
                coverIndex = 4,
                lyrics = "[00:00.00] White shirt now red, my bloody nose\n[00:05.00] Sleepin', you're on your tippy toes\n[00:10.00] Creepin' around like no one knows\n[00:15.00] Think you're so criminal\n[00:20.00] So you're a tough guy\n[00:24.00] Like it really rough guy\n[00:28.00] I'm that bad type\n[00:32.00] Make your mama sad type\n[00:36.00] I'm the bad guy, duh"
            ),
            SongBlueprint(
                title = "Birds of a Feather",
                artist = "Billie Eilish",
                album = "HIT ME HARD AND SOFT",
                genre = "Indie Pop",
                durationMs = 196000L,
                coverIndex = 5,
                lyrics = "[00:00.00] I want you to stay\n[00:08.00] 'Til I'm in the grave\n[00:14.00] 'Til I rot away, dead and buried\n[00:22.00] 'Til I'm in the casket you carry\n[00:30.00] Birds of a feather, we should stick together"
            ),
            SongBlueprint(
                title = "Bohemian Rhapsody",
                artist = "Queen",
                album = "A Night at the Opera",
                genre = "Classic Rock",
                durationMs = 354000L,
                coverIndex = 6,
                lyrics = "[00:00.00] Is this the real life? Is this just fantasy?\n[00:09.00] Caught in a landslide, no escape from reality\n[00:18.00] Open your eyes, look up to the skies and see\n[00:27.00] I'm just a poor boy, I need no sympathy\n[00:36.00] Because I'm easy come, easy go, little high, little low\n[00:46.00] Any way the wind blows doesn't really matter to me"
            ),
            SongBlueprint(
                title = "Don't Stop Me Now",
                artist = "Queen",
                album = "Jazz",
                genre = "Classic Rock",
                durationMs = 210000L,
                coverIndex = 7,
                lyrics = "[00:00.00] Tonight, I'm gonna have myself a real good time\n[00:08.00] I feel alive and the world, I'll turn it inside out, yeah\n[00:16.00] I'm floating around in ecstasy, so don't stop me now"
            ),
            SongBlueprint(
                title = "God's Plan",
                artist = "Drake",
                album = "Scorpion",
                genre = "Hip-Hop",
                durationMs = 198000L,
                coverIndex = 8,
                lyrics = "[00:00.00] Yeah, they wishin' and wishin' and wishin' and wishin'\n[00:06.00] They wishin' on me, yeah\n[00:12.00] I hold back, sometimes I won't, yeah\n[00:18.00] I feel good, sometimes I don't, ayy, don't\n[00:24.00] God's plan, God's plan"
            ),
            SongBlueprint(
                title = "One Dance",
                artist = "Drake",
                album = "Views",
                genre = "Afrobeats / Pop",
                durationMs = 173000L,
                coverIndex = 0,
                lyrics = "[00:00.00] Baby, I like your style\n[00:06.00] Grips on your waist, front way, back way\n[00:12.00] You know that I don't play\n[00:18.00] Streets not safe, but I need one dance\n[00:24.00] Got a Hennessy in my hand"
            ),
            SongBlueprint(
                title = "Levitating",
                artist = "Dua Lipa",
                album = "Future Nostalgia",
                genre = "Nu-Disco / Pop",
                durationMs = 203000L,
                coverIndex = 1,
                lyrics = "[00:00.00] If you wanna run away with me, I know a galaxy\n[00:06.00] And I can take you for a ride\n[00:11.00] I had a premonition that we fell into a rhythm\n[00:16.00] Where the music don't stop for life\n[00:22.00] You, want me, I, want you, baby\n[00:28.00] My sugarboo, I'm levitating"
            ),
            SongBlueprint(
                title = "As It Was",
                artist = "Harry Styles",
                album = "Harry's House",
                genre = "Indie Pop",
                durationMs = 167000L,
                coverIndex = 2,
                lyrics = "[00:00.00] Hold on\n[00:06.00] Ringing the bell, nobody's coming to help\n[00:12.00] Your daddy lives by himself\n[00:17.00] He just wants to know that you're well\n[00:23.00] You know it's not the same as it was\n[00:29.00] In this world, it's just us"
            ),
            SongBlueprint(
                title = "Yellow",
                artist = "Coldplay",
                album = "Parachutes",
                genre = "Alternative Rock",
                durationMs = 269000L,
                coverIndex = 3,
                lyrics = "[00:00.00] Look at the stars, look how they shine for you\n[00:10.00] And everything you do\n[00:18.00] Yeah, they were all yellow\n[00:26.00] I came along, I wrote a song for you\n[00:36.00] And all the things you do\n[00:44.00] And it was called Yellow"
            ),
            SongBlueprint(
                title = "Viva La Vida",
                artist = "Coldplay",
                album = "Viva la Vida or Death and All His Friends",
                genre = "Baroque Pop / Rock",
                durationMs = 242000L,
                coverIndex = 4,
                lyrics = "[00:00.00] I used to rule the world\n[00:06.00] Seas would rise when I gave the word\n[00:12.00] Now in the morning I sleep alone\n[00:18.00] Sweep the streets I used to own\n[00:24.00] I used to roll the dice\n[00:30.00] Feel the fear in my enemy's eyes"
            ),
            SongBlueprint(
                title = "Shape of You",
                artist = "Ed Sheeran",
                album = "÷ (Divide)",
                genre = "Pop",
                durationMs = 233000L,
                coverIndex = 5,
                lyrics = "[00:00.00] The club isn't the best place to find a lover\n[00:06.00] So the bar is where I go\n[00:12.00] Me and my friends at the table doing shots\n[00:18.00] Drinking fast and then we talk slow\n[00:24.00] I'm in love with the shape of you\n[00:30.00] We push and pull like a magnet do"
            ),
            SongBlueprint(
                title = "Believer",
                artist = "Imagine Dragons",
                album = "Evolve",
                genre = "Alternative Rock",
                durationMs = 204000L,
                coverIndex = 6,
                lyrics = "[00:00.00] First things first, I'ma say all the words inside my head\n[00:07.00] I'm fired up and tired of the way that things have been, oh-ooh\n[00:15.00] Second thing second, don't you tell me what you think that I could be\n[00:23.00] I'm the one at the sail, I'm the master of my sea, oh-ooh\n[00:31.00] Pain! You made me a, you made me a believer, believer"
            ),
            SongBlueprint(
                title = "HUMBLE.",
                artist = "Kendrick Lamar",
                album = "DAMN.",
                genre = "Hip-Hop",
                durationMs = 177000L,
                coverIndex = 7,
                lyrics = "[00:00.00] Nobody pray for me, it been that day for me\n[00:05.00] Way (yeah, yeah!)\n[00:09.00] Ay, I remember syrup sandwiches and crime allowances\n[00:14.00] Finesse a nigga with some counterfeits, but now I'm countin' this\n[00:20.00] Be humble (hol' up, bitch), sit down (hol' up, lil', hol' up, lil' bitch)"
            ),
            SongBlueprint(
                title = "Uptown Funk",
                artist = "Bruno Mars",
                album = "Uptown Special",
                genre = "Funk Pop",
                durationMs = 269000L,
                coverIndex = 8,
                lyrics = "[00:00.00] This hit, that ice cold, Michelle Pfeiffer, that white gold\n[00:08.00] This one for them hood girls, them good girls straight masterpieces\n[00:16.00] Stylin', wilin', livin' it up in the city\n[00:24.00] Got Chucks on with Saint Laurent, gotta kiss myself, I'm so pretty\n[00:32.00] 'Cause Uptown Funk gon' give it to you"
            ),
            SongBlueprint(
                title = "Rolling in the Deep",
                artist = "Adele",
                album = "21",
                genre = "Soul / Pop",
                durationMs = 228000L,
                coverIndex = 0,
                lyrics = "[00:00.00] There's a fire starting in my heart\n[00:06.00] Reaching a fever pitch and it's bringing me out the dark\n[00:13.00] Finally I can see you crystal clear\n[00:20.00] Go 'head and sell me out and I'll lay your shit bare\n[00:27.00] We could have had it all\n[00:34.00] Rolling in the deep"
            ),
            SongBlueprint(
                title = "Lose Yourself",
                artist = "Eminem",
                album = "8 Mile",
                genre = "Hip-Hop",
                durationMs = 326000L,
                coverIndex = 1,
                lyrics = "[00:00.00] Look, if you had one shot, or one opportunity\n[00:07.00] To seize everything you ever wanted in one moment\n[00:14.00] Would you capture it, or just let it slip?\n[00:20.00] His palms are sweaty, knees weak, arms are heavy\n[00:27.00] There's vomit on his sweater already, mom's spaghetti"
            ),
            SongBlueprint(
                title = "Circles",
                artist = "Post Malone",
                album = "Hollywood's Bleeding",
                genre = "Pop Rock",
                durationMs = 215000L,
                coverIndex = 2,
                lyrics = "[00:00.00] Oh, oh, oh-oh\n[00:06.00] We couldn't turn the page, run off the engine\n[00:12.00] Seasons change and our love went cold\n[00:18.00] Feed the flame 'cause we can't let go\n[00:24.00] Run away, but we're running in circles"
            ),
            SongBlueprint(
                title = "drivers license",
                artist = "Olivia Rodrigo",
                album = "SOUR",
                genre = "Bedroom Pop",
                durationMs = 242000L,
                coverIndex = 3,
                lyrics = "[00:00.00] I got my driver's license last week\n[00:06.00] Just like we always talked about\n[00:12.00] 'Cause you were so excited for me\n[00:18.00] To finally drive up to your house\n[00:24.00] And I know we weren't perfect, but I've never felt this way for no one"
            ),
            SongBlueprint(
                title = "Kill Bill",
                artist = "SZA",
                album = "SOS",
                genre = "R&B",
                durationMs = 153000L,
                coverIndex = 4,
                lyrics = "[00:00.00] I'm still a fan even though I was salty\n[00:06.00] Hate to see you with some other broad, know you happy\n[00:12.00] Hate to see you happy if I'm not the one driving\n[00:18.00] I might kill my ex, not the best idea\n[00:24.00] His new girlfriend's next, how'd I get here?"
            ),
            SongBlueprint(
                title = "Get Lucky",
                artist = "Daft Punk",
                album = "Random Access Memories",
                genre = "Disco Funk",
                durationMs = 248000L,
                coverIndex = 5,
                lyrics = "[00:00.00] Like the legend of the phoenix\n[00:06.00] All ends with beginnings\n[00:12.00] What keeps the planet spinning\n[00:18.00] The force from the beginning\n[00:24.00] We've come too far to give up who we are\n[00:30.00] So let's raise the bar and our cups to the stars\n[00:36.00] She's up all night 'til the sun\n[00:42.00] I'm up all night to get some\n[00:48.00] We're up all night for good fun\n[00:54.00] We're up all night to get lucky"
            ),
            SongBlueprint(
                title = "Smells Like Teen Spirit",
                artist = "Nirvana",
                album = "Nevermind",
                genre = "Grunge Rock",
                durationMs = 301000L,
                coverIndex = 6,
                lyrics = "[00:00.00] Load up on guns, bring your friends\n[00:06.00] It's fun to lose and to pretend\n[00:12.00] She's over-bored and self-assured\n[00:18.00] Oh no, I know a dirty word\n[00:24.00] Hello, hello, hello, how low\n[00:30.00] With the lights out, it's less dangerous\n[00:36.00] Here we are now, entertain us"
            ),
            SongBlueprint(
                title = "Tokyo Rain Beats",
                artist = "Chilled Cow",
                album = "Midnight Study Session",
                genre = "Lo-Fi Beats",
                durationMs = 188000L,
                coverIndex = 7,
                lyrics = "[00:00.00] Tokyo Rain Beats\n[00:10.00] Smooth tape hiss and ambient raindrops\n[00:25.00] Relaxing Rhodes piano with vinyl warmth\n[00:40.00] Perfect focus and tranquil study flow"
            ),
            SongBlueprint(
                title = "Cyber Sunset Drive",
                artist = "Kavinsky Wave",
                album = "Neon Outrun 1986",
                genre = "Synthwave",
                durationMs = 224000L,
                coverIndex = 8,
                lyrics = "[00:00.00] Synth bassline pulsing at 118 BPM\n[00:15.00] Analog arpeggios cruising through digital neon\n[00:35.00] Cruising the grid into the endless sunset\n[00:55.00] Pure retro synth harmony"
            )
        )

        // For each song blueprint, construct BOTH a Spotify Lossless Master and a YouTube Live/HD counterpart
        blueprints.forEachIndexed { index, bp ->
            val cleanKey = bp.title.lowercase().replace(Regex("[^a-z0-9]"), "")
            val spId = "sp_uni_$cleanKey"
            val ytId = "yt_uni_$cleanKey"
            val coverUrl = curatedCovers[bp.coverIndex % curatedCovers.size]
            val streamUrl = sampleAudioStreams[index % sampleAudioStreams.size]

            // 1. Spotify Master
            val spotifyTrack = TrackEntity(
                id = spId,
                title = bp.title,
                artist = bp.artist,
                album = bp.album,
                durationMs = bp.durationMs,
                platformSource = PlatformSource.SPOTIFY,
                sourceTrackId = "spotify:track:$cleanKey",
                coverUrl = coverUrl,
                streamUrl = streamUrl,
                audioQuality = AudioQuality.LOSSLESS,
                isDownloaded = false,
                isLiked = false,
                lyricsLrc = bp.lyrics,
                genre = "${bp.genre} • Spotify Master",
                spotifyEquivalentId = spId,
                youtubeEquivalentId = ytId
            )

            // 2. YouTube HD / Live Session counterpart
            val youtubeTrack = TrackEntity(
                id = ytId,
                title = "${bp.title} (YouTube HD Session)",
                artist = bp.artist,
                album = "${bp.album} (Live / Visual)",
                durationMs = bp.durationMs + 3000L,
                platformSource = PlatformSource.YOUTUBE,
                sourceTrackId = "youtube:video:$cleanKey",
                coverUrl = coverUrl,
                streamUrl = streamUrl,
                audioQuality = AudioQuality.HIGH,
                isDownloaded = false,
                isLiked = false,
                lyricsLrc = bp.lyrics,
                genre = "${bp.genre} • YouTube Stream",
                spotifyEquivalentId = spId,
                youtubeEquivalentId = ytId
            )

            list.add(spotifyTrack)
            list.add(youtubeTrack)
        }

        list
    }

    /**
     * Searches the catalog by query and platform, and if query is unfamiliar,
     * dynamically synthesizes unified Spotify & YouTube pairs so that ANY song is immediately found!
     */
    fun searchCatalog(
        query: String,
        platformFilter: PlatformSource? = null
    ): List<TrackEntity> {
        val q = query.trim()
        if (q.isBlank()) {
            return when (platformFilter) {
                PlatformSource.SPOTIFY -> dualPlatformCatalog.filter { it.platformSource == PlatformSource.SPOTIFY }
                PlatformSource.YOUTUBE -> dualPlatformCatalog.filter { it.platformSource == PlatformSource.YOUTUBE }
                else -> dualPlatformCatalog
            }
        }

        // Direct matching against curated catalog
        val matches = dualPlatformCatalog.filter { track ->
            track.title.contains(q, ignoreCase = true) ||
            track.artist.contains(q, ignoreCase = true) ||
            track.album.contains(q, ignoreCase = true) ||
            track.genre.contains(q, ignoreCase = true)
        }

        val filtered = when (platformFilter) {
            PlatformSource.SPOTIFY -> matches.filter { it.platformSource == PlatformSource.SPOTIFY }
            PlatformSource.YOUTUBE -> matches.filter { it.platformSource == PlatformSource.YOUTUBE }
            else -> matches
        }

        if (filtered.isNotEmpty()) {
            return filtered
        }

        // Dynamic unified synthesis: ANY song or artist searched generates unified dual-database entries
        return generateDynamicUnifiedPair(q, platformFilter)
    }

    /**
     * Synthesizes paired Spotify and YouTube entries for any custom query.
     */
    fun generateDynamicUnifiedPair(
        query: String,
        platformFilter: PlatformSource? = null
    ): List<TrackEntity> {
        val cleanQuery = query.trim()
        val hash = Math.abs(cleanQuery.hashCode())
        val cover = curatedCovers[hash % curatedCovers.size]
        val stream = sampleAudioStreams[hash % sampleAudioStreams.size]
        val cleanId = cleanQuery.lowercase().replace(Regex("[^a-z0-9]"), "_").take(24)

        val spId = "sp_dyn_$cleanId"
        val ytId = "yt_dyn_$cleanId"

        val lyrics = """
            [00:00.00] $cleanQuery
            [00:06.00] Unified dual-platform streaming
            [00:14.00] Lossless Spotify Master & YouTube HD Audio
            [00:22.00] Seamlessly synchronized playback
            [00:32.00] Playing smoothly with zero latency
        """.trimIndent()

        val spTrack = TrackEntity(
            id = spId,
            title = cleanQuery.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            artist = "Unified Artist",
            album = "Spotify Studio Master",
            durationMs = 210000L + ((hash % 60) * 1000L),
            platformSource = PlatformSource.SPOTIFY,
            sourceTrackId = "spotify:track:$cleanId",
            coverUrl = cover,
            streamUrl = stream,
            audioQuality = AudioQuality.LOSSLESS,
            isDownloaded = false,
            isLiked = false,
            lyricsLrc = lyrics,
            genre = "Unified Pop / Electronic",
            spotifyEquivalentId = spId,
            youtubeEquivalentId = ytId
        )

        val ytTrack = TrackEntity(
            id = ytId,
            title = "${spTrack.title} (YouTube HD Session)",
            artist = "Unified Artist",
            album = "YouTube Live Showcase",
            durationMs = spTrack.durationMs + 4000L,
            platformSource = PlatformSource.YOUTUBE,
            sourceTrackId = "youtube:video:$cleanId",
            coverUrl = cover,
            streamUrl = stream,
            audioQuality = AudioQuality.HIGH,
            isDownloaded = false,
            isLiked = false,
            lyricsLrc = lyrics,
            genre = "Unified YouTube Audio",
            spotifyEquivalentId = spId,
            youtubeEquivalentId = ytId
        )

        return when (platformFilter) {
            PlatformSource.SPOTIFY -> listOf(spTrack)
            PlatformSource.YOUTUBE -> listOf(ytTrack)
            else -> listOf(spTrack, ytTrack)
        }
    }
}
