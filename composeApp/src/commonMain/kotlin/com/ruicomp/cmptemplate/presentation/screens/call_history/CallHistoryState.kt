package com.ruicomp.cmptemplate.presentation.screens.call_history

import com.ruicomp.cmptemplate.domain.models.CallHistory

data class CallHistoryState(
    val history: List<CallHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 