package dev.keego.musicplayer.ui.my_space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.local.playlist.PlaylistRepository
import dev.keego.musicplayer.local.playlist.PlaylistTbl
import dev.keego.musicplayer.local.playlist.PlaylistWithTracksTbl
import dev.keego.musicplayer.local.playlist.TrackTbl
import dev.keego.musicplayer.ui.search.Provider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySpaceViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<PreparedPlaylist>>(emptyList())
    val playlists: StateFlow<List<PreparedPlaylist>> = _playlists.asStateFlow()

    init {
        fetchPlaylists()
    }

    // Fetch playlists from repository or use mock data
    private fun fetchPlaylists() {
        viewModelScope.launch {
            val result = playlistRepository.getAllPlaylists()
            _playlists.value = result
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
