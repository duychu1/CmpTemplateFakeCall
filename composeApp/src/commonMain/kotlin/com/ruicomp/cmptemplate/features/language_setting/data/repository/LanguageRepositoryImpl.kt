package com.ruicomp.cmptemplate.features.language_setting.data.repository

import com.ruicomp.cmptemplate.features.language_setting.data.datasource.LanguageDataSource
import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language
import com.ruicomp.cmptemplate.features.language_setting.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.Flow

class LanguageRepositoryImpl(
    private val dataSource: LanguageDataSource
) : LanguageRepository {
    override fun getLanguages(): Flow<List<Language>> = dataSource.languages
    override suspend fun selectLanguage(code: String) {
        dataSource.selectLanguage(code)
    }
}
