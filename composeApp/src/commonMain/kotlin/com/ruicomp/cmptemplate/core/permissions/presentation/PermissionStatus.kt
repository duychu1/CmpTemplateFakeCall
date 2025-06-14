package com.ruicomp.cmptemplate.core.permissions.presentation

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object Denied : PermissionStatus()
    data class RationaleNeeded(val permission: String) : PermissionStatus()
    data object NotApplicable : PermissionStatus() // New status
}