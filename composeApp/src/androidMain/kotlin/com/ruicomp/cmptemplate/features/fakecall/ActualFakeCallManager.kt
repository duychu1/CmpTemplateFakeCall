package com.ruicomp.cmptemplate.features.fakecall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler // Added import
import android.os.Looper  // Added import
import com.ruicomp.cmptemplate.IFakeCallManager



/**
 * An actual implementation of [IFakeCallManager] that interacts with the Android system's
 * `FakeCallManager` to manage and trigger fake phone calls.
 *
 * This class handles the necessary Android-specific operations, such as checking
 * if the phone account is enabled, guiding the user to enable it, and initiating
 * the fake call process.
 *
 * @property context The Android [Context] required for interacting with system services.
 */
class ActualFakeCallManager(private val context: Context) : IFakeCallManager {
    private val androidFakeCallManager = FakeCallManager()

    override fun isPhoneAccountEnable(): Boolean {
        return androidFakeCallManager.isPhoneAccountEnabled(context)
    }

    override fun requestEnablePhoneAccount() {
        androidFakeCallManager.registerPhoneAccount(context)
        androidFakeCallManager.guideUserToEnablePhoneAccount(context)
    }

    override fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?, delayMillis: Long) {
        //todo handle exception

        // Send the app to the background
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(homeIntent)

        Handler(Looper.getMainLooper()).postDelayed({
            androidFakeCallManager.triggerFakeCall(
                context,
                callerName,
                callerNumber,
                callerAvatarUrl
            )
        }, delayMillis)
    }
}
