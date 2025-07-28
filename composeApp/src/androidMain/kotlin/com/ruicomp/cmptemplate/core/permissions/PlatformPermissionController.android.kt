package com.ruicomp.cmptemplate.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus

// A private class to hold the Android-specific implementation details
private class AndroidPermissionController(
    private val permission: String,
    private val context: Context,
    private val launcher: androidx.activity.result.ActivityResultLauncher<String>,
    private val appSettingsLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) : PlatformPermissionController {

    override fun requestPermission() {
        launcher.launch(permission)
    }

    override fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

//        appSettingsLauncher.launch(intent)
    }
}

@Composable
actual fun rememberPlatformPermissionController(
    permission: String,
    onResult: (PermissionStatus) -> Unit
): PlatformPermissionController {
    val context = LocalContext.current
    val activity = context as androidx.activity.ComponentActivity

//    var permissionStatus: PermissionStatus by remember {
//        mutableStateOf(PermissionStatus.NotGranted)
//    }

    val launcherPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            val status = if (isGranted) {
                PermissionStatus.Granted
            } else if (activity.shouldShowRequestPermissionRationale(permission)) {
                PermissionStatus.RationaleNeeded
            } else {
                PermissionStatus.PermanentlyDenied
            }
            Log.d("PermissionController", "Launcher onResult status: $status")
            onResult(status)
        }
    )

    val lifecycle = activity.lifecycle
    DisposableEffect(lifecycle, permission) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                Log.d("PermissionControllerAndroid","PermissionAware: Lifecycle ON_RESUME")
                val permissionStatus = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    PermissionStatus.Granted
                } else when {
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED ->
                        PermissionStatus.Granted
                    activity.shouldShowRequestPermissionRationale(permission) ->
                        PermissionStatus.RationaleNeeded
                    else ->
                        PermissionStatus.PermanentlyDenied
                }
                onResult(permissionStatus)
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val appSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ -> // We don't need the ActivityResult data itself, just the fact that it returned
        // Re-check permission status when returning from settings
        val currentStatus = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            PermissionStatus.Granted
        } else when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED ->
                PermissionStatus.Granted
            activity?.shouldShowRequestPermissionRationale(permission) == true ->
                PermissionStatus.RationaleNeeded
            else ->
                PermissionStatus.NotGranted
        }
        onResult(currentStatus) // Notify the caller of the potentially changed status
    }

    return remember(permission, context, launcherPermission, appSettingsLauncher) {
        AndroidPermissionController(permission, context, launcherPermission, appSettingsLauncher)
    }
}

@Composable
actual fun checkPermissionStatus(permission: String): PermissionStatus {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) { // API level 31
        return PermissionStatus.Granted
    }
    return when {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED ->
            PermissionStatus.Granted
        activity?.shouldShowRequestPermissionRationale(permission) == true ->
            PermissionStatus.RationaleNeeded
        else ->
            PermissionStatus.NotGranted
    }
}