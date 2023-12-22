package dev.keego.musicplayer.ui.lyric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.LyricRepository
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BrowseLyricVimel @Inject constructor(private val lyricRepository: LyricRepository) :
    ViewModel() {
    val uiState = MutableStateFlow<UiState>(UiState.LOADING)
    val results = MutableStateFlow<List<BestMatchResultPOJO>>(emptyList())

    fun fetch(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = lyricRepository.search(song)
            if (!response.isSuccessful) {
                Timber.e("fetch: ${response.errorBody()}")
                uiState.value = UiState.ERROR(response.errorBody().toString())
                return@launch
            }
            val body = response.body()!!
            results.value = body
            uiState.value = UiState.SUCCESS
        }
    }
}