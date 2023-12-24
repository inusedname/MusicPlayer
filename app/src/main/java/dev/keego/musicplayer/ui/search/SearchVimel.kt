package dev.keego.musicplayer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keego.musicplayer.remote.OnlineSongRepository
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchVimel @Inject constructor(private val repo: OnlineSongRepository): ViewModel() {
    val uiState = MutableStateFlow<SearchUiState>(SearchUiState.IDLE)
    val results = MutableStateFlow<List<SearchSongEntry>>(emptyList())

     fun query(query: String) {
         viewModelScope.launch {
            uiState.value = SearchUiState.LOADING
            val response = repo.search(query)
            if (response.isSuccessful) {
                uiState.value = SearchUiState.SUCCESS
                results.value = SearchSongEntry.create(response.body()!!)
            } else {
                uiState.value = SearchUiState.ERROR(response.message())
            }
         }
     }

}