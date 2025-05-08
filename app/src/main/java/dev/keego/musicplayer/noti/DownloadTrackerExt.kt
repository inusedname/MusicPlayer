package dev.keego.musicplayer.noti

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import dev.keego.musicplayer.model.Song

fun DownloadTracker.toggleDownload(
    context: Context,
    fragmentManager: FragmentManager,
    mediaItem: MediaItem,
) {
    toggleDownload(
        fragmentManager,
        mediaItem,
        DemoUtil.buildRenderersFactory(context, false)
    )
}