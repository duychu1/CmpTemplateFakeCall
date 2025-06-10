package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.data.local.CallHistoryDataSource
import com.ruicomp.cmptemplate.data.local.CallerDataSource
import com.ruicomp.cmptemplate.data.repository.CallHistoryRepositoryImpl
import com.ruicomp.cmptemplate.data.repository.CallerRepositoryImpl
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.domain.repository.CallHistoryRepository
import com.ruicomp.cmptemplate.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.domain.usecases.AddCallToHistory
import com.ruicomp.cmptemplate.domain.usecases.GetCallHistory
import com.ruicomp.cmptemplate.presentation.screens.call_history.CallHistoryViewModel
import com.ruicomp.cmptemplate.presentation.screens.saved_caller.SavedCallerViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase(driver = get()) }

    // DataSources
    single { CallerDataSource(database = get()) }
    single { CallHistoryDataSource(database = get()) }

    // Repositories
    single<CallerRepository> { CallerRepositoryImpl(dataSource = get()) }
    single<CallHistoryRepository> { CallHistoryRepositoryImpl(dataSource = get()) }

    // Use cases
    factory { GetCallHistory(repository = get()) }
    factory { AddCallToHistory(repository = get()) }

    // ViewModels
    factory { SavedCallerViewModel(repository = get(), addCallToHistory = get()) }
    factory { CallHistoryViewModel(getCallHistory = get(), addCallToHistory = get()) }
}

expect val platformModule: Module 