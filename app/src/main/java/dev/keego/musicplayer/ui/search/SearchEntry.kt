package dev.keego.musicplayer.ui.search

import org.schabi.newpipe.extractor.InfoItem

data class SearchEntry(
    val detail: InfoItem,
    val provider: Provider = Provider.YOUTUBE_MUSIC,
)

enum class Provider {
    YOUTUBE_MUSIC,
}