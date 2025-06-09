package com.ruicomp.cmptemplate.presentation.screens.saved_caller

import com.ruicomp.cmptemplate.domain.models.Caller

data class SavedCallerState(
    val callers: List<Caller> = emptyList()
) 