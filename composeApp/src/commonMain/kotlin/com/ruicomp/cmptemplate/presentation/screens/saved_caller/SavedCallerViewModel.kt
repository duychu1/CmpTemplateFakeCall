package com.ruicomp.cmptemplate.presentation.screens.saved_caller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.domain.models.Contact
import com.ruicomp.cmptemplate.domain.usecases.AddCallToHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedCallerViewModel(
    private val repository: CallerRepository,
    private val addCallToHistory: AddCallToHistory
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedContactsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        repository.getCallers().onEach { callers ->
            _uiState.update {
                it.copy(
                    contacts = callers.map { c -> Contact(id = c.id, name = c.name, number = c.number) },
                    isLoading = false,
                    error = null
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAddContact(name: String, number: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.insertCaller(name, number)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onDeleteContact(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteCaller(id)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCallContact(contact: Contact) {
        viewModelScope.launch {
            addCallToHistory(contact)
            // TODO: A fake call screen will be shown here
        }
    }
} 