package dev.keego.musicplayer.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.model.LrcLyric
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.LyricRepository
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LyricViewModel @Inject constructor(
    private val lyricRepository: LyricRepository,
) : ViewModel() {
    val lyricUiState = MutableStateFlow<UiState>(UiState.LOADING)
    val lyric = MutableStateFlow<LrcLyric?>(null)

    fun queryLyric(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            lyricUiState.value = UiState.LOADING
            lyricRepository.getBestMatch(song).onSuccess {
                lyricUiState.value = UiState.SUCCESS
                lyric.value = LrcLyric.fromLyric(it)
            }.onFailure {
                lyricUiState.value = UiState.ERROR(it.message ?: "Unknown error")
            }
        }
    }
}