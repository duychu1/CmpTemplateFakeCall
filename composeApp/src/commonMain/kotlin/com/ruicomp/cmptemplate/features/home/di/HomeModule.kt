package com.ruicomp.cmptemplate.features.home.di

import com.ruicomp.cmptemplate.features.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
} 