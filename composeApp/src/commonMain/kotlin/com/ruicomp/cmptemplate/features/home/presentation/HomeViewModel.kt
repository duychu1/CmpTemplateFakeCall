package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.permissions.presentation.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fakeCallManager: IFakeCallManager,
) : ViewModel() {

    companion object {
        const val READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS"
    }

    var isPhoneAccountEnable: Boolean = false
    var isPhonePermissionGranted: Boolean = false

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun onShowPermissionAwareChange(show: Boolean) {
        _uiState.update { it.copy(showPermissionAwarePhone = show) }
    }

    /**
     * Event from the UI: The initial check for the permission status is complete.
     */
    fun onPermissionStatusChecked(status: PermissionStatus) {
        when (status) {
            is PermissionStatus.Granted -> {
                handlePhonePermissionGranted()
                handleCallNow()
            }
            is PermissionStatus.NotApplicable -> { }
            is PermissionStatus.NotGranted -> { }
            is PermissionStatus.RationaleNeeded -> { }
            is PermissionStatus.PermanentlyDenied -> { }
        }
//        permissionHandler.onPermissionStatus(permission, status)
    }

    /**
     * Event from the UI: The user has responded to the permission request dialog.
     */
    fun onPermissionResult(status: PermissionStatus) {
        when (status) {
            is PermissionStatus.Granted -> { handlePhonePermissionGranted() }
            is PermissionStatus.NotApplicable -> { }
            is PermissionStatus.NotGranted -> { }
            is PermissionStatus.RationaleNeeded -> { }
            is PermissionStatus.PermanentlyDenied -> { }
        }
//        permissionHandler.onPermissionResult(status)
    }

    private fun handlePhonePermissionGranted() {
        isPhonePermissionGranted = true
        _uiState.update { it.copy(showPermissionAwarePhone = false) }
    }

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

    private fun checkPhoneAccountPermission() {
        isPhoneAccountEnable = fakeCallManager.isPhoneAccountEnable()
    }

    private fun requestFakeCallPermission() {
        fakeCallManager.requestPermission()
    }

    private fun handleCallNow() {
        if (!_uiState.value.showPermissionAwarePhone && !isPhonePermissionGranted) {
            _uiState.update { it.copy(showPermissionAwarePhone = true) }
            return
        }
        checkPhoneAccountPermission() // Re-check before attempting to call
        if (isPhoneAccountEnable) {
            triggerFakeCallFlow()
        } else {
            _uiState.update { it.copy(showPhoneAccountPermissionRationaleDialog = true) }
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