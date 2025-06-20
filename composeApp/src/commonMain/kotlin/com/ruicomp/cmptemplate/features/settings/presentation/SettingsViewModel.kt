package com.ruicomp.cmptemplate.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.datastore.DataStoreKeys
import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.core.utils.ShareUtils
import com.ruicomp.cmptemplate.features.language_setting.data.provider.LanguageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStorePreferences: DataStorePreferences,
    private val shareUtils: ShareUtils,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    init {
        onEvent(SettingsEvent.LoadSettings)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.RateUsClick -> shareUtils.rateApp()
            is SettingsEvent.ShareAppClick -> shareUtils.shareApp()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            dataStorePreferences.getAsFlow(DataStoreKeys.LANGUAGE_STRING)
                .collectLatest { code ->
                    val language = LanguageProvider.languages.find { it.code == code }?.name
                        ?: LanguageProvider.languages.find { it.code == LanguageProvider.defaultLanguageCode }?.name
                        ?: ""
                    _uiState.value = _uiState.value.copy(language = language, isLoading = false)
                }
        }
    }
}

