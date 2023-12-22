package dev.keego.musicplayer.remote

import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.remote.lrclib.LrcLibLyricDao
import retrofit2.Response
import timber.log.Timber

class LyricRepository(private val dao: LrcLibLyricDao) {
    suspend fun getBestMatch(song: Song): Response<BestMatchResultPOJO> {
        Timber.d("""
            ${song.title}
            ${song.artist}
            ${song.duration / 1000}
        """.trimIndent())
        // TODO: missing album handling
        return dao.getBestMatch(song.title, song.artist, song.title, (song.duration / 1000L).toInt())
    }

    suspend fun search(song: Song): Response<List<BestMatchResultPOJO>> {
        return dao.search("${song.title} ${song.artist}")
    }
}