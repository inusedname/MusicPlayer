package dev.keego.musicplayer.remote.youtube

import dev.keego.musicplayer.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.Stream
import org.schabi.newpipe.extractor.stream.StreamExtractor
import org.schabi.newpipe.extractor.stream.VideoStream
import timber.log.Timber


class YoutubeExtractor(okHttpClient: OkHttpClient) {
    private val service = YoutubeService(1)

    init {
        NewPipe.init(DownloaderImpl.init(okHttpClient.newBuilder()))
    }

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

    suspend fun getYoutubeStream(url: String): Result<Song> {
        val streamExtractor = service.getStreamExtractor(url)

        fun StreamExtractor.string(): String {
            return "StreamExtractor(" +
                    "url=$url, " +
                    "name=$name, " +
                    "length=$length, " +
                    "host=$host, " +
                    "subchannelname=$subChannelName, " +
                    "uploaderName=$uploaderName"
        }
        return runCatching {
            withContext(Dispatchers.IO) {
                streamExtractor.fetchPage()
                Timber.d(streamExtractor.string())
            }
            val streams = streamExtractor.audioStreams.toList()
            Timber.d(streams.joinToString { "it.url=${it.isUrl}, ${it.content}\n" })
            streams.find { it.isUrl }
                ?.let {
                    val mimeType = getMimeType(it)
                    Song(
                        id = streamExtractor.url,
                        album = "",
                        title = streamExtractor.name,
                        duration = streamExtractor.length * 1000,
                        artist = streamExtractor.uploaderName.substringBefore(" - Topic"),
                        dateAdded = "",
                        thumbnailUri = streamExtractor.thumbnails.firstOrNull()?.url,
                        data = it.content
                    )
                } ?: throw IllegalArgumentException("None of streams is Url.")
        }
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