package dev.keego.musicplayer.stuff

import kotlinx.coroutines.flow.MutableStateFlow

fun <T: Any> MutableStateFlow<T>.updateTo(update: (T) -> T) {
    value = update(value)
}