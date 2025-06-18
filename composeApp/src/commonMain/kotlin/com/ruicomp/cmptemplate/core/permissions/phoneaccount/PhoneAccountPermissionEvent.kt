package com.ruicomp.cmptemplate.core.permissions.phoneaccount

sealed interface PhoneAccountPermissionEvent {
    data object AgreeRationalPermissionDialogClicked : PhoneAccountPermissionEvent
    data object CancelRationalPermissionDialogClicked : PhoneAccountPermissionEvent
    data object RationalPermissionDialogDismissed : PhoneAccountPermissionEvent
}