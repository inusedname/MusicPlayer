package dev.keego.musicplayer.remote

import dev.keego.musicplayer.local.lyric.LocalLyricDao
import dev.keego.musicplayer.local.lyric.LyricTbl
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.remote.lrclib.LrcLibLyricDao
import retrofit2.Response
import timber.log.Timber

class LyricRepository(
    private val remoteDao: LrcLibLyricDao,
    private val localDao: LocalLyricDao,
) {
    suspend fun getBestMatch(song: Song): Result<LyricTbl> {
        val local: LyricTbl? = localDao.get(song.id)
        if (local != null) {
            return Result.success(local)
        }
        try {
            val response = remoteDao.getBestMatch(
                song.title,
                song.artist,
                song.album.ifEmpty { song.title },
                song.duration.toInt()
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                val remote = LyricTbl(
                    id = song.id,
                    lrcLibId = body.id,
                    query = LyricTbl.getQuery(song),
                    lrcContent = body.syncedLyrics
                )
                localDao.save(remote)
                return Result.success(remote)
            } else {
                return Result.failure(Exception("Error: ${response.code()} ${response.errorBody()?.string() ?: ""}"))
            }
        } catch (t: Throwable) {
            Timber.e(t)
            return Result.failure(t)
        }
    }

    suspend fun search(song: Song): Response<List<BestMatchResultPOJO>> {
        return remoteDao.search("${song.title} ${song.artist}")
    }
}