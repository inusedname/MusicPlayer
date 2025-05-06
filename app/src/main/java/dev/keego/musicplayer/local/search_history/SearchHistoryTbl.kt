package dev.keego.musicplayer.local.search_history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryTbl(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
)