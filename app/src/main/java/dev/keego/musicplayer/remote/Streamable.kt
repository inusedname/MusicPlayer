package dev.keego.musicplayer.remote

import android.net.Uri

interface Streamable {
    fun getMimeType(): String
    fun getStreamUri(): Uri
}
