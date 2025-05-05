package dev.keego.musicplayer.ui

object MockData {
    // Current track
    val currentTrack = Track(
        id = "1",
        title = "Blinding Lights",
        artist = "The Weeknd",
        album = "After Hours",
        cover = "https://picsum.photos/300/300",
        duration = 203 // 3:23 in seconds
    )
    
    // Trending tracks
    val trendingTracks = listOf(
        Track(
            id = "1",
            title = "As It Was",
            artist = "Harry Styles",
            album = "Harry's House",
            cover = "https://picsum.photos/200/200?random=1",
            duration = 167
        ),
        Track(
            id = "2",
            title = "Unholy",
            artist = "Sam Smith & Kim Petras",
            album = "Gloria",
            cover = "https://picsum.photos/200/200?random=2",
            duration = 156
        ),
        Track(
            id = "3",
            title = "Anti-Hero",
            artist = "Taylor Swift",
            album = "Midnights",
            cover = "https://picsum.photos/200/200?random=3",
            duration = 200
        ),
        Track(
            id = "4",
            title = "Calm Down",
            artist = "Rema & Selena Gomez",
            album = "Rave & Roses",
            cover = "https://picsum.photos/200/200?random=4",
            duration = 239
        )
    )
    
    // Suggested playlists
    val suggestedPlaylists = listOf(
        Playlist(
            id = "1",
            title = "Today's Top Hits",
            tracks = 50,
            cover = "https://picsum.photos/200/200?random=5"
        ),
        Playlist(
            id = "2",
            title = "Chill Vibes",
            tracks = 42,
            cover = "https://picsum.photos/200/200?random=6"
        ),
        Playlist(
            id = "3",
            title = "Workout Motivation",
            tracks = 35,
            cover = "https://picsum.photos/200/200?random=7"
        )
    )
    
    // Recently played artists
    val recentlyPlayed = listOf(
        Artist(
            id = "1",
            name = "The Weeknd",
            cover = "https://picsum.photos/200/200?random=8"
        ),
        Artist(
            id = "2",
            name = "Dua Lipa",
            cover = "https://picsum.photos/200/200?random=9"
        ),
        Artist(
            id = "3",
            name = "Billie Eilish",
            cover = "https://picsum.photos/200/200?random=10"
        ),
        Artist(
            id = "4",
            name = "Post Malone",
            cover = "https://picsum.photos/200/200?random=11"
        )
    )
    
    // Downloaded songs
    val downloadedSongs = listOf(
        Track(
            id = "5",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=12",
            duration = 203
        ),
        Track(
            id = "6",
            title = "Save Your Tears",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=13",
            duration = 215
        ),
        Track(
            id = "7",
            title = "Starboy",
            artist = "The Weeknd ft. Daft Punk",
            album = "Starboy",
            cover = "https://picsum.photos/200/200?random=14",
            duration = 230
        )
    )
    
    // User playlists
    val userPlaylists = listOf(
        Playlist(
            id = "4",
            title = "My Favorites",
            tracks = 24,
            cover = "https://picsum.photos/200/200?random=15"
        ),
        Playlist(
            id = "5",
            title = "Workout Mix",
            tracks = 18,
            cover = "https://picsum.photos/200/200?random=16"
        ),
        Playlist(
            id = "6",
            title = "Chill Vibes",
            tracks = 32,
            cover = "https://picsum.photos/200/200?random=17"
        )
    )
    
    // Search history
    val searchHistory = listOf(
        "The Weeknd",
        "Taylor Swift new album",
        "Lo-fi beats to study",
        "Best workout music 2023"
    )
    
    // Search results
    val searchResults = listOf(
        Track(
            id = "8",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=18",
            duration = 203
        ),
        Track(
            id = "9",
            title = "Save Your Tears",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=19",
            duration = 215
        ),
        Track(
            id = "10",
            title = "Starboy",
            artist = "The Weeknd ft. Daft Punk",
            album = "Starboy",
            cover = "https://picsum.photos/200/200?random=20",
            duration = 230
        ),
        Track(
            id = "11",
            title = "The Hills",
            artist = "The Weeknd",
            album = "Beauty Behind the Madness",
            cover = "https://picsum.photos/200/200?random=21",
            duration = 242
        ),
        Track(
            id = "12",
            title = "After Hours",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=22",
            duration = 361
        ),
        Track(
            id = "13",
            title = "Heartless",
            artist = "The Weeknd",
            album = "After Hours",
            cover = "https://picsum.photos/200/200?random=23",
            duration = 198
        )
    )
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val cover: String,
    val duration: Int // in seconds
)

data class Playlist(
    val id: String,
    val title: String,
    val tracks: Int,
    val cover: String
)

data class Artist(
    val id: String,
    val name: String,
    val cover: String
)
