package com.ruicomp.cmptemplate.presentation.screens.saved_caller

import com.ruicomp.cmptemplate.domain.models.Contact

data class SavedContactsState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 