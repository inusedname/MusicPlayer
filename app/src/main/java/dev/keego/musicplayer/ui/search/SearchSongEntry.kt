package dev.keego.musicplayer.ui.search

import dev.keego.musicplayer.remote.freemp3download.SearchSongPOJO

data class SearchSongEntry(
    val deezerId: String,
    val title: String,
    val artist: String,
    val cover: String,
    val previewMp3Link: String
) {
    companion object {
        fun create(searchSongPOJO: SearchSongPOJO): List<SearchSongEntry> {
            return searchSongPOJO.data.map {
                SearchSongEntry(
                    deezerId = it.id,
                    title = it.title,
                    artist = it.artist.name,
                    cover = it.album.cover_medium,
                    previewMp3Link = it.preview
                )
            }
        }
    }
}