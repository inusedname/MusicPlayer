package dev.keego.musicplayer.local

import androidx.room.*

@Dao
interface LocalLyricDao {

    @Query("SELECT * FROM lyric WHERE mediaStoreId = :mediaStoreId")
    suspend fun get(mediaStoreId: Int): Lyric?

    @Upsert
    suspend fun save(lyric: Lyric)
}