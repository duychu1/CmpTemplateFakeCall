package com.ruicomp.cmptemplate.core.permissions.phoneaccount

import com.ruicomp.cmptemplate.IFakeCallManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PhoneAccountPermissionManager(
    private val fakeCallManager: IFakeCallManager,
) {

    private val _permissionState = MutableStateFlow(PhoneAccountPermissionState())
    val permissionState: StateFlow<PhoneAccountPermissionState> = _permissionState

    fun onEvent(event: PhoneAccountPermissionEvent) {
        when (event) {
            is PhoneAccountPermissionEvent.AgreeRationalPermissionDialogClicked -> {
                _permissionState.update { it.copy(showPhoneAccountRationaleSetting = false) }
                // Trigger permission request here if needed
            }
            is PhoneAccountPermissionEvent.CancelRationalPermissionDialogClicked -> {
                _permissionState.update { it.copy(showPhoneAccountRationaleSetting = false) }
            }
            is PhoneAccountPermissionEvent.RationalPermissionDialogDismissed -> {
                _permissionState.update { it.copy(showPhoneAccountRationaleSetting = false) }
            }
        }
    }

    fun showRationaleDialog() {
        _permissionState.update { it.copy(showPhoneAccountRationaleSetting = true) }
    }

    fun requestFakeCallPermission() {
        fakeCallManager.requestEnablePhoneAccount()
    }

    fun checkAndShowRational(): Boolean {
        if (!fakeCallManager.isPhonePermissionGranted()) {
            _permissionState.update { it.copy(showPhoneAccountRationaleSetting = true) }
            return true
        }
        return false
    }

    fun triggerFakeCall() {
        fakeCallManager.triggerFakeCall(
            callerName = "TestName",
            callerNumber = "01234567890",
            callerAvatarUrl = null
        )
    }

}
