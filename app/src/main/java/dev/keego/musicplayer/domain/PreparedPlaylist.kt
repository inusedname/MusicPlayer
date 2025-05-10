package dev.keego.musicplayer.domain

import dev.keego.musicplayer.local.playlist.PlaylistWithTracksTbl
import dev.keego.musicplayer.local.playlist.TrackTbl
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.model.toSong
import dev.keego.musicplayer.ui.search.Provider
import kotlinx.serialization.Serializable

@Serializable
data class PreparedPlaylist(
    val id: Long,
    val title: String,
    val coverUri: String,
    val tracks: List<Song>,
    val provider: Provider
) {
    companion object {
        fun fromPlaylistWithTrackTbl(playlistWithTracksTbl: PlaylistWithTracksTbl): PreparedPlaylist {
            return PreparedPlaylist(
                id = playlistWithTracksTbl.playlistTbl.id,
                title = playlistWithTracksTbl.playlistTbl.name,
                coverUri = "",
                tracks = playlistWithTracksTbl.tracks.map(TrackTbl::toSong),
                provider = playlistWithTracksTbl.playlistTbl.provider
            )
        }
    }
}