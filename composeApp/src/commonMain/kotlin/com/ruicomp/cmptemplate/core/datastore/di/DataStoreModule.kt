package com.ruicomp.cmptemplate.core.datastore.di

import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import org.koin.dsl.module

val dataStoreModule = module {
    single { DataStorePreferences(get()) }
}