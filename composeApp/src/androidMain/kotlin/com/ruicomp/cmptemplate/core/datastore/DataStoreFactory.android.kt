package com.ruicomp.cmptemplate.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences

private const val DATASTORE_FILE_NAME = "app_settings.preferences_pb"

actual class DataStoreFactory {
    internal fun createAndroidDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.filesDir.resolve(DATASTORE_FILE_NAME)
        }
    }
}