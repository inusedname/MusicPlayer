package dev.keego.musicplayer.local

import androidx.room.*

@Dao
interface LocalLyricDao {

    @Query("SELECT * FROM lyric WHERE id = :id")
    suspend fun get(id: String): Lyric?

    @Upsert
    suspend fun save(lyric: Lyric)
}