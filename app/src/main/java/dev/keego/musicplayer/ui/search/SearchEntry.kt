package dev.keego.musicplayer.ui.search

import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem


data class SearchEntry(
    val thumbnailUrl: String?,
    val title: String,
    val artist: String,
    val duration: Int, // in seconds
    val provider: Provider = Provider.YOUTUBE_MUSIC,
    val url: String,
) {
    companion object {
        fun fromInfoItem(item: StreamInfoItem): SearchEntry {
            return SearchEntry(
                thumbnailUrl = item.thumbnails.maxByOrNull { it.estimatedResolutionLevel }?.url,
                title = item.name,
                artist = item.uploaderName,
                duration = item.duration.toInt(),
                provider = Provider.YOUTUBE_MUSIC,
                url = item.url,
            )
        }
    }
}

enum class Provider {
    LOCAL,
    YOUTUBE_MUSIC,
}