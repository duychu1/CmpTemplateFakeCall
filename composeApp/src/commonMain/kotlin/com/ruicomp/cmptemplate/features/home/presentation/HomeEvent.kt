package com.ruicomp.cmptemplate.features.home.presentation

sealed interface HomeEvent {
    data object CallNowClicked : HomeEvent
//    data object ScheduleCallClicked : HomeEvent // Example for another feature
//    data object SavedCallerClicked : HomeEvent  // Example for another feature
//    data object CallHistoryClicked : HomeEvent  // Example for another feature
//    data object SettingsClicked : HomeEvent

    // Events related to permission handling
    data object AgreeRationalPermissionDialogClicked : HomeEvent
    data object CancelRationalPermissionDialogClicked : HomeEvent
    data object RationalPermissionDialogDismissed : HomeEvent
}