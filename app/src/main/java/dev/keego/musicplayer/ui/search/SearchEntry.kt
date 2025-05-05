package dev.keego.musicplayer.ui.search

import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.InfoItem


data class SearchEntry(
    val thumbnailUrl: String?,
    val title: String,
    val artist: String,
    val duration: Int, // in seconds
    val provider: Provider = Provider.YOUTUBE_MUSIC,
    val url: String,
) {
    companion object {
        fun fromInfoItem(item: InfoItem): SearchEntry {
            return SearchEntry(
                thumbnailUrl = item.thumbnails.maxByOrNull { it.estimatedResolutionLevel }?.url,
                title = item.name,
                artist = "unknown",
                duration = 1,
                provider = Provider.YOUTUBE_MUSIC,
                url = item.url,
            )
        }
    }
}

enum class Provider {
    YOUTUBE_MUSIC,
}