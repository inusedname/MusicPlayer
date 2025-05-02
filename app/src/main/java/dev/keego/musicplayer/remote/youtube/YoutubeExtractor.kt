package dev.keego.musicplayer.remote.youtube

import android.net.Uri
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.Stream
import org.schabi.newpipe.extractor.stream.VideoStream
import timber.log.Timber
import androidx.core.net.toUri
import dev.keego.musicplayer.remote.Streamable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory


class YoutubeExtractor {
    val service = YoutubeService(1)

    suspend fun getSearchSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        val suggestionExtractor = service.suggestionExtractor
        suggestionExtractor.suggestionList(query)
    }

    suspend fun search(query: String): Result<List<InfoItem>> = withContext(Dispatchers.IO) {
        val searchExtractor = service.getSearchExtractor(query, listOf(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS), "")
        runCatching {
            searchExtractor.fetchPage()
            val items = searchExtractor.initialPage.items
            items
        }
    }

    fun getYoutubeStream(url: String): Streamable {
        val streams = service.getStreamExtractor(url).audioStreams.toList()
        Timber.d(streams.joinToString { "it.url=${it.isUrl}, ${it.content}\n" })
        return streams.find { it.isUrl }
            ?.let {
                val mimeType = getMimeType(it)
                object : Streamable {
                    override fun getStreamUri(): Uri {
                        return it.content.toUri()
                    }

                    override fun getMimeType(): String {
                        return mimeType ?: ""
                    }
                }
            } ?: throw IllegalArgumentException("None of streams is Url.")
    }

    private fun getMimeType(stream: Stream): String? {
        val deliveryMethod: DeliveryMethod = stream.deliveryMethod

        if (!stream.isUrl || deliveryMethod == DeliveryMethod.TORRENT) {
            Timber.d("selected_stream_external_player_not_supported")
            return null
        }

        val mimeType = when (deliveryMethod) {
            DeliveryMethod.PROGRESSIVE_HTTP -> if (stream.format == null) {
                if (stream is AudioStream) {
                    "audio/*"
                } else if (stream is VideoStream) {
                    "video/*"
                } else {
                    // This should never be reached, because subtitles are not opened in
                    // external players
                    return null
                }
            } else {
                stream.format?.getMimeType()
            }

            DeliveryMethod.HLS -> "application/x-mpegURL"
            DeliveryMethod.DASH -> "application/dash+xml"
            DeliveryMethod.SS -> "application/vnd.ms-sstr+xml"
            else ->                 // Torrent streams are not exposed to external players
                ""
        }
        return mimeType
    }
}