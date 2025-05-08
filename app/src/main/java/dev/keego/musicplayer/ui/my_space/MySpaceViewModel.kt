package dev.keego.musicplayer.ui.my_space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keego.musicplayer.local.playlist.PlaylistRepository
import dev.keego.musicplayer.local.playlist.PlaylistTbl
import dev.keego.musicplayer.local.playlist.PlaylistWithTracksTbl
import dev.keego.musicplayer.local.playlist.TrackTbl
import dev.keego.musicplayer.ui.search.Provider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MySpaceViewModel(
    private val playlistRepository: PlaylistRepository? = null
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<PlaylistWithTracksTbl>>(emptyList())
    val playlists: StateFlow<List<PlaylistWithTracksTbl>> = _playlists.asStateFlow()

    // Initialize with mock data if repository is null (for previews)
    init {
        if (playlistRepository == null) {
            _playlists.value = createMockPlaylists()
        }
    }

    // Create mock data for previews
    private fun createMockPlaylists(): List<PlaylistWithTracksTbl> {
        val playlist1 = PlaylistTbl(1, "Favorites")
        val playlist2 = PlaylistTbl(2, "Workout Mix")
        val playlist3 = PlaylistTbl(3, "Chill Vibes")

        val tracks1 = listOf(
            TrackTbl(
                id = "track1",
                provider = Provider.SPOTIFY,
                name = "Shape of You",
                artist = "Ed Sheeran",
                content = "https://example.com/track1",
                thumbnail = "https://example.com/thumbnail1.jpg",
                length = "3:54",
                playlistTblId = 1
            ),
            TrackTbl(
                id = "track2",
                provider = Provider.SPOTIFY,
                name = "Blinding Lights",
                artist = "The Weeknd",
                content = "https://example.com/track2",
                thumbnail = "https://example.com/thumbnail2.jpg",
                length = "3:20",
                playlistTblId = 1
            )
        )

        val tracks2 = listOf(
            TrackTbl(
                id = "track3",
                provider = Provider.YOUTUBE,
                name = "Eye of the Tiger",
                artist = "Survivor",
                content = "https://example.com/track3",
                thumbnail = "https://example.com/thumbnail3.jpg",
                length = "4:05",
                playlistTblId = 2
            ),
            TrackTbl(
                id = "track4",
                provider = Provider.YOUTUBE,
                name = "Till I Collapse",
                artist = "Eminem",
                content = "https://example.com/track4",
                thumbnail = "https://example.com/thumbnail4.jpg",
                length = "4:57",
                playlistTblId = 2
            )
        )

        val tracks3 = listOf(
            TrackTbl(
                id = "track5",
                provider = Provider.LOCAL,
                name = "Waves",
                artist = "Chill Lofi",
                content = "https://example.com/track5",
                thumbnail = "https://example.com/thumbnail5.jpg",
                length = "3:30",
                playlistTblId = 3
            )
        )

        return listOf(
            PlaylistWithTracksTbl(playlist1, tracks1),
            PlaylistWithTracksTbl(playlist2, tracks2),
            PlaylistWithTracksTbl(playlist3, tracks3)
        )
    }

    // Fetch playlists from repository or use mock data
    fun fetchPlaylists() {
        viewModelScope.launch {
            _playlists.value = if (playlistRepository != null) {
                try {
                    playlistRepository.getAllPlaylists()
                } catch (e: Exception) {
                    // Fallback to mock data if repository call fails
                    createMockPlaylists()
                }
            } else {
                createMockPlaylists()
            }
        }
    }

    // Create a new playlist
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository?.let { repo ->
                try {
                    // Check if name exists
                    val exists = repo.checkNameExists(name) > 0
                    if (!exists) {
                        val playlist = PlaylistTbl(name = name)
                        repo.createPlaylist(playlist)
                        fetchPlaylists() // Refresh the list
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}
