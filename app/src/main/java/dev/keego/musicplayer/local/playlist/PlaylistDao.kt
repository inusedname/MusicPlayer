package dev.keego.musicplayer.local.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.keego.musicplayer.ui.search.Provider

@Dao
interface PlaylistDao {

    @Insert
    suspend fun createPlaylist(playlistTbl: PlaylistTbl)

    @Query("SELECT COUNT(*) FROM Playlist WHERE name = :name")
    suspend fun checkNameExists(name: String): Int

    @Transaction
    @Query("SELECT * FROM Playlist")
    suspend fun getAllPlaylists(): List<PlaylistWithTracksTbl>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE provider = :provider")
    suspend fun getPlaylistsByProvider(provider: Provider): List<PlaylistWithTracksTbl>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistWithTracksTbl?

    @Insert
    suspend fun addTrackToPlaylist(trackTbl: TrackTbl)

    @Delete
    suspend fun removeTrackFromPlaylist(trackTbl: TrackTbl)
}