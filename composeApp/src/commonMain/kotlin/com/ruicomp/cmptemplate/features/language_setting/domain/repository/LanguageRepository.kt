package com.ruicomp.cmptemplate.features.language_setting.domain.repository

import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    fun getLanguages(): Flow<List<Language>>
    suspend fun selectLanguage(code: String)
}