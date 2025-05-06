package dev.keego.musicplayer.stuff

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.keego.musicplayer.model.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val REFRESH_TIMEOUT = 500L

@Composable
fun Player.playbackAsState(): State<Boolean> {
    val playing = remember { mutableStateOf(isPlaying) }
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
    val owner = LocalLifecycleOwner.current
    val progress = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                progress.floatValue = currentPosition.toFloat() / duration.toFloat()
                delay(REFRESH_TIMEOUT)
            }
        }
    }
    return progress
}

@Composable
fun Player.progressMsAsState(): State<Long> {
    val owner = LocalLifecycleOwner.current
    val progress = remember { mutableLongStateOf(currentPosition) }
    LaunchedEffect(Unit) {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                progress.longValue = currentPosition
                delay(REFRESH_TIMEOUT)
            }
        }
    }
    return progress
}

@Composable
fun Player.currentSongAsState(songResolver: () -> Song?): State<Song?> {
    val owner = LocalLifecycleOwner.current
    val currentTrack = remember { mutableStateOf<Song?>(null) }
//    DisposableEffect(this) {
//        val listener = object : Player.Listener {
//            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
////                currentTrack.value = songResolver()
//            }
//        }
//        addListener(listener)
//        onDispose {
//            removeListener(listener)
//        }
//    }
    return currentTrack
}

@Composable
fun ListenableFuture<out Player>.currentSongAsState(songResolver: () -> Song?): State<Song?> {
    val owner = LocalLifecycleOwner.current
    val currentTrack = remember { mutableStateOf<Song?>(null) }
    Futures.addCallback(this, object : com.google.common.util.concurrent.FutureCallback<Player> {
        override fun onSuccess(result: Player) {
            result.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    currentTrack.value = songResolver()
                }
            })
        }

        override fun onFailure(t: Throwable) {
            // Handle failure
        }
    }, MoreExecutors.directExecutor())
    return currentTrack
}