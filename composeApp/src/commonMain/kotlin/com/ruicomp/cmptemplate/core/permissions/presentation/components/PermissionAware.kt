package com.ruicomp.cmptemplate.core.permissions.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import com.ruicomp.cmptemplate.core.permissions.getInitialPermissionStatus
import com.ruicomp.cmptemplate.core.permissions.rememberPlatformPermissionController

@Composable
fun PermissionAware(
    permission: String,
    permissionNameDialog: String,
    // NEW: Receive state from the outside
    permissionState: PermissionState,
    // NEW: Callbacks for events
    onPermissionStatusChecked: (PermissionStatus) -> Unit,
    onPermissionResult: (isGranted: Boolean, shouldShowRationale: Boolean) -> Unit,
    // Content lambdas remain the same
    grantedContent: @Composable () -> Unit = { },
    rationaleContent: @Composable (onRequestPermission: () -> Unit) -> Unit = { DefaultRationaleContent(permissionNameDialog, it) },
    permanentlyDeniedContent: @Composable (onOpenSettings: () -> Unit) -> Unit = { DefaultPermanentlyDeniedContent(permissionNameDialog, it) },
    notApplicableContent: @Composable () -> Unit = grantedContent
) {

    // 1. Get the platform-specific controller, which now calls the provided lambda.
    val controller = rememberPlatformPermissionController(
        permission = permission,
        onResult = onPermissionResult // Pass the callback directly
    )

    // 2. Check the initial status and report it up via the callback.
    val initialStatus = getInitialPermissionStatus(permission)
    LaunchedEffect(initialStatus) {
        onPermissionStatusChecked(initialStatus)
    }

    // 3. Render UI based on the INJECTED state.
    when (permissionState) {
        is PermissionState.Granted -> grantedContent()
        is PermissionState.Initial, is PermissionState.RationaleRequired -> {
            rationaleContent { controller.requestPermission() }
        }
        is PermissionState.PermanentlyDenied -> {
            permanentlyDeniedContent { controller.openAppSettings() }
        }
        is PermissionState.NotApplicable -> notApplicableContent()
    }
}

// Default rationale content that can be overridden
@Composable
internal fun DefaultRationaleContent(
    permissionNameDialog: String,
    onRequestPermission: () -> Unit
) {
    PermissionRationaleDialog(
        title = "Permission Required",
        message = "$permissionNameDialog permission is required to provide relevant information.",
        onAgree = onRequestPermission,
        onCancel = {},
        onDismiss = {}
    )
}

// Default permanently denied content that can be overridden
@Composable
internal fun DefaultPermanentlyDeniedContent(
    permissionNameDialog: String,
    onOpenSettings: () -> Unit
) {
    PermissionRationaleDialog(
        title = "Permission Required",
        message = "$permissionNameDialog permission has been permanently denied. Please enable it in app settings to use this feature.",
        onAgree = onOpenSettings,
        agreeText = "Settings",
        onCancel = {},
        onDismiss = {}
    )
}
