package dev.keego.musicplayer.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.ui.search.Provider
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: OnlineSongRepository,
): BaseViewModel<Unit, PlayerVMEvent>() {
    override fun initialState() = Unit

    fun playImmediate(searchEntry: SearchEntry) {
        viewModelScope.launch {
            when (searchEntry.provider) {
                Provider.YOUTUBE_MUSIC -> {
                    repository.getYoutubeMusicStream(searchEntry.url)
                        .onSuccess {
                            playImmediate(it, false)
                        }
                        .onFailure {
                            it.printStackTrace()
                            publishEvent(PlayerVMEvent.PlayerError(it))
                        }
                }
            }
        }
    }

    fun playImmediate(streamable: Streamable, popUpPlayer: Boolean) {
        viewModelScope.launch {
            publishEvent(PlayerVMEvent.PlayImmediate(streamable, popUpPlayer))
        }
    }
}

sealed class PlayerVMEvent {
    data class PlayImmediate(val streamable: Streamable, val popUpPlayer: Boolean) : PlayerVMEvent()
    data class PlayerError(val error: Throwable): PlayerVMEvent()
}