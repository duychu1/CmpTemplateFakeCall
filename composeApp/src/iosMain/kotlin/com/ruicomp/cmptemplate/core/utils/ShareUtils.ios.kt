package com.ruicomp.cmptemplate.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareUtils {
    actual fun shareApp(title: String?, text: String?, error: String?) {
        val activityItems = mutableListOf<Any>()
        if (!text.isNullOrBlank()) {
            activityItems.add(text)
        }
        if (!title.isNullOrBlank()) {
            activityItems.add(title)
        }
        if (activityItems.isEmpty()) {
            // Optionally handle error
            return
        }
        val activityViewController = UIActivityViewController(
            activityItems = activityItems as List<*>,
            applicationActivities = null
        )
        val window = UIApplication.sharedApplication.keyWindow
        val rootVC = window?.rootViewController
        rootVC?.presentViewController(activityViewController, animated = true, completion = null)
    }

    actual fun rateApp() {
        // Replace YOUR_APP_ID with your actual App Store app id
        val appId = "YOUR_APP_ID" // Placeholder, needs to be replaced
        val urlString = "itms-apps://itunes.apple.com/app/id$appId?action=write-review"
        val url = NSURL(string = urlString) // Create an NSURL object

        if (UIApplication.sharedApplication.canOpenURL(url)) { // Check if the URL can be opened
            UIApplication.sharedApplication.openURL(url)
        } else {
            // Fallback or error logging if the URL is invalid or cannot be opened
            // For example, you could try opening the App Store page without the write-review action
            val appStoreUrlString = "itms-apps://itunes.apple.com/app/id$appId"
            val appStoreUrl = NSURL(string = appStoreUrlString)
            if (UIApplication.sharedApplication.canOpenURL(appStoreUrl)) {
                UIApplication.sharedApplication.openURL(appStoreUrl)
            } else {
                println("Error: Could not construct or open App Store URL for app ID: $appId")
                // As a last resort, you could try the web URL, though the itms-apps is preferred
                // val webUrlString = "https://apps.apple.com/app/id$appId"
                // ...
            }
        }
    }
}