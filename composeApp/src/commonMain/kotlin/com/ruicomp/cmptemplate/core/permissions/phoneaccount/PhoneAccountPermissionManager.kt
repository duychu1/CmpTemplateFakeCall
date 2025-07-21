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
                showRationaleDialog(false)
                // Trigger permission request here if needed
                requestFakeCallPermission()
            }
            is PhoneAccountPermissionEvent.CancelRationalPermissionDialogClicked -> {
                showRationaleDialog(false)
            }
            is PhoneAccountPermissionEvent.RationalPermissionDialogDismissed -> {
                showRationaleDialog(false)
            }
        }
    }

    fun showRationaleDialog(isShow: Boolean) {
        _permissionState.update { it.copy(showPhoneAccountRationaleSetting = isShow) }
    }

    fun requestFakeCallPermission() {
        fakeCallManager.requestEnablePhoneAccount()
    }

    fun checkAndShowRational(): Boolean {
        if (!fakeCallManager.isPhoneAccountEnable()) {
            showRationaleDialog(true)
            return true
        }
        return false
    }

    fun triggerFakeCall(
        callerName: String,
        callerNumber: String,
        callerAvatarUrl: String?,
        delayMillis: Long = 1000L
    ) {
    fakeCallManager.triggerFakeCall(
            callerName = callerName,
            callerNumber = callerNumber,
            callerAvatarUrl = null,
            delayMillis = delayMillis
        )
    }

}
