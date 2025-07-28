package com.ruicomp.cmptemplate.features.fakecall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FakeCallAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == ActualFakeCallManager.ACTION_TRIGGER_FAKE_CALL) {
            val callerName = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_NAME)
            val callerNumber = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_NUMBER)
            val callerAvatarUrl = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_AVATAR_URL)

            if (callerName != null && callerNumber != null) {
                // Here, we use the FakeCallManager directly to trigger the call UI.
                // Ensure FakeCallManager is accessible and can be instantiated here.
                // If FakeCallManager needs specific context or setup, adjust accordingly.
                val fakeCallManager = FakeCallManager() // This is the Android Telecom one
                fakeCallManager.triggerFakeCall(context, callerName, callerNumber, callerAvatarUrl)
            }
        }
    }
}
