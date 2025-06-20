package com.ruicomp.cmptemplate.core.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

actual class ShareUtils(private val context: Context) {
    actual fun shareApp(title: String?, text: String?, error: String?) {
        val mTitle = title ?: "Share App"
        val mError = error ?: "Error sharing app"
        val playStoreUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, playStoreUrl)
            }
            context.applicationContext.startActivity(Intent.createChooser(intent, mTitle).apply {
                // The chooser intent also needs this flag if the base context isn't an Activity
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Toast.makeText(context, mError, Toast.LENGTH_SHORT).show()
        }
    }

    actual fun rateApp() {
        val mError = "Can not open Play Store"
        val appPackageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle any other exceptions, e.g., if the Play Store is not installed
            Toast.makeText(context, mError, Toast.LENGTH_SHORT).show()
        }
    }
}