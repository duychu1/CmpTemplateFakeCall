package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.permissions.PermissionHandler
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionState
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fakeCallManager: IFakeCallManager,
    private val permissionHandler: PermissionHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    // Expose the permission state directly from the handler
    val uiPermissionState: StateFlow<PermissionState> = permissionHandler.permissionState

    /**
     * Event from the UI: The initial check for the permission status is complete.
     */
    fun onPermissionStatusChecked(permission: String, status: PermissionStatus) {
        permissionHandler.onPermissionStatus(permission, status)
    }

    /**
     * Event from the UI: The user has responded to the permission request dialog.
     */
    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        permissionHandler.onPermissionResult(isGranted, shouldShowRationale)
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
        val isGranted = fakeCallManager.isPhoneAccountEnable()
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
                callerName = "TestName", // Replace with actual data if needed
                callerNumber = "01234567890",
                callerAvatarUrl = null
            )
            // Example: _uiState.update { it.copy(callInitiated = true) }
        }
    }
}