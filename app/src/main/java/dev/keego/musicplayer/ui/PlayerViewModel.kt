package dev.keego.musicplayer.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.local.playlist.PlaylistRepository
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.ui.search.Provider
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val onlineSongRepository: OnlineSongRepository,
    private val playlistRepository: PlaylistRepository,
): BaseViewModel<Unit, PlayerVMEvent>() {
    override fun initialState() = Unit

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

    fun publishError(error: Throwable) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayerError(error))
        }
    }
}

sealed class PlayerVMEvent {
    data class PlayImmediate(val searchEntry: SearchEntry?, val song: Song?) : PlayerVMEvent()
    data class PlayNext(val streamInfoItem: StreamInfoItem) : PlayerVMEvent()
    data class PlayerError(val error: Throwable): PlayerVMEvent()
}