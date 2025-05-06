package dev.keego.musicplayer.local.search_history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(searchHistory: SearchHistoryTbl)

    @Query("SELECT * FROM SearchHistoryTbl ORDER BY id DESC LIMIT 5")
    suspend fun getTopFive(): List<SearchHistoryTbl>

    @Query("DELETE FROM SearchHistoryTbl")
    suspend fun _deleteAll()

    @Insert
    suspend fun _insertAll(searchHistory: List<SearchHistoryTbl>)

    suspend fun cleanUp() {
        val topFive = getTopFive()
        if (topFive.isNotEmpty()) {
            _deleteAll()
            _insertAll(topFive)
        }
    }
}