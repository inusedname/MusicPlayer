package dev.keego.musicplayer.local.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.keego.musicplayer.ui.search.Provider
import org.checkerframework.common.aliasing.qual.Unique

@Entity(tableName = "Playlist", indices = [Index(value = ["name"], unique = true)])
data class PlaylistTbl(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val provider: Provider = Provider.LOCAL,
)