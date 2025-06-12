package com.ruicomp.cmptemplate.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

private const val DATASTORE_FILE_NAME = "app_settings.preferences_pb"

actual class DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        return createDesktopDataStore()
    }
}

private fun createDesktopDataStore(): DataStore<Preferences> {
    val appDataDir = File(System.getProperty("user.home"), ".my_app_name")
    if (!appDataDir.exists()) {
        appDataDir.mkdirs() // Ensure the directory exists
    }
    val dataStoreFile = File(appDataDir, DATASTORE_FILE_NAME)

    return PreferenceDataStoreFactory.createWithPath( // Using createWithPath for Okio
        corruptionHandler = null, // Optional: Add a corruption handler
        migrations = emptyList(), // Optional: Add migrations
        produceFile = { dataStoreFile.absolutePath.toPath() }
    )
}