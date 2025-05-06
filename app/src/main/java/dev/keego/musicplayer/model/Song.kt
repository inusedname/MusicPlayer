package dev.keego.musicplayer.model

import android.net.Uri
import androidx.core.net.toUri
import dev.keego.musicplayer.remote.Streamable

data class Song(
    val id: String,
    val album: String,
    val title: String,
    val duration: Long,
    val artist: String,
    val thumbnailUri: String?,
    val data: String,
): Streamable {
    override fun getMimeType(): String {
        return "audio/mpeg"
    }
    override fun getStreamUri(): Uri {
        return data.toUri()
    }
}