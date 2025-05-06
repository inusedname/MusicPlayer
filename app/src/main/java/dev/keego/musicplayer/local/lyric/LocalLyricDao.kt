package dev.keego.musicplayer.local.lyric

import androidx.room.*

@Dao
interface LocalLyricDao {

    @Query("SELECT * FROM lyric WHERE id = :id")
    suspend fun get(id: String): LyricTbl?

    @Upsert
    suspend fun save(lyricTbl: LyricTbl)
}