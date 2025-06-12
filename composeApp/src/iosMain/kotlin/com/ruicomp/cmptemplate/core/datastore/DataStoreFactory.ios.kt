package com.ruicomp.cmptemplate.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private const val DATASTORE_FILE_NAME = "app_settings.preferences_pb"

actual class DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        return createIOSDataStore()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun createIOSDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath( // Use createWithPath for Okio Path
        corruptionHandler = null, // Optional: Add a corruption handler
        migrations = emptyList(), // Optional: Add migrations
        produceFile = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            val path = requireNotNull(documentDirectory?.path) { "Could not get documents directory path" }
            ("$path/$DATASTORE_FILE_NAME").toPath()
        }
    )
}