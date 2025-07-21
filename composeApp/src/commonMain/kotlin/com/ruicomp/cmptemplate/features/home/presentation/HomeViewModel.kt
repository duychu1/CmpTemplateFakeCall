package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.datastore.DataStoreKeys
import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionManager
import com.ruicomp.cmptemplate.core.permissions.phoneaccount.PhoneAccountPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    val basePermissionManager: BasePermissionManager,
    val phoneAccountPermissionManager: PhoneAccountPermissionManager,
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    companion object {
        const val READ_PHONE_NUMBERS_PERMISSION = "android.permission.READ_PHONE_NUMBERS"
    }

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContactDetails()
    }

    private fun loadContactDetails() {
        viewModelScope.launch {
            val name = dataStorePreferences.getString(DataStoreKeys.CONTACT_NAME)
            val number = dataStorePreferences.getString(DataStoreKeys.CONTACT_NUMBER)
            if (name != null && number != null) {
                _uiState.update {
                    it.copy(
                        contact = it.contact.copy(name = name, number = number),
                        nameTmp = name, // Also update tmp fields if needed or manage dialog opening separately
                        numberTmp = number
                    )
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CallNowClicked -> handleCallNow()
            is HomeEvent.ShowTmpContactDialog -> {
                _uiState.update {
                    if (event.show) {
                        it.copy(
                            showInputContactDialog = true,
                            nameTmp = it.contact.name, // Load from current contact state
                            numberTmp = it.contact.number
                        )
                    } else {
                        it.copy(showInputContactDialog = false)
                    }
                }
            }
            is HomeEvent.UpdateTmpContactName -> {
                _uiState.update { it.copy(nameTmp = event.name) }
            }
            is HomeEvent.UpdateTmpContactNumber -> {
                _uiState.update { it.copy(numberTmp = event.number) }
            }
            is HomeEvent.UpdateContact -> {
                val newName = _uiState.value.nameTmp
                val newNumber = _uiState.value.numberTmp
                _uiState.update {
                    it.copy(
                        contact = it.contact.copy(name = newName, number = newNumber)
                    )
                }
                viewModelScope.launch {
                    dataStorePreferences.saveString(DataStoreKeys.CONTACT_NAME, newName)
                    dataStorePreferences.saveString(DataStoreKeys.CONTACT_NUMBER, newNumber)
                }
            }
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
