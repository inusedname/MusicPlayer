package dev.keego.musicplayer.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.noti.DemoUtil
import dev.keego.musicplayer.noti.DownloadCenter
import dev.keego.musicplayer.remote.OnlineSongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class SearchVimel @Inject constructor(
    private val repo: OnlineSongRepository,
    @ApplicationContext context: Context,
) : ViewModel() {
    val uiState = MutableStateFlow<SearchUiState>(SearchUiState.IDLE)
    val results = MutableStateFlow<List<SearchSongEntry>>(emptyList())
    private val downloadCenter: WeakReference<DownloadCenter>

    init {
        downloadCenter = WeakReference(DemoUtil.getDownloadCenter(context))
    }

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