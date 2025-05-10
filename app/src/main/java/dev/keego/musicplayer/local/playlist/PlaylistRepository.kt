package dev.keego.musicplayer.local.playlist

import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.ui.search.Provider

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    suspend fun createPlaylist(playlistTbl: PlaylistTbl) {
        playlistDao.createPlaylist(playlistTbl)
    }

    suspend fun checkNameExists(name: String): Int {
        return playlistDao.checkNameExists(name)
    }

    suspend fun getAllPlaylists(): List<PreparedPlaylist> {
        return playlistDao.getAllPlaylists().map(PreparedPlaylist::fromPlaylistWithTrackTbl)
    }
    
    suspend fun getPlaylistsByProvider(provider: Provider): List<PreparedPlaylist> {
        return playlistDao.getPlaylistsByProvider(provider).map(PreparedPlaylist::fromPlaylistWithTrackTbl)
    }
    
    suspend fun getPlaylistById(playlistId: Long): PreparedPlaylist? {
        return playlistDao.getPlaylistById(playlistId)?.let(PreparedPlaylist::fromPlaylistWithTrackTbl)
    }

    suspend fun addTrackToPlaylist(trackTbl: TrackTbl) {
        playlistDao.addTrackToPlaylist(trackTbl)
    }

    suspend fun removeTrackFromPlaylist(trackTbl: TrackTbl) {
        playlistDao.removeTrackFromPlaylist(trackTbl)
    }
}