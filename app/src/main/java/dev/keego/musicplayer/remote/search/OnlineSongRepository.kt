package dev.keego.musicplayer.remote.search

import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.remote.youtube.YoutubeExtractor
import org.schabi.newpipe.extractor.InfoItem

class OnlineSongRepository(private val youtube: YoutubeExtractor) {

    suspend fun search(query: String): Result<List<InfoItem>> {
        return youtube.search(query)
    }

    suspend fun getYoutubeStream(source: InfoItem): Result<Streamable> {
        return youtube.getYoutubeStream(source.url)
    }
}