package dev.keego.musicplayer.local.playlist

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    suspend fun createPlaylist(playlistTbl: PlaylistTbl) {
        playlistDao.createPlaylist(playlistTbl)
    }

    suspend fun checkNameExists(name: String): Int {
        return playlistDao.checkNameExists(name)
    }

    suspend fun getAllPlaylists(): List<PlaylistWithTracksTbl> {
        return playlistDao.getAllPlaylists()
    }

    suspend fun addTrackToPlaylist(trackTbl: TrackTbl) {
        playlistDao.addTrackToPlaylist(trackTbl)
    }

    suspend fun removeTrackFromPlaylist(trackTbl: TrackTbl) {
        playlistDao.removeTrackFromPlaylist(trackTbl)
    }
}