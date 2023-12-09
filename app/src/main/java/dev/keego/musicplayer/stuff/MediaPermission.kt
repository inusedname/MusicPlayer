package dev.keego.musicplayer.stuff

import android.Manifest
import dev.keego.voice.changer.setup.center.BasePermission

object MediaPermission : BasePermission() {
    override var permissions: Array<String> =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
}