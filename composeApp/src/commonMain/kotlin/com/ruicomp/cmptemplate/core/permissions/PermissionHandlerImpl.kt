package com.ruicomp.cmptemplate.core.permissions

import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionHandlerImpl : PermissionHandler {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Initial)
    override val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    override fun onPermissionStatus(permission: String, status: PermissionStatus) {
        _permissionState.value = when (status) {
            is PermissionStatus.Granted -> PermissionState.Granted
            is PermissionStatus.Denied -> PermissionState.Initial
            is PermissionStatus.RationaleNeeded -> PermissionState.RationaleRequired
            is PermissionStatus.NotApplicable -> PermissionState.NotApplicable
        }
    }

    override fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        _permissionState.value = when {
            isGranted -> PermissionState.Granted
            shouldShowRationale -> PermissionState.RationaleRequired
            else -> PermissionState.PermanentlyDenied
        }
    }
}