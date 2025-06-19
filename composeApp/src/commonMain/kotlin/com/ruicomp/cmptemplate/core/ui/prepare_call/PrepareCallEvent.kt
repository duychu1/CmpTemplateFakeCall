package com.ruicomp.cmptemplate.core.ui.prepare_call

import com.ruicomp.cmptemplate.core.models.Contact

sealed class PrepareCallEvent {
    data class ShowSheet(val contact: Contact) : PrepareCallEvent()
    object HideSheet : PrepareCallEvent()
    data class SelectTime(val timeInSeconds: Int) : PrepareCallEvent()
    object StartCall : PrepareCallEvent() // New event to trigger call from manager
}