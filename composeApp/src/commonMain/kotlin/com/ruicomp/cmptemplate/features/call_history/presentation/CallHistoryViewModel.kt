package com.ruicomp.cmptemplate.features.call_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.database.models.Contact
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallHistoryState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun onRecall(contact: Contact) {
        viewModelScope.launch {
            callHistoryRepository.addCallToHistory(contact)
        }
    }

    private fun loadHistory() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        callHistoryRepository.getCallHistory().onEach { history ->
            _uiState.update {
                it.copy(
                    history = history,
                    isLoading = false,
                    error = null
                )
            }
        }.launchIn(viewModelScope)
    }
} 