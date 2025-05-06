package dev.keego.musicplayer.local.search_history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Upsert
    suspend fun upsert(searchHistory: SearchHistoryTbl)

    @Query("SELECT * FROM SearchHistoryTbl ORDER BY time DESC LIMIT 5")
    fun getTopFiveAsFlow(): Flow<List<SearchHistoryTbl>>

    @Query("SELECT * FROM SearchHistoryTbl ORDER BY time DESC LIMIT 5")
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