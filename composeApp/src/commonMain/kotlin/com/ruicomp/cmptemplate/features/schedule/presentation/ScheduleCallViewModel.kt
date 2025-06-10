package com.ruicomp.cmptemplate.features.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.database.models.Contact
import com.ruicomp.cmptemplate.core.domain.repository.CallerRepository
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
        loadContacts()
    }

    private fun loadContacts() {
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

    fun onContactSelected(contact: Contact) {
        _uiState.update {
            it.copy(
                name = contact.name,
                number = contact.number,
                isContactSheetVisible = false
            )
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onNumberChange(number: String) {
        _uiState.update { it.copy(number = number) }
    }

    fun showContactSheet() {
        _uiState.update { it.copy(isContactSheetVisible = true) }
    }

    fun hideContactSheet() {
        _uiState.update { it.copy(isContactSheetVisible = false) }
    }

    fun onScheduleCall(time: String) {
        val currentState = _uiState.value
        if (currentState.name.isNotBlank() && currentState.number.isNotBlank()) {
            val contactToSchedule = Contact(id = 0L, name = currentState.name, number = currentState.number)
            viewModelScope.launch {
                _uiState.update { it.copy(isScheduling = true, error = null) }
                try {
                    callHistoryRepository.addCallToHistory(contactToSchedule)
                    _uiState.update { it.copy(isScheduling = false, scheduledTime = time) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isScheduling = false, error = e.message) }
                }
            }
        }
    }
} 