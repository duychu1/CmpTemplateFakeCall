package com.ruicomp.cmptemplate.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus

// A private class to hold the Android-specific implementation details
private class AndroidPermissionController(
    private val permission: String,
    private val context: Context,
    private val launcher: androidx.activity.result.ActivityResultLauncher<String>
) : PlatformPermissionController {

    override fun requestPermission() {
        launcher.launch(permission)
    }

    override fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberPlatformPermissionController(
    permission: String,
    onResult: (isGranted: Boolean, shouldShowRationale: Boolean) -> Unit
): PlatformPermissionController {
    val context = LocalContext.current
    val activity = context as androidx.activity.ComponentActivity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onResult(isGranted, activity.shouldShowRequestPermissionRationale(permission))
        }
    )

    return remember(permission, context, launcher) {
        AndroidPermissionController(permission, context, launcher)
    }
}

@Composable
actual fun getInitialPermissionStatus(permission: String): PermissionStatus {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

    return when {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED ->
            PermissionStatus.Granted
        activity?.shouldShowRequestPermissionRationale(permission) == true ->
            PermissionStatus.RationaleNeeded(permission)
        else ->
            PermissionStatus.Denied
    }
}