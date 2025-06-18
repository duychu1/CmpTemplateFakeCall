package com.ruicomp.cmptemplate.features.saved_caller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedCallerViewModel(
    private val callerRepository: CallerRepository,
    private val callHistoryRepository: CallHistoryRepository,
    private val fakeCallManager: IFakeCallManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedCallerState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        callerRepository.getContacts().onEach { callers ->
            _uiState.update {
                it.copy(
                    contacts = callers,
                    isLoading = false,
                    error = null
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SavedCallerEvent) {
        when (event) {
            is SavedCallerEvent.LoadContacts -> {
                loadContacts()
            }
            is SavedCallerEvent.AddContact -> { // Changed: Now uses name and number from uiState
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        val name = _uiState.value.addContactName
                        val number = _uiState.value.addContactNumber
                        if (name.isNotBlank() && number.isNotBlank()) {
                            callerRepository.insertCaller(name, number)
                            _uiState.update { it.copy(isLoading = false, addContactName = "", addContactNumber = "") } // Clear fields
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Name and number cannot be empty.") } // Or handle specific error
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            }
            is SavedCallerEvent.DeleteContact -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        callerRepository.deleteCaller(event.id)
                        _uiState.update { it.copy(isLoading = false) }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            }
            is SavedCallerEvent.CallContact -> {
                event.contact?.let { contact ->
                    viewModelScope.launch {
                        fakeCallManager.triggerFakeCall(
                            callerName = contact.name,
                            callerNumber = contact.number,
                            callerAvatarUrl = null
                        )
                        callHistoryRepository.addCallToHistory(contact)
                    }
                }
            }
            is SavedCallerEvent.ShowAddContactDialog -> {
                _uiState.update { it.copy(showAddContactDialog = event.show) }
                if (!event.show) { // Clear fields when dialog is dismissed
                    _uiState.update { it.copy(addContactName = "", addContactNumber = "") }
                }
            }
            is SavedCallerEvent.ShowBottomSheet -> {
                _uiState.update { it.copy(showBottomSheet = event.show) }
            }
            is SavedCallerEvent.SelectContactForCall -> {
                _uiState.update { it.copy(selectedContactForCall = event.contact) }
            }
            is SavedCallerEvent.UpdateAddContactName -> { // New
                _uiState.update { it.copy(addContactName = event.name) }
            }
            is SavedCallerEvent.UpdateAddContactNumber -> { // New
                _uiState.update { it.copy(addContactNumber = event.number) }
            }
        }
    }
}