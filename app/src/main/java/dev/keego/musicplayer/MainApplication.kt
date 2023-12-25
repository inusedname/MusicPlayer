package dev.keego.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.keego.musicplayer.pref.Preferences
import dev.keego.musicplayer.stuff.MediaPermission
import timber.log.Timber

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(MediaPermission)
        Preferences.init(this)

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, message: String?, vararg args: Any?) {
                super.log(priority, ">> $message", *args)
            }
        })
    }
}