package dev.keego.musicplayer.ui.search

import kotlinx.serialization.Serializable

@Serializable
enum class Provider {
    YOUTUBE,
    YOUTUBE_MUSIC,
    SPOTIFY,
    LOCAL
}
