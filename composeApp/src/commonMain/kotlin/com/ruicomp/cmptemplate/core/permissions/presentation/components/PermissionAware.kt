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
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionEvent
import com.ruicomp.cmptemplate.core.permissions.rememberPlatformPermissionController
import org.jetbrains.compose.resources.stringResource

/**
 * A Composable function that handles permission requests and updates the UI accordingly.
 * It checks the initial permission status, requests permission if not granted,
 * and shows appropriate dialogs for rationale or permanently denied states.
 *
 * @param permission The permission string (e.g., `android.permission.CAMERA`).
 * @param permissionNameDialog The user-friendly name of the permission to be displayed in dialogs.
 * @param initialed A boolean flag indicating whether the initial permission check has been performed.
 *                  If `false`, it triggers an initial event.
 * @param onEvent Callback to dispatch [BasePermissionEvent]s, allowing the caller to react to
 *                permission status changes, results, and UI interactions.
 * @param isShowCancel A boolean flag to determine if the cancel button should be shown in the
 *                     permanently denied dialog. Defaults to `false`.
 */
@Composable
fun PermissionAware(
    permission: String,
    permissionNameDialog: String,
    initialed: Boolean,
    onEvent: (BasePermissionEvent) -> Unit,
    isShowCancel: Boolean = false,
) {

    var isShowRationale by remember { mutableStateOf(true) }
    var isShowPermanentlyDenied by remember { mutableStateOf(true) }

    val tmpPermissionStatus = checkPermissionStatus(permission)

    var permissionStatus: PermissionStatus by remember { mutableStateOf(tmpPermissionStatus) } // Now it's Compose State
//    permissionStatus = tmpPermissionStatus
    LaunchedEffect(permissionStatus) {
        onEvent(BasePermissionEvent.PermissionStatusChecked(permission, permissionStatus))
        println("PermissionAware: LaunchedEffect Permission status for $permission is $permissionStatus")
    }
    println("PermissionAware: Initial permission status for $permission is $permissionStatus")

    val controller = rememberPlatformPermissionController(
        permission = permission,
        onResult = {
            println("PermissionAware: Permission result for $permission is $it")
            permissionStatus = it
            onEvent(BasePermissionEvent.PermissionResult(permission,it))
        } // Pass the callback directly
    )

    // 2. Check the initial status and report it up via the callback.
    if (!initialed) {
        onEvent(BasePermissionEvent.OnInitialed(permission,true))
    } else {
        when (permissionStatus) {
            is PermissionStatus.Granted -> {}
            PermissionStatus.NotApplicable -> {}
            PermissionStatus.NotGranted -> {
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
                        
                    },
                    agreeText = stringResource(Res.string.settings_title),
                    onDismiss = {
                        if (isShowCancel) { onEvent(BasePermissionEvent.ShowPermissionAwareChange(permission, false)) }
                    },
                    onCancel = {
                        onEvent(BasePermissionEvent.ShowPermissionAwareChange(permission, false))
                    },
                    isShowCancel = isShowCancel,
                )
            }

            PermissionStatus.RationaleNeeded -> {
                CustomAlertDialog(
                    title = stringResource(Res.string.permission_required_title),
                    message = stringResource(
                        Res.string.permission_rationale_message,
                        permissionNameDialog
                    ),
                    onAgree = {
                        controller.requestPermission()
                        
                    },
                    onDismiss = {  },
                    onCancel = {  },
                    isShowCancel = false,
                )
            }
        }
    }
}
