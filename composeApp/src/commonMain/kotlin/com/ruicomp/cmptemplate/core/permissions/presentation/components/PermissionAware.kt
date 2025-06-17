package com.ruicomp.cmptemplate.core.permissions.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.permission_permanently_denied_message
import cmptemplate.composeapp.generated.resources.permission_rationale_message
import cmptemplate.composeapp.generated.resources.permission_required_title
import cmptemplate.composeapp.generated.resources.settings_title
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import com.ruicomp.cmptemplate.core.permissions.checkPermissionStatus
import com.ruicomp.cmptemplate.core.permissions.rememberPlatformPermissionController
import org.jetbrains.compose.resources.stringResource

@Composable
fun PermissionAware(
    permission: String,
    permissionNameDialog: String,
    onShowPermissionAwareChange: (Boolean) -> Unit = {},
    onPermissionStatusChecked: (PermissionStatus) -> Unit,
    onPermissionResult: (PermissionStatus) -> Unit,
) {

    var isShowRationale by remember { mutableStateOf(true) }
    var isShowPermanentlyDenied by remember { mutableStateOf(true) }

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
                title = stringResource(Res.string.permission_required_title),
                message = stringResource(
                    Res.string.permission_permanently_denied_message,
                    permissionNameDialog
                ),
                onAgree = {
                    controller.openAppSettings()
                    onShowPermissionAwareChange(false)
                },
                agreeText = stringResource(Res.string.settings_title),
                onDismiss = { onShowPermissionAwareChange(false) },
                onCancel = { onShowPermissionAwareChange(false) },
            )
        }
        PermissionStatus.RationaleNeeded -> {
            CustomAlertDialog(
                title = stringResource(Res.string.permission_required_title),
                message = stringResource(Res.string.permission_rationale_message, permissionNameDialog),
                onAgree = {
                    controller.requestPermission()
                    onShowPermissionAwareChange(false)
                },
                onDismiss = { onShowPermissionAwareChange(false) },
                onCancel = { onShowPermissionAwareChange(false) },
            )
        }
    }

}
