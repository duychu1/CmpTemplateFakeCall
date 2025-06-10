package com.ruicomp.cmptemplate.features.saved_caller.di

import com.ruicomp.cmptemplate.features.saved_caller.presentation.SavedCallerViewModel
import org.koin.dsl.module

val savedCallerModule = module {
    factory { SavedCallerViewModel(callerRepository = get(), callHistoryRepository = get()) }
} 