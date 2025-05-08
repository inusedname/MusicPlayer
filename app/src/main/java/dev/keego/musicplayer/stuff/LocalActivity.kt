package dev.keego.musicplayer.stuff

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.platform.LocalContext

val LocalActivity =
    compositionLocalWithComputedDefaultOf<Activity?> { findOwner(
        LocalContext.currentValue) }

private inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}