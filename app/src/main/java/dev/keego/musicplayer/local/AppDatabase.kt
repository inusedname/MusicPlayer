package dev.keego.musicplayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.keego.musicplayer.local.lyric.LocalLyricDao
import dev.keego.musicplayer.local.lyric.LyricTbl
import dev.keego.musicplayer.local.playlist.PlaylistDao
import dev.keego.musicplayer.local.playlist.PlaylistTbl
import dev.keego.musicplayer.local.playlist.TrackTbl
import dev.keego.musicplayer.local.search_history.SearchHistoryDao
import dev.keego.musicplayer.local.search_history.SearchHistoryTbl

@Database(
    entities = [
        LyricTbl::class,
        PlaylistTbl::class,
        TrackTbl::class,
        SearchHistoryTbl::class,
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lyricDao(): LocalLyricDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.compileStatement(
                            "INSERT INTO Playlist (name) VALUES ('Favourites', 'Listen History')"
                        ).executeInsert()
                    }
                })
                .build()
        }
    }
}