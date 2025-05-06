package dev.keego.musicplayer.local

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

class ExoPlayerExceptionHandler(private val onError: (Throwable) -> Unit): Player.Listener {
    override fun onPlayerError(error: PlaybackException) {
        onError(error)
    }
}