package com.ruicomp.cmptemplate.features.saved_caller.di

import com.ruicomp.cmptemplate.features.saved_caller.data.datasource.CallerDataSource
import com.ruicomp.cmptemplate.features.saved_caller.data.repository.CallerRepositoryImpl
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.features.saved_caller.presentation.SavedCallerViewModel
import org.koin.dsl.module

val savedCallerModule = module {
    single { CallerDataSource(database = get()) }
    single<CallerRepository> { CallerRepositoryImpl(callerDataSource = get()) }
    factory { SavedCallerViewModel(callerRepository = get(), callHistoryRepository = get()) }
} 