package dev.keego.musicplayer

import android.app.Application
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, message: String?, vararg args: Any?) {
                super.log(priority, ">> $message", *args)
            }
        })
    }
}