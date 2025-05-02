package dev.keego.musicplayer.ui.search

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.BaseViewModel
import dev.keego.musicplayer.noti.DemoUtil
import dev.keego.musicplayer.noti.DownloadCenter
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection.Base
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class SearchVimel @Inject constructor(
    private val repo: OnlineSongRepository,
    @ApplicationContext private val context: Context,
): BaseViewModel<SearchUiState, Unit>() {
    override fun initialState(): SearchUiState = SearchUiState.IDLE

    private val downloadCenter: WeakReference<DownloadCenter> =
        WeakReference(DemoUtil.getDownloadCenter(context))

    fun query(query: String) {
        viewModelScope.launch {
            setState(SearchUiState.LOADING)
            repo.search(query)
                .onSuccess {
                    setState(SearchUiState.SUCCESS(it.map { SearchEntry(it) }))
                }.onFailure {
                    setState(SearchUiState.ERROR(it))
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