package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.data.local.CallerDataSource
import com.ruicomp.cmptemplate.data.local.DatabaseDriverFactory
import com.ruicomp.cmptemplate.data.repository.CallerRepositoryImpl
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.domain.repository.CallerRepository
import com.ruicomp.cmptemplate.presentation.screens.saved_caller.SavedCallerViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    single<AppDatabase> { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<CallerDataSource> { CallerDataSource(get()) }
    single<CallerRepository> { CallerRepositoryImpl(get()) }

    factory<SavedCallerViewModel> { SavedCallerViewModel(get()) }
}

expect val platformModule: Module 