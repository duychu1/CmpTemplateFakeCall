package com.ruicomp.cmptemplate.features.schedule.presentation

import com.ruicomp.cmptemplate.core.models.Contact

data class ScheduleCallState(
    // Schedule Call Screen State
    val scheduledTime: String? = null,
    val isScheduling: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,

    // Contact Input State
    val name: String = "",
    val number: String = "",

    // Contact Picker State
    val isContactSheetVisible: Boolean = false,
    val contacts: List<Contact> = emptyList(),

    // Date Time Picker State
    val selectedDateMillis: Long? = null,
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
    val formattedDate: String = "",
    val formattedTime: String = "",

)