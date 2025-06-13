package com.ruicomp.cmptemplate.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import com.ruicomp.cmptemplate.IFakeCallManager
import com.ruicomp.cmptemplate.core.database.DatabaseDriverFactory
import com.ruicomp.cmptemplate.core.datastore.DataStoreFactory
import com.ruicomp.cmptemplate.features.fakecall.ActualFakeCallManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    //sqldelight
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }

    single<DataStore<Preferences>> {
        DataStoreFactory(androidContext()).createDataStore()
    }
    
    single<IFakeCallManager> { ActualFakeCallManager(androidContext()) }
}

