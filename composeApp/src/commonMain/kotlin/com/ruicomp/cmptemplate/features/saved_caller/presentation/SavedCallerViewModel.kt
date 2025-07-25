package com.ruicomp.cmptemplate.features.saved_caller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionManager
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallEvent
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallManager
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedCallerViewModel(
    private val callerRepository: CallerRepository,
    val prepareCallManager: PrepareCallManager,
    val basePermissionManager: BasePermissionManager // Added
) : ViewModel() {

    companion object { // Added
        const val READ_CONTACTS_PERMISSION = "android.permission.READ_CONTACTS" // Added
    }

    private val _uiState = MutableStateFlow(SavedCallerState())
    val uiState = _uiState.asStateFlow()

    val uiBasePermissionState = basePermissionManager.uiBasePermissionState // Added

    init {
        loadContacts()
        basePermissionManager.initialize(READ_CONTACTS_PERMISSION)
    }

    private fun loadContacts() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        callerRepository.getContacts().onEach { callers ->
            _uiState.update {
                it.copy(
                    contacts = callers,
                    isLoading = false,
                    error = null
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SavedCallerEvent) {
        when (event) {
            is SavedCallerEvent.LoadContacts -> {
                loadContacts()
            }
            is SavedCallerEvent.DeleteContact -> {
                viewModelScope.launch {
//                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        callerRepository.deleteCaller(event.id)
                        _uiState.update { it.copy(isLoading = false) }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            }
            is SavedCallerEvent.SelectContactForCall -> {
                _uiState.update { it.copy(selectedContactForCall = event.contact) }
                onEvent(SavedCallerEvent.TriggerShowBottomSheet)
            }
            is SavedCallerEvent.ShowAddContactDialog -> {
                _uiState.update { it.copy(showAddContactDialog = event.show) }
                if (!event.show) { // Clear fields when dialog is dismissed
                    _uiState.update { it.copy(addContactName = "", addContactNumber = "") }
                }
            }
            is SavedCallerEvent.UpdateAddContactName -> { // New
                _uiState.update { it.copy(addContactName = event.name) }
            }
            is SavedCallerEvent.UpdateAddContactNumber -> { // New
                _uiState.update { it.copy(addContactNumber = event.number) }
            }
            is SavedCallerEvent.AddContact -> { // Changed: Now uses name and number from uiState
                viewModelScope.launch {
//                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        val name = _uiState.value.addContactName
                        val number = _uiState.value.addContactNumber
                        if (name.isNotBlank() && number.isNotBlank()) {
                            callerRepository.insertCaller(name, number)
                            _uiState.update { it.copy(isLoading = false, addContactName = "", addContactNumber = "") } // Clear fields
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Name and number cannot be empty.") } // Or handle specific error
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            }
            is SavedCallerEvent.TriggerShowBottomSheet -> {
                _uiState.value.selectedContactForCall?.let { contact ->
                    prepareCallManager.onEvent(PrepareCallEvent.ShowSheet(contact))
                }
            }
            is SavedCallerEvent.ImportContactsFromSystemClicked -> {
                // First, check/request permission. If granted, trigger the picker launch.
                if (!basePermissionManager.checkAndShowPermissionAwareness(READ_CONTACTS_PERMISSION)) {
                    // Permission is already granted or no rationale needed, proceed to trigger picker
                    _uiState.update { it.copy(triggerContactPickerLaunch = true) }
                }
            }
            is SavedCallerEvent.ContactPicked -> {
                viewModelScope.launch {
                    try {
                        callerRepository.insertCaller(
                            event.name,
                            event.number
                        )
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
                _uiState.update { it.copy(triggerContactPickerLaunch = false) } // Reset trigger
            }
            is SavedCallerEvent.ResetContactPickerLaunchTrigger -> {
                _uiState.update { it.copy(triggerContactPickerLaunch = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        prepareCallManager.clear()
    }

}