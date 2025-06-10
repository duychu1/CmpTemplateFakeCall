package com.ruicomp.cmptemplate.core.di

import com.ruicomp.cmptemplate.core.data.datasource.CallHistoryDataSource
import com.ruicomp.cmptemplate.core.data.datasource.CallerDataSource
import com.ruicomp.cmptemplate.core.data.repository.CallHistoryRepositoryImpl
import com.ruicomp.cmptemplate.core.data.repository.CallerRepositoryImpl
import com.ruicomp.cmptemplate.core.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import org.koin.dsl.module

val coreModule = module {
    single { AppDatabase(driver = get()) }

    // DataSources
    single { CallerDataSource(database = get()) }
    single { CallHistoryDataSource(database = get()) }

    // Repositories
    single<CallerRepository> { CallerRepositoryImpl(callerDataSource = get()) }
    single<CallHistoryRepository> { CallHistoryRepositoryImpl(dataSource = get()) }
} 