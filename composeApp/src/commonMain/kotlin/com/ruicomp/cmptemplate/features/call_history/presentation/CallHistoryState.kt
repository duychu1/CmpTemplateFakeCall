package com.ruicomp.cmptemplate.features.call_history.presentation

import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory

data class CallHistoryState(
    val history: List<CallHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showBottomSheet: Boolean = false,
    val selectedHistoryForRecall: CallHistory? = null
)
