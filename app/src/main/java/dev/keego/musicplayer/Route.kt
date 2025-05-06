package dev.keego.musicplayer

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    object Home : Route()

    @Serializable
    object Search : Route()

    @Serializable
    object Library : Route()
}