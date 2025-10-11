package com.ruicomp.cmptemplate.features.call_history.domain.models

data class CallHistoryGroup(
    val groupTitle: String,
    val items: List<CallHistory>
)