package com.ruicomp.cmptemplate.features.schedule.presentation

import com.ruicomp.cmptemplate.core.models.Contact

sealed class ScheduleCallEvent {
    object LoadContacts : ScheduleCallEvent()
    data class NameChanged(val name: String) : ScheduleCallEvent()
    data class NumberChanged(val number: String) : ScheduleCallEvent()
    data class DateSelected(val millis: Long) : ScheduleCallEvent()
    data class TimeSelected(val hour: Int, val minute: Int) : ScheduleCallEvent()
    data class Schedule(val time: String) : ScheduleCallEvent()
    data class SelectContact(val contact: Contact) : ScheduleCallEvent()
    object ShowContactSheet : ScheduleCallEvent()
    object HideContactSheet : ScheduleCallEvent()
    data class CancelScheduleCall(val id: Int) : ScheduleCallEvent()
}
