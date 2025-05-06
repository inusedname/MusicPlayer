package dev.keego.musicplayer.local.playlist

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithTracksTbl(
    @Embedded val playlistTbl: PlaylistTbl,
    @Relation(parentColumn = "id", entityColumn = "playlistTblId")
    val tracks: List<TrackTbl>,
)