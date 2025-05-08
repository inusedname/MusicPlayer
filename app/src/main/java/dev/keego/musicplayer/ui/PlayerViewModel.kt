package dev.keego.musicplayer.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.local.playlist.PlaylistRepository
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.stuff.PlaybackManager
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val onlineSongRepository: OnlineSongRepository,
    private val playlistRepository: PlaylistRepository,
): BaseViewModel<Unit, PlayerVMEvent>() {
    override fun initialState() = Unit

    val playbackManager by lazy {
        PlaybackManager(
            coroutineScope = viewModelScope,
            onlineSongRepository = onlineSongRepository,
            onException = ::publishError,
            onRemoteSongResolved = {},
        )
    }

    fun playImmediate(searchEntry: SearchEntry) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayImmediate(searchEntry, null))
        }
    }

    fun playImmediate(song: Song) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayImmediate(null, song))
        }
    }

    fun playNext(searchEntry: SearchEntry) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayNext(searchEntry, null))
        }
    }

    fun publishError(error: Throwable) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayerError(error))
        }
    }
}

sealed class PlayerVMEvent {
    data class PlayImmediate(val searchEntry: SearchEntry?, val song: Song?) : PlayerVMEvent()
    data class PlayNext(val searchEntry: SearchEntry?, val song: Song?) : PlayerVMEvent()
    data class PlayerError(val error: Throwable): PlayerVMEvent()
}