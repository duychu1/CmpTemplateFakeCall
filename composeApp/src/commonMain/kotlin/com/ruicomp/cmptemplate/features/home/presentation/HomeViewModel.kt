package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionManager
import com.ruicomp.cmptemplate.core.permissions.phoneaccount.PhoneAccountPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    val basePermissionManager: BasePermissionManager,
    val phoneAccountPermissionManager: PhoneAccountPermissionManager
) : ViewModel() {

    companion object {
        const val READ_PHONE_NUMBERS_PERMISSION = "android.permission.READ_PHONE_NUMBERS"
    }

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.CallNowClicked -> handleCallNow()
        }
    }

    private fun handleCallNow() {
        if (basePermissionManager.checkAndShowPermissionAwareness(READ_PHONE_NUMBERS_PERMISSION)){
            return
        }
        if (phoneAccountPermissionManager.checkAndShowRational()) {
            return
        }
        viewModelScope.launch {
            phoneAccountPermissionManager.triggerFakeCall()
        }
    }
}
