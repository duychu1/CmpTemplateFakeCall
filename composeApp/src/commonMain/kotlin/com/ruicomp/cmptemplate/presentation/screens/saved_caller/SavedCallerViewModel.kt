package com.ruicomp.cmptemplate.presentation.screens.saved_caller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.domain.usecases.AddCaller
import com.ruicomp.cmptemplate.domain.usecases.DeleteCaller
import com.ruicomp.cmptemplate.domain.usecases.GetCallers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedCallerViewModel(
    private val getCallers: GetCallers,
    private val addCaller: AddCaller,
    private val deleteCaller: DeleteCaller
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedCallerState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCallers()
    }

    private fun loadCallers() {
        getCallers().onEach { callers ->
            _uiState.update { it.copy(callers = callers) }
        }.launchIn(viewModelScope)
    }

    fun onAddCaller(name: String, number: String) {
        viewModelScope.launch {
            addCaller(name, number)
        }
    }

    fun onDeleteCaller(id: Long) {
        viewModelScope.launch {
            deleteCaller(id)
        }
    }
} 