package dev.keego.musicplayer.stuff

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.keego.musicplayer.model.Song
import timber.log.Timber

object MediaQuery {
    fun querySongs(context: Context): List<Song> {
        val allMusics = mutableListOf<Song>()
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM
            ),
            null,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                val id = it.get<Int>(MediaStore.Audio.Media._ID)
                val uri = it.get<String>(MediaStore.Audio.Media.DATA)
                val title = it.get<String>(MediaStore.Audio.Media.TITLE)
                val date = it.get<String>(MediaStore.Audio.Media.DATE_ADDED)
                val artist = it.get<String>(MediaStore.Audio.Media.ARTIST)
                val duration = it.get<Long>(MediaStore.Audio.Media.DURATION)
                val albumId = it.get<Long>(MediaStore.Audio.Media.ALBUM_ID)
                val albumTitle = it.get<String>(MediaStore.Audio.Media.ALBUM)

                allMusics.add(
                    Song(
                        mediaStoreId = id,
                        album = albumTitle,
                        title = title,
                        artist = artist,
                        dateAdded = date,
                        duration = duration,
                        data = uri,
                        albumUri = ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()
                    )
                )
            }
        }

        Timber.d("All musics: ${allMusics.size} song(s)")
        if (allMusics.isNotEmpty()) {
            Timber.d("First music: ${allMusics.first()}")
        }
        return allMusics
    }

    private inline fun <reified T> Cursor.get(columnName: String): T {
        return when (T::class) {
            String::class -> getString(getColumnIndexOrThrow(columnName))
            Long::class -> getLong(getColumnIndexOrThrow(columnName))
            Int::class -> getInt(getColumnIndexOrThrow(columnName))
            else -> {}
        } as T
    }
}