package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fakeCallManager: IFakeCallManager,
) : BasePermissionViewModel() {

    companion object {
        const val READ_PHONE_NUMBERS_PERMISSION = "android.permission.READ_PHONE_NUMBERS"
    }

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.CallNowClicked -> handleCallNow()
            HomeEvent.AgreeRationalPermissionDialogClicked -> {
                _uiState.update {
                    it.copy(
                        showPhoneAccountPermissionRationaleDialog = false
                    )
                }
                requestFakeCallPermission()
            }

            HomeEvent.CancelRationalPermissionDialogClicked -> {
                _uiState.update {
                    it.copy(
                        showPhoneAccountPermissionRationaleDialog = false
                    )
                }
            }

            HomeEvent.RationalPermissionDialogDismissed -> {
                _uiState.update { it.copy(showPhoneAccountPermissionRationaleDialog = false) }
            }

        }
    }

    private fun requestFakeCallPermission() {
        fakeCallManager.requestEnablePhoneAccount()
    }

    private fun handleCallNow() {
        if (checkAndShowPermissionAwareness(READ_PHONE_NUMBERS_PERMISSION)){
            return
        }
        if (!fakeCallManager.isPhonePermissionGranted()) {
            _uiState.update { it.copy(showPhoneAccountPermissionRationaleDialog = true) }
            return
        }
        triggerFakeCallFlow()
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