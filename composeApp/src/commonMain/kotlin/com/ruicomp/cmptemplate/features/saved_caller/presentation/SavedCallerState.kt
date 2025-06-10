package com.ruicomp.cmptemplate.features.saved_caller.presentation

import com.ruicomp.cmptemplate.core.database.models.Contact

data class SavedCallerState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)