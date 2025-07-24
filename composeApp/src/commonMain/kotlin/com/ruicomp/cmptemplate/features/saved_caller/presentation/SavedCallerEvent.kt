package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.models.Contact

sealed class SavedCallerEvent {
    object LoadContacts : SavedCallerEvent()
    data class DeleteContact(val id: Long) : SavedCallerEvent()
    data class SelectContactForCall(val contact: Contact?) : SavedCallerEvent()
    //dialog add contact
    data class ShowAddContactDialog(val show: Boolean) : SavedCallerEvent()
    data class UpdateAddContactName(val name: String) : SavedCallerEvent() // New
    data class UpdateAddContactNumber(val number: String) : SavedCallerEvent() // New
    object AddContact : SavedCallerEvent()

    object TriggerShowBottomSheet : SavedCallerEvent()
    object ImportContactsFromSystemClicked : SavedCallerEvent()
}