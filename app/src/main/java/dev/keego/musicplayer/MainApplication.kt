package dev.keego.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.keego.musicplayer.pref.Preferences
import timber.log.Timber

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, message: String?, vararg args: Any?) {
                super.log(priority, ">> $message", *args)
            }
        })
    }
}