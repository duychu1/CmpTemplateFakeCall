package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.models.Contact

sealed class SavedCallerEvent {
    data class AddContact(val name: String, val number: String) : SavedCallerEvent()
    data class DeleteContact(val id: Long) : SavedCallerEvent()
    data class CallContact(val contact: Contact) : SavedCallerEvent()
    object LoadContacts : SavedCallerEvent()
}

