package com.ruicomp.cmptemplate.features.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class ScheduleCallViewModel(
    private val callHistoryRepository: CallHistoryRepository,
    private val callerRepository: CallerRepository,
    private val fakeCallManager: IFakeCallManager,
) : ViewModel() {

    private fun formatDate(millis: Long?): String {
        return millis?.let {
            val instant = Instant.fromEpochMilliseconds(it)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val monthName = localDate.month.name.lowercase().take(3).replaceFirstChar { char -> char.uppercase() }
            "$monthName ${localDate.dayOfMonth.toString().padStart(2, '0')}, ${localDate.year}"
        } ?: ""
    }

    private fun formatTime(hour: Int?, minute: Int?): String {
        return if (hour != null && minute != null) {
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
        } else ""
    }

    private val _uiState = MutableStateFlow(ScheduleCallState())
    val uiState = _uiState.asStateFlow()

    init {
        onEvent(ScheduleCallEvent.LoadContacts)
    }

    fun onEvent(event: ScheduleCallEvent) {
        when (event) {
            is ScheduleCallEvent.LoadContacts -> {
                callerRepository.getContacts().onEach { callers ->
                    _uiState.update {
                        it.copy(contacts = callers.map { c ->
                            Contact(
                                id = c.id,
                                name = c.name,
                                number = c.number
                            )
                        })
                    }
                }.launchIn(viewModelScope)
            }
            is ScheduleCallEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.name) }
            }
            is ScheduleCallEvent.NumberChanged -> {
                _uiState.update { it.copy(number = event.number) }
            }
            is ScheduleCallEvent.DateSelected -> {
                val formatted = formatDate(event.millis)
                _uiState.update { it.copy(selectedDateMillis = event.millis, formattedDate = formatted) }
            }
            is ScheduleCallEvent.TimeSelected -> {
                val formatted = formatTime(event.hour, event.minute)
                _uiState.update { it.copy(selectedHour = event.hour, selectedMinute = event.minute, formattedTime = formatted) }
            }
            is ScheduleCallEvent.SelectContact -> {
                _uiState.update {
                    it.copy(
                        name = event.contact.name,
                        number = event.contact.number,
                        isContactSheetVisible = false
                    )
                }
            }
            is ScheduleCallEvent.ShowContactSheet -> {
                _uiState.update { it.copy(isContactSheetVisible = true) }
            }
            is ScheduleCallEvent.HideContactSheet -> {
                _uiState.update { it.copy(isContactSheetVisible = false) }
            }
            is ScheduleCallEvent.Schedule -> {
                val currentState = _uiState.value
                if (currentState.name.isNotBlank() && currentState.number.isNotBlank() &&
                    currentState.selectedDateMillis != null && currentState.selectedHour != null && currentState.selectedMinute != null
                ) {
                    val contactToSchedule = Contact(id = 0L, name = currentState.name, number = currentState.number)

                    val selectedLocalDateTime = Instant.fromEpochMilliseconds(currentState.selectedDateMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        .atTime(currentState.selectedHour, currentState.selectedMinute)
                    val triggerAtMillis = selectedLocalDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

                    val nowMillis = Clock.System.now().toEpochMilliseconds()

                    if (triggerAtMillis < nowMillis) {
                        _uiState.update { it.copy(isScheduling = false, error = "Cannot schedule a call in the past. Please select a future time.") }
                        return
                    }

                    viewModelScope.launch {
                        _uiState.update { it.copy(isScheduling = true, error = null) }
                        try {
                            fakeCallManager.scheduleExactFakeCall(
                                callerName = currentState.name,
                                callerNumber = currentState.number,
                                // callerAvatarUrl = currentState.avatarUrl, // If you have avatar
                                triggerAtMillis = triggerAtMillis
                            )
                            callHistoryRepository.addCallToHistory(contactToSchedule)
                            val scheduledDateTimeStr = "${currentState.formattedDate} at ${currentState.formattedTime}"
                            _uiState.update { it.copy(isScheduling = false, scheduledTime = scheduledDateTimeStr) }
                        } catch (e: Exception) {
                            _uiState.update { it.copy(isScheduling = false, error = e.message) }
                        }
                    }
                } else {
                    _uiState.update { it.copy(isScheduling = false, error = "Please select a valid name, number, date, and time.") }
                }
            }
        }
    }
}
