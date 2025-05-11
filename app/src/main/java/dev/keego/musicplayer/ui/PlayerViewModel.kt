package dev.keego.musicplayer.ui

import androidx.lifecycle.viewModelScope
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.launch

class PlayerViewModel : BaseViewModel<Unit, PlayerVMEvent>() {
    override fun initialState() = Unit

    fun playList(preparedPlaylist: PreparedPlaylist) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayList(preparedPlaylist))
        }
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
    data class PlayList(val playlist: PreparedPlaylist) : PlayerVMEvent()
    data class PlayerError(val error: Throwable): PlayerVMEvent()
}