package com.ruicomp.cmptemplate.core.permissions.presentation

sealed class BasePermissionEvent {
    data class ShowPermissionAwareChange(val permission: String, val show: Boolean) : BasePermissionEvent()
    data class PermissionStatusChecked(val permission: String, val status: PermissionStatus) : BasePermissionEvent()
    data class PermissionResult(val permission: String, val status: PermissionStatus) : BasePermissionEvent()
    data class OnInitialed(val permission: String, val initialed: Boolean) : BasePermissionEvent()
}
