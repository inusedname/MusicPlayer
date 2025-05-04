package dev.keego.musicplayer.remote

import dev.keego.musicplayer.local.LocalLyricDao
import dev.keego.musicplayer.local.Lyric
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.remote.lrclib.LrcLibLyricDao
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import timber.log.Timber

class LyricRepository(
    private val remoteDao: LrcLibLyricDao,
    private val localDao: LocalLyricDao,
) {
    suspend fun getBestMatch(song: Song): Response<Lyric> {
        val local: Lyric? = localDao.get(song.id)
        if (local != null) {
            return Response.success(local)
        }
        val response = remoteDao.getBestMatch(
            song.title,
            song.artist,
            song.album.ifEmpty { song.title },
            (song.duration / 1000).toInt()
        )
        if (response.isSuccessful) {
            val body = response.body()!!
            val remote = Lyric(
                id = song.id,
                lrcLibId = body.id,
                query = Lyric.getQuery(song),
                lrcContent = body.syncedLyrics
            )
            localDao.save(remote)
            return Response.success(remote)
        } else {
            Timber.e(response.errorBody()?.string() ?: "null")
            return Response.error(
                response.code(),
                response.errorBody() ?: "undefined".toResponseBody(null)
            )
        }
    }

    suspend fun search(song: Song): Response<List<BestMatchResultPOJO>> {
        return remoteDao.search("${song.title} ${song.artist}")
    }
}