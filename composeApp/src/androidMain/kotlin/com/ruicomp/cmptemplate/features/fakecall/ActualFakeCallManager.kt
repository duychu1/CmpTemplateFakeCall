package com.ruicomp.cmptemplate.features.fakecall

import android.content.Context
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

    override fun isPhonePermissionGranted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun requestPermission() {
        androidFakeCallManager.guideUserToEnablePhoneAccount(context)
    }

    override fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?) {
        androidFakeCallManager.registerPhoneAccount(context)
        androidFakeCallManager.triggerFakeCall(
            context,
            callerName,
            callerNumber,
            callerAvatarUrl
        )
    }
}