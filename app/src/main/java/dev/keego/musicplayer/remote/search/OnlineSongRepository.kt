package dev.keego.musicplayer.remote.search

import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.remote.youtube.YoutubeExtractor
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class OnlineSongRepository(private val youtube: YoutubeExtractor) {

    suspend fun search(query: String): Result<List<StreamInfoItem>> {
        return youtube.search(query)
    }

    suspend fun getYoutubeMusicStream(url: String): Result<Song> {
        return youtube.getYoutubeMusicStream(url)
    }

    suspend fun getSuggestions(query: String): Result<List<String>> {
        return runCatching {
            youtube.getSearchSuggestions(query)
        }
    }
}