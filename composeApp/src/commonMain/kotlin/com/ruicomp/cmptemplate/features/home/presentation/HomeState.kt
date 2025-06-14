package com.ruicomp.cmptemplate.features.home.presentation

data class HomeState(
    val isMakeCallPermissionGranted: Boolean = false,
    val shouldShowPermissionRationaleDialog: Boolean = false, // To show a dialog if permission was denied
    // Add other state properties as needed, e.g., for loading indicators, data for other features
)