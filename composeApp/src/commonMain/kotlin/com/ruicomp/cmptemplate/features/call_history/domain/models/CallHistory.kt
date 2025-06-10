package com.ruicomp.cmptemplate.features.call_history.domain.models

data class CallHistory(
    val id: Long,
    val name: String,
    val number: String,
    val timestamp: Long
) 