package com.ruicomp.cmptemplate.core.utils

/**
 * Expected utility class for sharing content across platforms.
 */
expect class ShareUtils {

    fun shareApp(title: String? = null, text: String? = null, error: String? = null)

    fun rateApp()
}