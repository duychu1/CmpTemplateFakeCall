package com.ruicomp.cmptemplate.features.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleCallViewModel(
    private val callHistoryRepository: CallHistoryRepository,
    private val callerRepository: CallerRepository
) : ViewModel() {

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
                _uiState.update { it.copy(selectedDateMillis = event.millis) }
            }
            is ScheduleCallEvent.TimeSelected -> {
                _uiState.update { it.copy(selectedHour = event.hour, selectedMinute = event.minute) }
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
                if (currentState.name.isNotBlank() && currentState.number.isNotBlank()) {
                    val contactToSchedule = Contact(id = 0L, name = currentState.name, number = currentState.number)
                    viewModelScope.launch {
                        _uiState.update { it.copy(isScheduling = true, error = null) }
                        try {
                            callHistoryRepository.addCallToHistory(contactToSchedule)
                            _uiState.update { it.copy(isScheduling = false, scheduledTime = event.time) }
                        } catch (e: Exception) {
                            _uiState.update { it.copy(isScheduling = false, error = e.message) }
                        }
                    }
                }
            }
        }
    }
}
