package dev.keego.musicplayer.ui.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.local.search_history.SearchHistoryRepository
import dev.keego.musicplayer.local.search_history.SearchHistoryTbl
import dev.keego.musicplayer.noti.DemoUtil
import dev.keego.musicplayer.noti.DownloadCenter
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class SearchVimel @Inject constructor(
    private val repo: OnlineSongRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<SearchUiState, Unit>() {
    override fun initialState(): SearchUiState = SearchUiState()

    private val downloadCenter: WeakReference<DownloadCenter> =
        WeakReference(DemoUtil.getDownloadCenter(context))

    init {
        viewModelScope.launch {
            searchHistoryRepository.getTopFiveSearchHistory().collect { histories ->
                if (histories.isNotEmpty()) {
                    setState { it.copy(searchHistories = histories.map { it.keyword }) }
                }
            }
        }
    }

    fun getSuggestions(query: String) {
        viewModelScope.launch {
            setState { it.copy(searchState = UiState.Loading()) }
            repo.getSuggestions(query)
                .onSuccess { suggestions ->
                    setState { it.copy(suggestions = suggestions) }
                }.onFailure { throwable ->
                    Timber.e(throwable)
                }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            setState { it.copy(searchState = UiState.Loading()) }
            searchHistoryRepository.upsertSearchHistory(SearchHistoryTbl(keyword = query))
            repo.search(query)
                .onSuccess { items ->
                    setState {
                        it.copy(searchState = UiState.Success(items.map {
                            SearchEntry.fromInfoItem(
                                it
                            )
                        }))
                    }
                }.onFailure { throwable ->
                    Timber.e(throwable)
                    setState { it.copy(searchState = UiState.Error(throwable)) }
                }
        }
    }

//    private val renderersFactory = DemoUtil.buildRenderersFactory(context, false)

//    fun postDownload(fragmentManager: FragmentManager, deezerId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = repo.directDownload(deezerId)
//            when (response.isSuccessful) {
//                true -> {
//                    val url = response.body()!!
//                    Timber.d(response.body()!!)
//                    val mediaItem = MediaItem.fromUri(url)
//                    withContext(Dispatchers.Main) {
//                        downloadCenter.get()?.toggleDownload(
//                            fragmentManager, mediaItem, renderersFactory
//                        )
//                    }
//                }
//
//                false -> {
//                    android.os.Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//    }
}