package dev.keego.musicplayer.local.search_history

import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun upsertSearchHistory(searchHistory: SearchHistoryTbl) {
        searchHistoryDao.upsert(searchHistory)
    }

    fun getTopFiveSearchHistory(): Flow<List<SearchHistoryTbl>> {
        return searchHistoryDao.getTopFiveAsFlow()
    }

    suspend fun cleanUpSearchHistory() {
        searchHistoryDao.cleanUp()
    }
}