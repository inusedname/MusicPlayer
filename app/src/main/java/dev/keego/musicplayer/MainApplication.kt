package dev.keego.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.pref.Preferences
import dev.keego.musicplayer.ui.search.Provider
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)

        Timber.plant(object : Timber.DebugTree() {

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                super.log(priority, tag, ">>" + message, t)
            }
        })

        Timber.d(Json.encodeToString(PreparedPlaylist(
            id = 1,
            title = "adwdw",
            coverUri = "adwdwda",
            tracks = listOf(
                Song(
                    id = "1",
                    album = "",
                    title = "",
                    duration = 100,
                    artist = "",
                    thumbnailUri = "",
                    data = ""
                )
            ),
            provider = Provider.LOCAL
        )))
    }
}