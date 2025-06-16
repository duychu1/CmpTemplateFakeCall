package com.ruicomp.cmptemplate.core.permissions

import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionHandlerImpl : PermissionHandler {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.NotGranted)
    override val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    override fun onPermissionStatus(permission: String, status: PermissionStatus) {
        _permissionState.value = when (status) {
            is PermissionStatus.Granted -> PermissionState.Granted
            is PermissionStatus.NotGranted -> PermissionState.NotGranted
            is PermissionStatus.RationaleNeeded -> PermissionState.RationaleRequired
            is PermissionStatus.NotApplicable -> PermissionState.NotApplicable
            PermissionStatus.PermanentlyDenied -> TODO()
        }
    }

    override fun onPermissionResult(status: PermissionStatus) {
        _permissionState.value = when (status) {
            is PermissionStatus.Granted -> PermissionState.Granted
            is PermissionStatus.NotGranted -> PermissionState.NotGranted
            is PermissionStatus.RationaleNeeded -> PermissionState.RationaleRequired
            is PermissionStatus.NotApplicable -> PermissionState.NotApplicable
            PermissionStatus.PermanentlyDenied -> TODO()
        }
    }
}