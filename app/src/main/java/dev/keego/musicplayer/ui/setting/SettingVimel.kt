package dev.keego.musicplayer.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.offline.Download
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.noti.DemoUtil
import dev.keego.musicplayer.noti.DownloadCenter
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Read me: https://developer.android.com/guide/topics/media/exoplayer/downloading-media#playing-downloaded-content
 */
@HiltViewModel
class SettingVimel @Inject constructor(@ApplicationContext context: Context) : ViewModel(), DownloadCenter.Listener {
    private var downloadCenter: WeakReference<DownloadCenter>
    val downloads: MutableStateFlow<List<Download>> = MutableStateFlow(listOf())

    init {
        downloadCenter = WeakReference(DemoUtil.getDownloadCenter(context))
        downloadCenter.get()?.addListener(this)
    }

    override fun onCleared() {
        downloadCenter.get()?.removeListener(this)
    }

    override fun onDownloadsChanged() {
        downloadCenter.get()?.let {
            downloads.value = it.downloads.values.toList()
        }
    }

    fun addToDownload() {
        // TODO
    }
}