package dev.keego.musicplayer.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.LyricRepository
import dev.keego.musicplayer.stuff.MediaQuery
import dev.keego.musicplayer.stuff.updateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class HomeVimel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lyricRepository: LyricRepository,
) :
    ViewModel() {
    val songs = MutableStateFlow<List<Song>>(emptyList())

    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            val externalStorage =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            MediaScannerConnection.scanFile(
                context, arrayOf(externalStorage.absolutePath), arrayOf("audio/*")
            ) { path, _ ->
                Timber.d("Scanned $path:")
            }
            val siu = MediaQuery.querySongs(context)
            songs.updateTo { siu }
        }
    }
}