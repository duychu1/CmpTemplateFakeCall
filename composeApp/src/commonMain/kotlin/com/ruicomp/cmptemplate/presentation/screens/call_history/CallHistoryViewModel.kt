package com.ruicomp.cmptemplate.presentation.screens.call_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.domain.models.CallHistory
import com.ruicomp.cmptemplate.domain.models.Contact
import com.ruicomp.cmptemplate.domain.usecases.AddCallToHistory
import com.ruicomp.cmptemplate.domain.usecases.GetCallHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val getCallHistory: GetCallHistory,
    private val addCallToHistory: AddCallToHistory
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallHistoryState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun onRecall(contact: Contact) {
        viewModelScope.launch {
            addCallToHistory(contact)
        }
    }

    private fun loadHistory() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        getCallHistory().onEach { history ->
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