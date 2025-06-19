package com.ruicomp.cmptemplate.features.call_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallEvent
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallManager
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val callHistoryRepository: CallHistoryRepository,
    val prepareCallManager: PrepareCallManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallHistoryState())
    val uiState = _uiState.asStateFlow()

    init {
        onEvent(CallHistoryEvent.LoadHistory)
    }

    fun onEvent(event: CallHistoryEvent) {
        when (event) {
            is CallHistoryEvent.LoadHistory -> {
                loadHistory()
            }
            is CallHistoryEvent.SelectHistoryForRecall -> {
                _uiState.update { it.copy(selectedHistoryForRecall = event.history) }
                onEvent(CallHistoryEvent.TriggerShowBottomSheet)
            }
            is CallHistoryEvent.TriggerShowBottomSheet -> {
                _uiState.value.selectedHistoryForRecall?.let { history ->
                    prepareCallManager.onEvent(PrepareCallEvent.ShowSheet(history.asContact()))
                }
            }
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

    override fun onCleared() {
        super.onCleared()
        prepareCallManager.clear()
    }
}

