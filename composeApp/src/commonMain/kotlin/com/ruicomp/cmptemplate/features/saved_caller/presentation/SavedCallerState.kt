package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.models.Contact

data class SavedCallerState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddContactDialog: Boolean = false,
    val showBottomSheet: Boolean = false,
    val selectedContactForCall: Contact? = null,
    val addContactName: String = "",
    val addContactNumber: String = ""
)