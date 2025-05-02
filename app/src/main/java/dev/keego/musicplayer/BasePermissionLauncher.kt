package dev.keego.musicplayer

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun <PARAM: Any> basePermissionLauncher(
    permissions: List<String>,
    onSuccess: (PARAM) -> Unit,
    getTryAgainSettingIntent: () -> Intent,
): Launcher<PARAM> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var _param: PARAM? = remember { null }
    var previouslyLaunchSetting = remember { false }

    fun handlePermissionDenied(context: Context) {
        previouslyLaunchSetting = true
        val intent = getTryAgainSettingIntent()
        context.startActivity(intent)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.all { it.value }) {
            onSuccess(_param!!)
            _param = null
        } else {
            // TODO: I could show a explaining dialog here
            handlePermissionDenied(context)
        }
    }

    fun launch(param: PARAM) {
        _param = param
        launcher.launch(permissions.toTypedArray())
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START && previouslyLaunchSetting) {
                previouslyLaunchSetting = false
                launch(_param!!)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return object : Launcher<PARAM> {
        override fun launch(param: PARAM) {
            launch(param)
        }
    }
}

interface Launcher<T: Any> {
    fun launch(param: T)
}