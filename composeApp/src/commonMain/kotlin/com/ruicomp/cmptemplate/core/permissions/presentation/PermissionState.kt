package com.ruicomp.cmptemplate.core.permissions.presentation

// --- State Model ---
sealed interface PermissionState {
    data object Initial : PermissionState
    data object Granted : PermissionState
    data object RationaleRequired : PermissionState
    data object PermanentlyDenied : PermissionState

    // A state for platforms where the permission is not applicable (e.g., iOS, Desktop for this specific permission)
    data object NotApplicable : PermissionState
}