package com.ruicomp.cmptemplate.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import com.ruicomp.cmptemplate.core.database.DatabaseDriverFactory
import com.ruicomp.cmptemplate.core.datastore.DataStoreFactory
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File


actual val platformModule: Module = module {
    //sqldelight
    single<SqlDriver> { DatabaseDriverFactory().createDriver() }

    //datastore
    single<DataStore<Preferences>> {
        DataStoreFactory().createDataStore()
    }
}

