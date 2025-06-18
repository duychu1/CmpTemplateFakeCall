package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.models.Contact

sealed class SavedCallerEvent {
    object AddContact : SavedCallerEvent() // Changed: No longer takes name and number
    data class DeleteContact(val id: Long) : SavedCallerEvent()
    data class CallContact(val contact: Contact?) : SavedCallerEvent()
    object LoadContacts : SavedCallerEvent()
    data class ShowAddContactDialog(val show: Boolean) : SavedCallerEvent()
    data class ShowBottomSheet(val show: Boolean) : SavedCallerEvent()
    data class SelectContactForCall(val contact: Contact?) : SavedCallerEvent()
    data class UpdateAddContactName(val name: String) : SavedCallerEvent() // New
    data class UpdateAddContactNumber(val number: String) : SavedCallerEvent() // New
}