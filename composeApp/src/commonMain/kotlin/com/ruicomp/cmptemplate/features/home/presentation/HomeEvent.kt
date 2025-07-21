package com.ruicomp.cmptemplate.features.home.presentation

import com.ruicomp.cmptemplate.features.saved_caller.presentation.SavedCallerEvent

sealed class HomeEvent {
    data object CallNowClicked : HomeEvent()
    //input contact dialog
    data class ShowTmpContactDialog(val show: Boolean) : HomeEvent()
    data class UpdateTmpContactName(val name: String) : HomeEvent() // New
    data class UpdateTmpContactNumber(val number: String) : HomeEvent() // New
    object UpdateContact : HomeEvent()
    data class DelayItemSelected(val delay: Int) : HomeEvent()
    
}