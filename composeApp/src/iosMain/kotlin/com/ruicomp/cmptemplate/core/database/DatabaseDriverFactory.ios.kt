package com.ruicomp.cmptemplate.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ruicomp.cmptemplate.database.AppDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "app.db") // Use NativeSqliteDriver for iOS
    }
}