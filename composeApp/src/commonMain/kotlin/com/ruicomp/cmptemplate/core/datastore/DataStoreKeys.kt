package com.ruicomp.cmptemplate.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
//    val EXAMPLE_STRING = stringPreferencesKey("example_string")
//    val EXAMPLE_INT = intPreferencesKey("example_int")
//    val EXAMPLE_BOOLEAN = booleanPreferencesKey("example_boolean")
//    val EXAMPLE_LONG = longPreferencesKey("example_long")
//    val EXAMPLE_DOUBLE = doublePreferencesKey("example_double")
//    val EXAMPLE_FLOAT = floatPreferencesKey("example_float")

    val LANGUAGE_STRING = stringPreferencesKey("language_string")
    
    val IS_ONBOARD_COMPLETE = booleanPreferencesKey("is_onboard_complete")
    val IS_PERMISSION_COMPLETE = booleanPreferencesKey("is_permission_complete")

    val CONTACT_NAME = stringPreferencesKey("contact_name")
    val CONTACT_NUMBER = stringPreferencesKey("contact_number")
    val SELECTED_DELAY = intPreferencesKey("selected_delay")
    

}

