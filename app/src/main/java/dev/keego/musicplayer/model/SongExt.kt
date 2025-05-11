package dev.keego.musicplayer.model

import dev.keego.musicplayer.local.playlist.TrackTbl

fun TrackTbl.toSong(): Song {
    return Song(
        id = id,
        album = "",
        title = name,
        duration = duration,
        artist = artist,
        thumbnailUri = thumbnail,
        data = content
    )
}