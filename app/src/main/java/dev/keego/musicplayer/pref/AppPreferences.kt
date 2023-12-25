package dev.keego.musicplayer.pref

import android.annotation.SuppressLint

@SuppressLint("StaticFieldLeak")
internal object AppPreferences: Preferences("pref") {
    var directDownloadToken by stringPref()
}