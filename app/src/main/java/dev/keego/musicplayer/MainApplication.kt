package dev.keego.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.keego.musicplayer.stuff.MediaPermission
import dev.keego.musicplayer.stuff.copySampleAssetsToInternalStorage
import timber.log.Timber

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(MediaPermission)

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, message: String?, vararg args: Any?) {
                super.log(priority, ">> $message", *args)
            }
        })
    }
}