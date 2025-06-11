package com.ruicomp.cmptemplate.features.language_setting.data.datasource

import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LanguageDataSource {
    private val _languages = MutableStateFlow(
        listOf(
            Language("en", "English", 0, isSelected = true),
            Language("es", "Spanish", 0),
            Language("fr", "French", 0)
        )
    )
    val languages: StateFlow<List<Language>> = _languages

    fun selectLanguage(code: String) {
        _languages.update { list ->
            list.map { it.copy(isSelected = it.code == code) }
        }
    }
}
