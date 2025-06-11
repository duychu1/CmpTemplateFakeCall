package com.ruicomp.cmptemplate.features.language_setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.features.language_setting.data.provider.LanguageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageSettingViewModel(
) : ViewModel() {

    private var currentLanguageCode : String? = null
    private val defaultLanguageCode = LanguageProvider.defaultLanguageCode
    private val listLanguage = LanguageProvider.languages
    
    private val _uiState = MutableStateFlow(LanguageSettingState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        setSelectionLanguage(currentLanguageCode)
        _uiState.update {
            it.copy(
                languages = listLanguage,
                isLoading = false,
                error = null
            )
        }
    }

    fun onEvent(event: LanguageSettingEvent) {
        when (event) {
            is LanguageSettingEvent.SelectLanguage -> {
                viewModelScope.launch {
                    setSelectionLanguage(event.code)
                }
            }
        }
    }

    fun setSelectionLanguage(code: String?) {
        val selectedCode = code ?: defaultLanguageCode
        val updatedList = listLanguage.map {
            it.copy(isSelected = it.code == selectedCode)
        }
        val found = updatedList.any { it.isSelected }
        val finalList = if (found) {
            updatedList
        } else {
            // If not found, select the default language
            updatedList.map { lang -> lang.copy(isSelected = lang.code == defaultLanguageCode) }
        }
        currentLanguageCode = finalList.find { it.isSelected }?.code
        _uiState.update { it.copy(languages = finalList) }
    }
}