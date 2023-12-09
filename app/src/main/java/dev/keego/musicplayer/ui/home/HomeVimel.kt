package dev.keego.musicplayer.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.MediaQuery
import dev.keego.musicplayer.stuff.updateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class HomeVimel @Inject constructor(@ApplicationContext private val context: Context): ViewModel() {
    val songs = MutableStateFlow<List<Song>>(emptyList())

    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            val siu = MediaQuery.querySongs(context)
            songs.updateTo {siu }
        }
    }
}