package com.ruicomp.cmptemplate.features.settings.presentation

data class SettingsState(
    val language: String = "English",
    val isLoading: Boolean = false,
    val error: String? = null
)

