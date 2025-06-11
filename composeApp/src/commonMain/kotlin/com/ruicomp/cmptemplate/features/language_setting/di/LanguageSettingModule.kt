package com.ruicomp.cmptemplate.features.language_setting.di

import com.ruicomp.cmptemplate.features.language_setting.data.datasource.LanguageDataSource
import com.ruicomp.cmptemplate.features.language_setting.data.repository.LanguageRepositoryImpl
import com.ruicomp.cmptemplate.features.language_setting.domain.repository.LanguageRepository
import com.ruicomp.cmptemplate.features.language_setting.presentation.LanguageSettingViewModel
import org.koin.dsl.module

val languageSettingModule = module {
    single { LanguageDataSource() }
    single<LanguageRepository> { LanguageRepositoryImpl(get()) }
    factory { LanguageSettingViewModel(get()) }
}
