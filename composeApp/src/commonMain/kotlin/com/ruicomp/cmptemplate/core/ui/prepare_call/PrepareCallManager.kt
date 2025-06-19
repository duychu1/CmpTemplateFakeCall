package com.ruicomp.cmptemplate.core.ui.prepare_call

import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrepareCallManager(
    private val fakeCallManager: IFakeCallManager,
    private val callHistoryRepository: CallHistoryRepository
    // Add CoroutineScope for launching tasks, or pass Dispatchers if preferred
) {

    private val managerScope = CoroutineScope(Dispatchers.Main + SupervisorJob()) // Or inject a scope

    private val _uiState = MutableStateFlow(PrepareCallState())
    val uiState = _uiState.asStateFlow()

    // Optional: Add a separate flow for one-time events like "call started, dismiss sheet"
    // private val _oneTimeEvent = MutableSharedFlow<PrepareCallOneTimeEvent>()
    // val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    fun onEvent(event: PrepareCallEvent) {
        when (event) {
            is PrepareCallEvent.ShowSheet -> {
                _uiState.update {
                    it.copy(
                        showBottomSheet = true,
                        selectedContact = event.contact,
                        selectedTimeInSeconds = 0 // Reset time
                    )
                }
            }
            is PrepareCallEvent.HideSheet -> {
                _uiState.update {
                    it.copy(
                        showBottomSheet = false
                        // Optionally clear selectedContact: it.copy(selectedContact = null)
                        // This depends on whether you want the manager to hold the contact
                        // even when the sheet is not visible from its own state.
                    )
                }
            }
            is PrepareCallEvent.SelectTime -> {
                _uiState.update { it.copy(selectedTimeInSeconds = event.timeInSeconds) }
            }
            is PrepareCallEvent.StartCall -> {
                val contactToCall = _uiState.value.selectedContact
                val delay = _uiState.value.selectedTimeInSeconds

                if (contactToCall != null) {
                    managerScope.launch {
                        // 1. Trigger the fake call
                        delay(delay * 1000L)
                        fakeCallManager.triggerFakeCall(
                            callerName = contactToCall.name,
                            callerNumber = contactToCall.number,
                            callerAvatarUrl = null, // Or get from contact if available
                        )

                        // 2. Add to call history
                        callHistoryRepository.addCallToHistory(contactToCall)

                        // 3. Update state to hide sheet (or emit one-time event)
                        _uiState.update { it.copy(showBottomSheet = false) }
                        // Alternatively, if you want the screen to react more explicitly:
                        // _oneTimeEvent.emit(PrepareCallOneTimeEvent.CallStartedAndDismiss)
                    }
                }
            }
        }
    }

    // getSelectedDelayInSeconds() is no longer needed externally if StartCall is handled internally
    // but can be kept if useful for UI display or other logic.

    fun clear() {
        managerScope.cancel()
    }
}

// Optional: For events that the UI should react to once (like dismissing the sheet after call)
// sealed class PrepareCallOneTimeEvent {
//    object CallStartedAndDismiss : PrepareCallOneTimeEvent()
// }