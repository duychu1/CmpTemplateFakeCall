package com.ruicomp.cmptemplate.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import com.ruicomp.cmptemplate.core.database.DatabaseDriverFactory
import com.ruicomp.cmptemplate.core.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    //sqldelight
    single<SqlDriver> { DatabaseDriverFactory(get()).createDriver() }

    single<DataStore<Preferences>> {
        DataStoreFactory().createAndroidDataStore(get())
    }
}

