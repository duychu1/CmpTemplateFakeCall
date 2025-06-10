package com.ruicomp.cmptemplate.features.call_history.di

import com.ruicomp.cmptemplate.features.call_history.presentation.CallHistoryViewModel
import org.koin.dsl.module

val callHistoryModule = module {
    factory { CallHistoryViewModel(callHistoryRepository = get()) }
}
