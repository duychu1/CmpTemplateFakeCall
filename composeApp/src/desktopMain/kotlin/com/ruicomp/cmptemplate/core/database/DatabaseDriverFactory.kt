package com.ruicomp.cmptemplate.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ruicomp.cmptemplate.database.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:caller.db")
        if (!File("caller.db").exists()) {
            AppDatabase.Schema.create(driver)
        }
        return driver
    }
} 