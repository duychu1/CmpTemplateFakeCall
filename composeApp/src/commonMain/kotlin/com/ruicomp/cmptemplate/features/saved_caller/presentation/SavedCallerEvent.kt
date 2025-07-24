package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.utils.PickedContact // Added import

sealed class SavedCallerEvent {
    object LoadContacts : SavedCallerEvent()
    data class DeleteContact(val id: Long) : SavedCallerEvent()
    data class SelectContactForCall(val contact: Contact?) : SavedCallerEvent()
    //dialog add contact
    data class ShowAddContactDialog(val show: Boolean) : SavedCallerEvent()
    data class UpdateAddContactName(val name: String) : SavedCallerEvent()
    data class UpdateAddContactNumber(val number: String) : SavedCallerEvent()
    object AddContact : SavedCallerEvent()

    object TriggerShowBottomSheet : SavedCallerEvent()
    object ImportContactsFromSystemClicked : SavedCallerEvent()

    // For Composable Contact Picker
    data class ContactPicked(val name: String, val number: String) : SavedCallerEvent()
    data object ResetContactPickerLaunchTrigger : SavedCallerEvent()
}