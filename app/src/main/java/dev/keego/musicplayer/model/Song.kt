package dev.keego.musicplayer.model

data class Song(
    val mediaStoreId: Int,
    val album: String,
    val title: String,
    val duration: Long,
    val artist: String,
    val dateAdded: String,
    val albumUri: String,
    val data: String,
)