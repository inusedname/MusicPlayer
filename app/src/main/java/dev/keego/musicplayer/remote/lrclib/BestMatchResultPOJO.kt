package dev.keego.musicplayer.remote.lrclib

data class BestMatchResultPOJO(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Float,
    val instrument: Boolean,
    val plainLyrics: String,
    val syncedLyrics: String,
)