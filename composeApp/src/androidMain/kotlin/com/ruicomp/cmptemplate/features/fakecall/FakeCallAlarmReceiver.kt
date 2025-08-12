package com.ruicomp.cmptemplate.features.fakecall

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ruicomp.cmptemplate.features.schedule.domain.repository.ScheduledCalledRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FakeCallAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduledCalledRepository: ScheduledCalledRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == ActualFakeCallManager.ACTION_TRIGGER_FAKE_CALL) {
            val requestCode = intent.getIntExtra(ActualFakeCallManager.EXTRA_REQUEST_CODE, -1)

            // Cancel the PendingIntent before handling the call and DB operation.
            if (requestCode != -1) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intentToCancel = Intent(context, FakeCallAlarmReceiver::class.java).apply {
                    action = ActualFakeCallManager.ACTION_TRIGGER_FAKE_CALL
                }
                val pendingIntentToCancel = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intentToCancel,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntentToCancel)
                pendingIntentToCancel.cancel()
                println("Cleaned up the alarm for request code: $requestCode")
            }

            val callerName = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_NAME)
            val callerNumber = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_NUMBER)
            val callerAvatarUrl = intent.getStringExtra(ActualFakeCallManager.EXTRA_CALLER_AVATAR_URL)

            if (callerName != null && callerNumber != null) {
                // Here, we use the FakeCallManager directly to trigger the call UI.
                // Ensure FakeCallManager is accessible and can be instantiated here.
                // If FakeCallManager needs specific context or setup, adjust accordingly.
                val fakeCallManager = FakeCallManager() // This is the Android Telecom one
                fakeCallManager.triggerFakeCall(context, callerName, callerNumber, callerAvatarUrl)
                CoroutineScope(Dispatchers.IO).launch {
                    if (requestCode != -1) {
                        scheduledCalledRepository.removeScheduledCall(requestCode)
                    }
                }
            }
        }
    }
}
