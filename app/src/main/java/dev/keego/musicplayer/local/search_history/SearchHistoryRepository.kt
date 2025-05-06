package dev.keego.musicplayer.local.search_history

import dev.keego.musicplayer.local.search_history.SearchHistoryDao
import dev.keego.musicplayer.local.search_history.SearchHistoryTbl

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun insertSearchHistory(searchHistory: SearchHistoryTbl) {
        searchHistoryDao.insert(searchHistory)
    }

    suspend fun getTopFiveSearchHistory(): List<SearchHistoryTbl> {
        return searchHistoryDao.getTopFive()
    }

    suspend fun cleanUpSearchHistory() {
        searchHistoryDao.cleanUp()
    }
}