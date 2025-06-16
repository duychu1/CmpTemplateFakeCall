package com.ruicomp.cmptemplate.core.permissions.presentation

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object NotGranted : PermissionStatus()
    data object RationaleNeeded : PermissionStatus()
    data object PermanentlyDenied : PermissionStatus()
    data object NotApplicable : PermissionStatus()
}