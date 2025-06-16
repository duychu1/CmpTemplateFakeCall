package com.ruicomp.cmptemplate.core.permissions

import androidx.compose.runtime.Composable
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus

/**
 * A controller that encapsulates platform-specific permission actions.
 */
interface PlatformPermissionController {
    fun requestPermission()
    fun openAppSettings()
}

/**
 * A Composable that remembers a platform-specific controller.
 * @param onResult Callback for when the user responds to the permission dialog.
 */
@Composable
expect fun rememberPlatformPermissionController(
    permission: String,
    onResult: (PermissionStatus) -> Unit
): PlatformPermissionController

/**
 * A Composable function to get the initial status of a permission.
 * This is a Composable to ensure it can access platform-specific context if needed.
 */
@Composable
expect fun checkPermissionStatus(permission: String): PermissionStatus