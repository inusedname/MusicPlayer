package dev.keego.musicplayer.model

import dev.keego.musicplayer.local.playlist.TrackTbl
import dev.keego.musicplayer.stuff.millisToHHmmSS

fun TrackTbl.toSong(): Song {
    return Song(
        id = id,
        album = "",
        title = name,
        duration = durationMillis,
        artist = artist,
        thumbnailUri = thumbnail,
        data = content
    )
}