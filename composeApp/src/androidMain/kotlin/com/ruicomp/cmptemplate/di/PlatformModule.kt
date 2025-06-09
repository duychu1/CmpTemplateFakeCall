package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.data.local.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(get()) }
} 