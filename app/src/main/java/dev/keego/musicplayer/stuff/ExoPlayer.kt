package dev.keego.musicplayer.stuff

import androidx.compose.runtime.*
import androidx.media3.common.Player
import kotlinx.coroutines.delay

private const val REFRESH_TIMEOUT = 500L

@Composable
fun Player.playbackAsState(): State<Boolean> {
    val playing = remember { mutableStateOf(false) }
    DisposableEffect(this) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playing.value = isPlaying
            }
        }
        addListener(listener)
        onDispose {
            removeListener(listener)
        }
    }
    return playing
}

@Composable
fun Player.progressAsState(): State<Float> {
    val progress = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(true) {
        while (true) {
            progress.floatValue = currentPosition.toFloat() / duration.toFloat()
            delay(REFRESH_TIMEOUT)
        }
    }
    return progress
}