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
            is SavedCallerEvent.AddContact -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        callerRepository.insertCaller(event.name, event.number)
                        _uiState.update { it.copy(isLoading = false) }
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
                viewModelScope.launch {
                    fakeCallManager.triggerFakeCall(
                        callerName = event.contact.name,
                        callerNumber = event.contact.number,
                        callerAvatarUrl = null
                    )
                    callHistoryRepository.addCallToHistory(event.contact)
                }
            }
        }
    }
}
