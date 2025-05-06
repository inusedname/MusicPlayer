package dev.keego.musicplayer.local.search_history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryTbl(
    @PrimaryKey
    val keyword: String,

    val time: Long = System.currentTimeMillis(),
)