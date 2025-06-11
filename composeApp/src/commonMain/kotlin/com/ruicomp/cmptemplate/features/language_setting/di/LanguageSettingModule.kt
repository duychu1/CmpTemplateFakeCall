package com.ruicomp.cmptemplate.features.language_setting.di

import com.ruicomp.cmptemplate.features.language_setting.presentation.LanguageSettingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val languageSettingModule = module {
//    single { LanguageDataSource() }
//    single<LanguageRepository> { LanguageRepositoryImpl(get()) }
    factoryOf(::LanguageSettingViewModel)
}
