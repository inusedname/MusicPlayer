package dev.keego.musicplayer

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun readLocalAudioPermissionLauncher(): Launcher<Unit> {
    val context = LocalContext.current
    return basePermissionLauncher(
        permissions = if (Build.VERSION.SDK_INT <= 32) {
            listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            listOf(android.Manifest.permission.READ_MEDIA_AUDIO)
        },
        onSuccess = {},
        getTryAgainSettingIntent = {
            return@basePermissionLauncher Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setPackage(
                context.packageName
            )
        }
    )
}
