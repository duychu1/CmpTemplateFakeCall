package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.data.local.CallHistoryDataSource
import com.ruicomp.cmptemplate.data.local.CallerDataSource
import com.ruicomp.cmptemplate.data.local.DatabaseDriverFactory
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
    single<AppDatabase> { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }

    // Caller
    single<CallerDataSource> { CallerDataSource(get()) }
    single<CallerRepository> { CallerRepositoryImpl(get()) }

    // Call History
    single<CallHistoryDataSource> { CallHistoryDataSource(get()) }
    single<CallHistoryRepository> { CallHistoryRepositoryImpl(get()) }
    factory<GetCallHistory> { GetCallHistory(get()) }
    factory<AddCallToHistory> { AddCallToHistory(get()) }

    // ViewModels
    factory<SavedCallerViewModel> { SavedCallerViewModel(get(), get()) }
    factory<CallHistoryViewModel> { CallHistoryViewModel(get()) }
}

expect val platformModule: Module 