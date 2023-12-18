package dev.keego.musicplayer.remote

import retrofit2.Response

class LyricRepository(private val dao: LyricDao) {
    suspend fun searchSong(artist: String, title: String): Response<GeniusSearchPOJO> {
        val q = "${artist.replace("[^A-Za-z0-9]", "")} $title"
        return dao.search(q)
    }
}