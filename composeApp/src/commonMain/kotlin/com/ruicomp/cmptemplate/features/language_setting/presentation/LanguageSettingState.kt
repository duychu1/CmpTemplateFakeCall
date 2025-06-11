package com.ruicomp.cmptemplate.features.language_setting.presentation

import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language

data class LanguageSettingState(
    val languages: List<Language> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
