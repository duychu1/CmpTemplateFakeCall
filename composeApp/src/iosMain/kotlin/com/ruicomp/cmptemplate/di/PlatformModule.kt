package com.ruicomp.cmptemplate.di

import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.toPath
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ruicomp.cmptemplate.core.datastore.DataStoreFactory
import com.ruicomp.cmptemplate.database.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


actual val platformModule: Module = module {
    //sqldelight
    single<SqlDriver> {
        NativeSqliteDriver(AppDatabase.Schema, "app.db") // Use NativeSqliteDriver for iOS
    }

    //datastore
    single<DataStore<Preferences>> {
        DataStoreFactory().createDataStore()
    }
}

