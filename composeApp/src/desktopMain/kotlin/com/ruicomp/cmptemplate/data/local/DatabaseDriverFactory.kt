package com.ruicomp.cmptemplate.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ruicomp.cmptemplate.database.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "app.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        AppDatabase.Schema.create(driver)
        return driver
    }
} 