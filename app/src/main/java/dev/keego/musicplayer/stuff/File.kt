package dev.keego.musicplayer.stuff

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


fun Context.copySampleAssetsToInternalStorage() {
    val bufferSize = 1024
    val externalStorage = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "MusicPlayer"
    )
    externalStorage.mkdirs()
    val assetManager = assets
    val assetFiles = assetManager.list("sample/music")

    val internalFiles = externalStorage.list()
    if (internalFiles != null) {
        if (internalFiles.contains(assetFiles?.firstOrNull())) {
            return // Assets already copied
        }
    }

    assetFiles?.forEach {
        assetManager.open("sample/music/" + it).use { input ->
            val file = File(externalStorage, it)
            FileOutputStream(file).use { output ->
                input.copyTo(output, bufferSize)
            }
        }
    }

    MediaScannerConnection.scanFile(
        this, arrayOf(externalStorage.absolutePath), arrayOf("audio/*"),
        null
    )
}