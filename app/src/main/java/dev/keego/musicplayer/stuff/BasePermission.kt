package dev.keego.voice.changer.setup.center

import android.app.Activity
import android.app.AlertDialog
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import timber.log.Timber

abstract class BasePermission : ActivityLifecycleCallbacks {
    private var requestPermissionLaunchers = HashMap<String, ActivityResultLauncher<Array<String>>>()
    private var onGranted: ((Context) -> Unit) = {}
    private var onDenied: ((Context) -> Unit) = {}
    protected abstract var permissions: Array<String>
    open var onShowRationale: ((Context) -> Unit)? = null
    open val isShowDefaultDeny = false
    open val defaultDenyDialog: ((Context) -> Unit)? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.i("onActivityCreated")
        if (activity !is ComponentActivity) return

        try {
            requestPermissionLaunchers[activity::class.java.simpleName] =
                activity.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions(),
                ) { isGranted ->
                    if (isGranted.all { it.value }) {
                        onGranted.invoke(activity)
                    } else {
                        if (isShowDefaultDeny) {
                            defaultDenyDialog?.invoke(activity)
                        }
                        onDenied.invoke(activity)
                    }
                }
        } catch (e: Exception) {
//            e.printStackTrace()
            Timber.w(activity::class.java.simpleName)
//            Firebase.crashlytics.recordException(e)
        }
    }

    private fun isGranted(permission: String, context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isGranted(context: Context): Boolean {
        for (permission in permissions) {
            if (!isGranted(
                    permission,
                    context,
                )
            ) {
                return false
            }
        }
        return true
    }

    private fun removeGrantedPermission(context: Context, vararg permissions: String) {
        val permissionNotGranted = mutableListOf<String>()
        for (permission in permissions) {
            if (!isGranted(
                    permission,
                    context,
                )
            ) {
                permissionNotGranted.add(permission)
            }
        }
        this.permissions = permissionNotGranted.toTypedArray()
    }

    fun onGranted(onGranted: (Context) -> Unit): BasePermission {
        this.onGranted = onGranted
        return this
    }

    fun onDenied(onDenied: (Context) -> Unit): BasePermission {
        this.onDenied = onDenied
        return this
    }

    fun setDenied(title: String, message: String): ((Context) -> Unit) {
        return {
            showAlertDialog(
                context = it,
                title = title,
                message = message,
                positiveButtonContent = "Go to settings",
                negativeButtonContent = "Deny",
                onAllow = {
                    goToSetting(it)
                },
                onDenied = {
                    Toast.makeText(
                        it,
                        message,
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }

    fun setShowRationale(title: String, message: String): ((Context) -> Unit) {
        return {
            showAlertDialog(
                context = it,
                title = title,
                message = message,
                positiveButtonContent = "Allow",
                negativeButtonContent = "Deny",
                onAllow = {
                    launch(it as Activity)
                },
                onDenied = {
                    onDenied.invoke(it)
                },
            )
        }
    }

    private fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonContent: String,
        negativeButtonContent: String,
        onAllow: () -> Unit = {},
        onDenied: () -> Unit = {},
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonContent) { dialog, _ ->
                dialog.dismiss()
                onAllow()
            }.setNegativeButton(negativeButtonContent) { dialog, _ ->
                dialog.dismiss()
                onDenied()
            }.create().show()
    }

    private fun goToSetting(it: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(
            "package",
            it.packageName,
            null,
        )
        intent.data = uri
        it.startActivity(intent)
    }

    open fun request(activity: Activity) {
        val activityName = activity::class.java.simpleName
        Timber.tag("PermissionCenter").i("request: $activityName")
        requestPermissionLaunchers.forEach {
            Timber.tag("PermissionCenter").i("request: ${it.key}")
        }
        if (requestPermissionLaunchers[activityName] == null) {
            return
        }
        removeGrantedPermission(
            activity,
            *permissions,
        )

        if (permissions.isEmpty()) {
            Timber.tag("PermissionCenter").i("request: all permission granted")
            onGranted.invoke(activity)
        } else {
            var isShowRationale = false
            for (permission in permissions) {
                if (shouldShowRequestPermissionRationale(
                        activity,
                        permission,
                    )
                ) {
                    isShowRationale = true
                    break
                }
            }
            if (isShowRationale && onShowRationale != null) {
                onShowRationale?.invoke(activity)
            } else {
                launch(activity)
            }
        }
    }

    private fun launch(activity: Activity) {
        requestPermissionLaunchers[activity::class.java.simpleName]?.launch(permissions)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}