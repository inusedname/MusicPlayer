package dev.keego.musicplayer.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keego.musicplayer.noti.DemoUtil
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Read me: https://developer.android.com/guide/topics/media/exoplayer/downloading-media#playing-downloaded-content
 */
@UnstableApi
@HiltViewModel
class SettingVimel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    val downloads: MutableStateFlow<List<Download>> = MutableStateFlow(listOf())
    fun addToDownload() {
        // TODO
    }
}