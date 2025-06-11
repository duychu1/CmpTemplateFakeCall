package com.ruicomp.cmptemplate.features.saved_caller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val callHistoryRepository: CallHistoryRepository
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

    fun onAddContact(name: String, number: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                callerRepository.insertCaller(name, number)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onDeleteContact(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                callerRepository.deleteCaller(id)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCallContact(contact: Contact) {
        viewModelScope.launch {
            callHistoryRepository.addCallToHistory(contact)
            // TODO: A fake call screen will be shown here
        }
    }
}
