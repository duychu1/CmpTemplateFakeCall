package com.ruicomp.cmptemplate.di

import app.cash.sqldelight.db.SqlDriver
import com.ruicomp.cmptemplate.core.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> { DatabaseDriverFactory(get()).createDriver() }
} 