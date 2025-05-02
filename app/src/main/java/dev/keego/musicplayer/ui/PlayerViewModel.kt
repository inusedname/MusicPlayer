package dev.keego.musicplayer.ui

import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.ui.search.SearchEntry

class PlayerViewModel: BaseViewModel<Unit, PlayerVMEvent>() {
    override fun initialState() = Unit

    fun playImmediate(streamable: Streamable, popUpPlayer: Boolean) {
        publishEvent(PlayerVMEvent.PlayImmediate(streamable, popUpPlayer))
    }
}

sealed class PlayerVMEvent {
    data class PlayImmediate(val streamable: Streamable, val popUpPlayer: Boolean) : PlayerVMEvent()
}