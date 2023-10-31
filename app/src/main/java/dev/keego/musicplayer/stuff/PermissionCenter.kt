package dev.keego.musicplayer.stuff

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

class PermissionCenter(_fragment: Fragment) {
    private val fragment = WeakReference(_fragment)
    private val requiredPermissions = mutableListOf<String>()
    private val launcher = fragment.get()?.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onComplete
    )
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<String, Boolean>) -> Unit = {}

    fun permissions(vararg permission: String): PermissionCenter {
        requiredPermissions.addAll(permission)
        return this
    }

    fun onPermissionAllGranted(callback: (Boolean) -> Unit): PermissionCenter {
        this.callback = callback
        return this
    }

    fun onResult(callback: (Map<String, Boolean>) -> Unit): PermissionCenter {
        this.detailedCallback = callback
        return this
    }

    private fun onComplete(result: Map<String, Boolean>) {
        publish(result)
    }

    fun launch() {
        fragment.get()?.let {
            if (areAllPermissionsGranted(requiredPermissions)) {
                publish(requiredPermissions.associateWith { true })
                return
            }
            if (shouldShowPermissionRationale(requiredPermissions)) {
                displayRationale(it, "Permission required")
                return
            }
            request()
        }
    }

    private fun publish(result: Map<String, Boolean>) {
        callback(result.all { it.value })
        detailedCallback(result.mapKeys { it.key })
        result.forEach {
            Log.d(TAG, "${it.key}, granted:${it.value}")
        }
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        callback = {}
        detailedCallback = {}
    }

    private fun request() {
        launcher?.launch(requiredPermissions.toTypedArray())
    }

    private fun displayRationale(fragment: Fragment, msg: String) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
            }
            .show()
    }

    private fun areAllPermissionsGranted(permissions: List<String>): Boolean {
        fragment.get()?.let {
            return permissions.all { permission ->
                granted(it.requireContext(), permission)
            }
        }
        return false
    }

    private fun shouldShowPermissionRationale(permissions: List<String>): Boolean {
        fragment.get()?.let {
            return permissions.any { permission ->
                it.shouldShowRequestPermissionRationale(permission)
            }
        }
        return false
    }

    private fun granted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val TAG = "PermissionCenter"
    }
}