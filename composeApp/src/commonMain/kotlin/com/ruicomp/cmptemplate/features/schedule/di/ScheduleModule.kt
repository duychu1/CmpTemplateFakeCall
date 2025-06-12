package com.ruicomp.cmptemplate.features.schedule.di

import com.ruicomp.cmptemplate.features.schedule.presentation.ScheduleCallViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scheduleModule = module {
    viewModelOf(::ScheduleCallViewModel)
} 