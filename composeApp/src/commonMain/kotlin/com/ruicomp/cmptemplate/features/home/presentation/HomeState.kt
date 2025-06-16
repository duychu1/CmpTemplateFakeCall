package com.ruicomp.cmptemplate.features.home.presentation

data class HomeState(
    val showPhoneAccountPermissionRationaleDialog: Boolean = false, // To show a dialog if permission was denied
    val showPermissionAwarePhone:Boolean = false,

)