package com.ruicomp.cmptemplate.core.permissions

import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import kotlinx.coroutines.flow.StateFlow

interface PermissionHandler {
    val permissionState: StateFlow<PermissionState>
    fun onPermissionStatus(permission: String, status: PermissionStatus)
    fun onPermissionResult(status: PermissionStatus)
}