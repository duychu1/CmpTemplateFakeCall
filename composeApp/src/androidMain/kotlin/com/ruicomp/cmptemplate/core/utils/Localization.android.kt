package com.ruicomp.cmptemplate.core.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

actual class Localization(private val context: Context) {
    actual fun applyLanguage(languageCode: String) {
        // Ensure languageCode is in BCP 47 format (e.g., "en-US", "it-IT")
        // by replacing underscores with hyphens.
        val bcp47LanguageTag = languageCode.replace("_", "-")
        val locale = Locale.forLanguageTag(bcp47LanguageTag)
        
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        // Since your min API is 24+, we can directly use LocaleList.
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)

        // Create a new context with the updated configuration.
        // The original 'context' field in this class instance won't be updated by this,
        // but this is often used to get an updated context if needed immediately.
        context.createConfigurationContext(configuration)
        
        // Update the application's resources configuration.
        // resources.updateConfiguration is deprecated in API 33, but it's often still used
        // for broader compatibility and to ensure the current Resources object reflects the change.
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // IMPORTANT: After calling this function, the current Activity
        // usually needs to be recreated for the language changes to
        // take full effect visually across the entire UI.
        // Example: activity.recreate() in your Activity or from a composable via a callback.
    }
}
