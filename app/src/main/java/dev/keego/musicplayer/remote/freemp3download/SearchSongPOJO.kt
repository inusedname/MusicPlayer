package dev.keego.musicplayer.remote.freemp3download

data class SearchSongPOJO(
    val data: List<Data>,
) {
    data class Data(
        val id: String,
        val title: String,
        val preview: String,
        val artist: Artist,
        val album: Album,
    ) {
        data class Artist(
            val name: String,
        )

        data class Album(
            val cover_medium: String,
        )
    }
}