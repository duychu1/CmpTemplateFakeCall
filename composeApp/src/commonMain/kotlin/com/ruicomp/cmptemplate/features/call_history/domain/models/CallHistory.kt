package com.ruicomp.cmptemplate.features.call_history.domain.models

import com.ruicomp.cmptemplate.core.models.Contact

data class CallHistory(
    val id: Long,
    val name: String,
    val number: String,
    val timestamp: Long
) {
    fun asContact(): Contact {
        return Contact(
            id = id,
            name = name,
            number = number
        )
    }
}