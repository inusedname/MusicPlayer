package dev.keego.musicplayer.remote

import dev.keego.musicplayer.remote.freemp3download.DirectDownloadDao
import dev.keego.musicplayer.remote.freemp3download.SearchSongDao

class OnlineSongRepository(
    private val searchDao: SearchSongDao,
    private val downloadDao: DirectDownloadDao,
) {
    suspend fun search(query: String) = searchDao.search(query)
}