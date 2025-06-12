package com.ruicomp.cmptemplate.features.settings.di

import com.ruicomp.cmptemplate.features.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
