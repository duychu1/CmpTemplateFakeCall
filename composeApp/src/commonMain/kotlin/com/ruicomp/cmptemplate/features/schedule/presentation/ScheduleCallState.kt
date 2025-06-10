package com.ruicomp.cmptemplate.features.schedule.presentation

import com.ruicomp.cmptemplate.core.database.models.Contact

data class ScheduleCallState(
    val name: String = "",
    val number: String = "",
    val scheduledTime: String? = null,
    val isScheduling: Boolean = false,
    val error: String? = null,
    val contacts: List<Contact> = emptyList(),
    val isContactSheetVisible: Boolean = false
) 