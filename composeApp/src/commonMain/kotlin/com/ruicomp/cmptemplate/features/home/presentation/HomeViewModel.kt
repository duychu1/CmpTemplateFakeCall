package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fakeCallManager: IFakeCallManager // Inject this (expect/actual)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
//        checkFakeCallPermission()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.CallNowClicked -> handleCallNow()
            HomeEvent.AgreeRationalPermissionDialogClicked -> {
                _uiState.update {
                    it.copy(
                        shouldShowPermissionRationaleDialog = false
                    )
                }
                requestFakeCallPermission()
            }

            HomeEvent.CancelRationalPermissionDialogClicked -> {
                _uiState.update {
                    it.copy(
                        shouldShowPermissionRationaleDialog = false
                    )
                }
            }

            HomeEvent.RationalPermissionDialogDismissed -> {
                _uiState.update { it.copy(shouldShowPermissionRationaleDialog = false) }
            }

        }
    }

    private fun checkFakeCallPermission() {
        val isGranted = fakeCallManager.isPermissionGranted()
        _uiState.update { it.copy(isMakeCallPermissionGranted = isGranted) }
    }

    private fun requestFakeCallPermission() {
        fakeCallManager.requestPermission()
    }

    private fun handleCallNow() {
        checkFakeCallPermission() // Re-check before attempting to call
//        requestFakeCallPermission()
        if (_uiState.value.isMakeCallPermissionGranted) {
            triggerFakeCallFlow()
        } else {
            _uiState.update { it.copy(shouldShowPermissionRationaleDialog = true) }
        }
    }

    private fun triggerFakeCallFlow() {
        // Placeholder for actual call logic
        // This will call the platform-specific implementation
        viewModelScope.launch {
            // You might want to navigate or show some UI feedback here
            fakeCallManager.triggerFakeCall(
                callerName = "Instant Caller", // Replace with actual data if needed
                callerNumber = "123-456-7890",
                callerAvatarUrl = null
            )
            // Example: _uiState.update { it.copy(callInitiated = true) }
        }
    }
}