package com.ruicomp.cmptemplate.features.fakecall

import android.content.Context
import com.ruicomp.cmptemplate.IFakeCallManager

// Assuming your FakeCallManager class is in the same package
// and you have a way to get a Context (e.g., from an Application class or dependency injection)

class ActualFakeCallManager(private val context: Context) : IFakeCallManager {
    private val androidFakeCallManager = FakeCallManager()

    override fun isPermissionGranted(): Boolean {
        return androidFakeCallManager.isPhoneAccountEnabled(context)
    }

    override fun requestPermission() {
        androidFakeCallManager.guideUserToEnablePhoneAccount(context)
    }

    override fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?) {
        // You might need to adjust your Android FakeCallManager to accept a Context
        // or have a way to access it internally if it doesn't already.
        androidFakeCallManager.registerPhoneAccount(context) // Ensure account is registered
        androidFakeCallManager.triggerFakeCall(context, callerName, callerNumber, callerAvatarUrl)
    }
}