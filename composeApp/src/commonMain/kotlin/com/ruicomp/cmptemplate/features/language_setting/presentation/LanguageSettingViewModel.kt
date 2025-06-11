package com.ruicomp.cmptemplate.features.language_setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.datastore.DataStoreKeys
import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.features.language_setting.data.provider.LanguageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageSettingViewModel(
    private val dataStorePreferences: DataStorePreferences,
) : ViewModel() {

    private var currentLanguageCode: String? = null
    private val defaultLanguageCode = LanguageProvider.defaultLanguageCode
    private val listLanguage = LanguageProvider.languages

    private val _uiState = MutableStateFlow(LanguageSettingState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadLanguages()
        }
    }

    suspend fun loadLanguages() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        println("Loading languages...")
        dataStorePreferences.getAsFlow(DataStoreKeys.LANGUAGE_STRING)
            .collectLatest {
                currentLanguageCode = it ?: defaultLanguageCode
                println("Current language code: $currentLanguageCode")
                setSelectionLanguage(currentLanguageCode)
            }
    }

    fun onEvent(event: LanguageSettingEvent) {
        when (event) {
            is LanguageSettingEvent.SelectLanguage -> {
                viewModelScope.launch {
                    saveLanguage(event.code)
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
        _uiState.update { it.copy(languages = finalList, isLoading = false, error = null) }
    }
    
    private fun saveLanguage(code: String) {
        viewModelScope.launch {
            dataStorePreferences.saveString(DataStoreKeys.LANGUAGE_STRING, code)
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("LanguageSettingViewModel cleared")
    }
}