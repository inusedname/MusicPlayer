package dev.keego.musicplayer

import dev.keego.musicplayer.domain.PreparedPlaylist
import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    object Home : Route()

    @Serializable
    object Search : Route()

    @Serializable
    object MySpace : Route()

    @Serializable
    object Setting : Route()

    @Serializable
    data class Playlist(val preparedPlaylist: PreparedPlaylist) : Route()
}