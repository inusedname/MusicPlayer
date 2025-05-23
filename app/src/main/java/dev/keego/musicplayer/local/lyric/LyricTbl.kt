package dev.keego.musicplayer.local.lyric

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.keego.musicplayer.model.Song

@Entity(tableName = "Lyric")
data class LyricTbl(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val lrcLibId: Int,
    val query: String,
    val lrcContent: String,
) {
    companion object {
        fun getQuery(song: Song): String {
            return "${song.artist}_${song.title}"
        }
    }
}