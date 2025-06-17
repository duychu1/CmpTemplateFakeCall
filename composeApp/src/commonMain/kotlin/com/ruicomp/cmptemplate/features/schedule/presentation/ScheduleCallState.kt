package com.ruicomp.cmptemplate.features.schedule.presentation

import com.ruicomp.cmptemplate.core.models.Contact

data class ScheduleCallState(
    val contacts: List<Contact> = emptyList(),
    val name: String = "",
    val number: String = "",
    val scheduledTime: String? = null,
    val isScheduling: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isContactSheetVisible: Boolean = false,
    val selectedDateMillis: Long? = null,
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
    val formattedDate: String = "",
    val formattedTime: String = ""
)