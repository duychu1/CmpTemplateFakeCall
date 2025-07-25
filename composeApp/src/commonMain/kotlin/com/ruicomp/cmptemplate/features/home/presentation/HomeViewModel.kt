package com.ruicomp.cmptemplate.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.datastore.DataStoreKeys
import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionManager
import com.ruicomp.cmptemplate.core.permissions.phoneaccount.PhoneAccountPermissionManager
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    val basePermissionManager: BasePermissionManager,
    val phoneAccountPermissionManager: PhoneAccountPermissionManager,
    private val dataStorePreferences: DataStorePreferences,
    private val callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    companion object {
        const val READ_PHONE_NUMBERS_PERMISSION = "android.permission.READ_PHONE_NUMBERS"
    }

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val name = dataStorePreferences.getString(DataStoreKeys.CONTACT_NAME)
            val number = dataStorePreferences.getString(DataStoreKeys.CONTACT_NUMBER)
            val selectedDelay = dataStorePreferences.getInt(DataStoreKeys.SELECTED_DELAY)

            _uiState.update {
                it.copy(
                    contact = if (name != null && number != null) it.contact.copy(name = name, number = number) else it.contact,
                    nameTmp = name ?: it.contact.name,
                    numberTmp = number ?: it.contact.number,
                    selectedDelaySeconds = selectedDelay ?: it.selectedDelaySeconds // Use loaded delay or default
                )
            }
        }
        basePermissionManager.initialize(READ_PHONE_NUMBERS_PERMISSION)
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
            is HomeEvent.DelayItemSelected -> {
                _uiState.update { it.copy(selectedDelaySeconds = event.delay) }
                viewModelScope.launch {
                    dataStorePreferences.saveInt(DataStoreKeys.SELECTED_DELAY, event.delay)
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
            val contactToCall = _uiState.value.contact
            val delayMillis = _uiState.value.selectedDelaySeconds * 1000L

            phoneAccountPermissionManager.triggerFakeCall(
                callerName = contactToCall.name,
                callerNumber = contactToCall.number,
                callerAvatarUrl = null, // Or get from contact if available
                delayMillis = delayMillis
            )

            // 2. Add to call history
            callHistoryRepository.addCallToHistory(contactToCall)
        }
    }
}
