package com.ruicomp.cmptemplate.features.schedule.di

import com.ruicomp.cmptemplate.features.schedule.data.datasource.ScheduledCalledDataSource
import com.ruicomp.cmptemplate.features.schedule.data.repository.ScheduledCalledRepositoryImpl
import com.ruicomp.cmptemplate.features.schedule.domain.repository.ScheduledCalledRepository
import com.ruicomp.cmptemplate.features.schedule.presentation.ScheduleCallViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scheduleModule = module {
    single { ScheduledCalledDataSource(get()) }
    single<ScheduledCalledRepository> { ScheduledCalledRepositoryImpl(get()) }
    viewModelOf(::ScheduleCallViewModel)
} 