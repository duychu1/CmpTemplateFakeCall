package com.ruicomp.cmptemplate.core.utils

import platform.Foundation.NSArray
import platform.Foundation.NSUserDefaults
import platform.Foundation.arrayWithObjects

actual class Localization {
    actual fun applyLanguage(languageCode: String) {
        // Get standard UserDefaults
        val defaults = NSUserDefaults.standardUserDefaults

        // Set the desired language code as the first preferred language.
        // iOS uses this array to determine language fallbacks.
        val languagesArray = NSArray.arrayWithObjects(languageCode, null)
        defaults.setObject(languagesArray, forKey = "AppleLanguages")

        // Synchronize UserDefaults to ensure the change is written to disk.
        // While often done automatically, explicit synchronization can be good practice.
        defaults.synchronize()

        // IMPORTANT:
        // For the language change to be visually applied immediately throughout the app
        // without a restart, the UI needs to be reloaded/recomposed.
        // This might involve:
        // 1. Posting a notification (e.g., via NSNotificationCenter) that your UI observers
        //    can listen to and then trigger a recomposition or reload of views.
        // 2. If using Compose Multiplatform with a UIViewController, you might need to
        //    recreate or significantly update the view controller hosting your Compose UI.
        // 3. In some scenarios, apps prompt the user to restart for the change to take full effect.
        //
        // This function sets the preference. The UI update mechanism is separate and
        // depends on your app's architecture.
    }
}
