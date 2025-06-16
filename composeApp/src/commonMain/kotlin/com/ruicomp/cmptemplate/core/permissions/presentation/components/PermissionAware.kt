package com.ruicomp.cmptemplate.core.permissions.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import com.ruicomp.cmptemplate.core.permissions.checkPermissionStatus
import com.ruicomp.cmptemplate.core.permissions.rememberPlatformPermissionController

@Composable
fun PermissionAware(
    permission: String,
    permissionNameDialog: String,
    onShowPermissionAwareChange: (Boolean) -> Unit = {},
    onPermissionStatusChecked: (PermissionStatus) -> Unit,
    onPermissionResult: (PermissionStatus) -> Unit,
) {

    var isShowRationale by remember { mutableStateOf(true) }

    val tmpPermissionStatus = checkPermissionStatus(permission)

    var permissionStatus: PermissionStatus by remember { mutableStateOf(tmpPermissionStatus) } // Now it's Compose State
//    permissionStatus = tmpPermissionStatus
    LaunchedEffect(permissionStatus) {
        onPermissionStatusChecked(permissionStatus)
    }
    println("PermissionAware: Initial permission status for $permission is $permissionStatus")

    val controller = rememberPlatformPermissionController(
        permission = permission,
        onResult = {
            println("PermissionAware: Permission result for $permission is $it")
            permissionStatus = it
            onPermissionResult(it)
        } // Pass the callback directly
    )

    // 2. Check the initial status and report it up via the callback.
    when (permissionStatus) {
        is PermissionStatus.Granted -> { }
        PermissionStatus.NotApplicable -> { }
        PermissionStatus.NotGranted -> {
//            CustomAlertDialog(
//                title = "Permission Required",
//                message = "$permissionNameDialog permission has been permanently denied. Please enable it in app settings to use this feature.",
//                onAgree = controller::openAppSettings,
//                agreeText = "Settings",
//                onDismiss = { onShowPermissionAwareChange(false) },
//                onCancel = { onShowPermissionAwareChange(false) },
//            )
            LaunchedEffect(Unit) { // LaunchedEffect to call suspend function
                controller.requestPermission()
            }
        }
        PermissionStatus.PermanentlyDenied -> {
            CustomAlertDialog(
                title = "Permission Required",
                message = "$permissionNameDialog permission has been permanently denied. Please enable it in app settings to use this feature.",
                onAgree = controller::openAppSettings,
                agreeText = "Settings",
                onDismiss = { onShowPermissionAwareChange(false) },
                onCancel = { onShowPermissionAwareChange(false) },
            )
        }
        PermissionStatus.RationaleNeeded -> {
            if (isShowRationale) {
                CustomAlertDialog(
                    title = "Permission Required",
                    message = "$permissionNameDialog permission is required to app run correctly.",
                    onAgree = {
                        controller.requestPermission()
                        isShowRationale = false
                    },
                    onDismiss = { onShowPermissionAwareChange(false) },
                    onCancel = { onShowPermissionAwareChange(false) },
                )
            }
        }
    }

}

// Default rationale content that can be overridden
@Composable
internal fun DefaultRationaleContent(
    permissionNameDialog: String,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
) {
    CustomAlertDialog(
        title = "Permission Required",
        message = "$permissionNameDialog permission is required to provide relevant information.",
        onAgree = onRequestPermission,
        onCancel = onCancel,
        onDismiss = onDismiss,
    )
}

// Default permanently denied content that can be overridden
@Composable
internal fun DefaultPermanentlyDeniedContent(
    permissionNameDialog: String,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
) {
    CustomAlertDialog(
        title = "Permission Required",
        message = "$permissionNameDialog permission has been permanently denied. Please enable it in app settings to use this feature.",
        onAgree = onOpenSettings,
        agreeText = "Settings",
        onCancel = onCancel,
        onDismiss = onDismiss,
    )
}
