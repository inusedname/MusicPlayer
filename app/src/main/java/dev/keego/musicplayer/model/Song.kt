package dev.keego.musicplayer.model

import android.net.Uri
import androidx.core.net.toUri
import dev.keego.musicplayer.remote.Streamable
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val album: String,
    val title: String,
    val duration: Int,
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

    companion object {
        fun mock(): Song {
            return Song(
                id = "1",
                album = "",
                title = "",
                duration = 100,
                artist = "",
                thumbnailUri = "",
                data = ""
            )
        }
    }
}