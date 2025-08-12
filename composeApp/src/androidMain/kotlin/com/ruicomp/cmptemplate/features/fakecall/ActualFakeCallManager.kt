package com.ruicomp.cmptemplate.features.fakecall

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
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
    private var handler: Handler? = null
    private var fakeCallRunnable: Runnable? = null
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_TRIGGER_FAKE_CALL = "com.ruicomp.cmptemplate.ACTION_TRIGGER_FAKE_CALL"
        const val EXTRA_REQUEST_CODE = "requestCode"
        const val EXTRA_CALLER_NAME = "callerName"
        const val EXTRA_CALLER_NUMBER = "callerNumber"
        const val EXTRA_CALLER_AVATAR_URL = "callerAvatarUrl"
        private const val FAKE_CALL_REQUEST_CODE = 1001
    }

    override fun isPhoneAccountEnable(): Boolean {
        return androidFakeCallManager.isPhoneAccountEnabled(context)
    }

    override fun requestEnablePhoneAccount() {
        androidFakeCallManager.registerPhoneAccount(context)
        androidFakeCallManager.guideUserToEnablePhoneAccount(context)
    }

    override fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?, delayMillis: Long) {
        // Send the app to the background
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(homeIntent)

        handler = Handler(Looper.getMainLooper())
        fakeCallRunnable = Runnable {
            androidFakeCallManager.triggerFakeCall(
                context,
                callerName,
                callerNumber,
                callerAvatarUrl
            )
        }
        if (handler != null && fakeCallRunnable != null) {
            handler!!.postDelayed(fakeCallRunnable!!, delayMillis)
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleExactFakeCall(requestCode: Int, callerName: String, callerNumber: String, callerAvatarUrl: String?, triggerAtMillis: Long) {
        val intent = Intent(context, FakeCallAlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_FAKE_CALL
            putExtra(EXTRA_REQUEST_CODE, requestCode)
            putExtra(EXTRA_CALLER_NAME, callerName)
            putExtra(EXTRA_CALLER_NUMBER, callerNumber)
            putExtra(EXTRA_CALLER_AVATAR_URL, callerAvatarUrl)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Ensure the app has permission to schedule exact alarms on Android S+
        // This should ideally be checked before calling this method.
        // For simplicity here, we assume permission is granted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                // Handle the case where permission is not granted
                // Maybe fall back to an inexact alarm or notify the user
                // For now, let's log or throw an exception
                println("Cannot schedule exact alarms. Permission not granted.")
                // As a fallback, you could use an inexact alarm or triggerFakeCall with a delay.
                // For this example, we'll just use triggerFakeCall with a calculated delay.
                 val delayMillis = triggerAtMillis - System.currentTimeMillis()
                 if (delayMillis > 0) {
                    triggerFakeCall(callerName, callerNumber, callerAvatarUrl, delayMillis)
                 } else {
                    triggerFakeCall(callerName, callerNumber, callerAvatarUrl, 0)
                 }
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    /**
     * Checks if an alarm for a fake call with the given request code is currently scheduled.
     *
     * @param requestCode The request code of the alarm to check.
     * @return `true` if an alarm with the specified request code is scheduled, `false` otherwise.
     *         Returns `false` if no such PendingIntent exists (meaning no alarm is scheduled with that request code).
     */
    override fun isAlarmCallScheduled(requestCode: Int): Boolean {
        val intent = Intent(context, FakeCallAlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_FAKE_CALL
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        println("pendingIntent: $pendingIntent")
        return pendingIntent != null
    }


    override fun cancelCall(requestCode: Int) {
        // Cancel Handler-based call
//        try {
//            handler?.removeCallbacks(fakeCallRunnable!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        // Cancel AlarmManager-based call
        val intent = Intent(context, FakeCallAlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_FAKE_CALL
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
