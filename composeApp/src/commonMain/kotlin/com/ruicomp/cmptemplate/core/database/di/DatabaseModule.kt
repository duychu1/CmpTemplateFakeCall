package com.ruicomp.cmptemplate.core.database.di

import com.ruicomp.cmptemplate.database.AppDatabase
import org.koin.dsl.module

val coreModule = module {
    single { AppDatabase(driver = get()) }

    // DataSources

}