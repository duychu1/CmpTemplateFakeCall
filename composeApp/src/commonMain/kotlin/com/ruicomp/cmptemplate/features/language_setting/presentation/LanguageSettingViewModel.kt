package com.ruicomp.cmptemplate.features.language_setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.features.language_setting.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageSettingViewModel(
    private val repository: LanguageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSettingState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        repository.getLanguages().onEach { languages ->
            _uiState.update {
                it.copy(
                    languages = languages,
                    isLoading = false,
                    error = null
                )
            }
        }.launchIn(viewModelScope)
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            repository.selectLanguage(code)
        }
    }
}
