package dev.keego.musicplayer.local.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.keego.musicplayer.ui.search.Provider
import kotlin.time.Duration

@Entity(tableName = "Track", primaryKeys = ["id", "provider"])
data class TrackTbl(
    val id: String,
    val provider: Provider,
    val name: String,
    val artist: String,
    val content: String,
    val thumbnail: String,
    val durationMillis: Long,
    val playlistTblId: Long,
)
