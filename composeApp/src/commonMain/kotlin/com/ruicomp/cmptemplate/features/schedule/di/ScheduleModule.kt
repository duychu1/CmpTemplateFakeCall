package com.ruicomp.cmptemplate.features.schedule.di

import com.ruicomp.cmptemplate.features.schedule.presentation.ScheduleCallViewModel
import org.koin.dsl.module

val scheduleModule = module {
    factory { ScheduleCallViewModel(callHistoryRepository = get(), callerRepository = get()) }
} 