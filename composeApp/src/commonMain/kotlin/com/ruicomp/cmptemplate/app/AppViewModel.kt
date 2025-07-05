package com.ruicomp.cmptemplate.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruicomp.cmptemplate.core.datastore.DataStoreKeys
import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.core.utils.Localization
import com.ruicomp.cmptemplate.features.language_setting.data.provider.LanguageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppViewModel(
    private val dataStore: DataStorePreferences,
    private val localization: Localization
) : ViewModel() {

    private val _isLocalizationInitialized = MutableStateFlow(false)
    val isLocalizationInitialized: StateFlow<Boolean> = _isLocalizationInitialized.asStateFlow()

    init {
        initializeLocalization()
    }

    private fun initializeLocalization() {
        viewModelScope.launch {
            dataStore.getString(DataStoreKeys.LANGUAGE_STRING)
                ?.let { localization.applyLanguage(it) }
            _isLocalizationInitialized.value = true
        }
    }
}
