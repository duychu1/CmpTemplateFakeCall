package com.ruicomp.cmptemplate.features.fakecall

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ruicomp.cmptemplate.R

class FakeCallManager {
    companion object {
        const val PHONE_ACCOUNT_ID = "fake_call_account_id_124"
        const val PHONE_ACCOUNT_LABEL = "Fake Call App"
        const val EXTRA_CALLER_NAME = "EXTRA_CALLER_NAME"
        const val EXTRA_CALLER_NUMBER = "EXTRA_CALLER_NUMBER"
        const val EXTRA_CALLER_AVATAR_URI = "EXTRA_CALLER_AVATAR_URI"
    }

    fun isManageOwnCallsPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.MANAGE_OWN_CALLS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun isPhoneAccountEnabled(context: Context): Boolean {
        if (!isManageOwnCallsPermissionGranted(context)) {
            println("MANAGE_OWN_CALLS permission not granted. Cannot check PhoneAccount status.")
            return false
        }

        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val handle = getPhoneAccountHandle(context)

        return try {
            val account: PhoneAccount? = telecomManager.getPhoneAccount(handle)
            account != null && account.isEnabled
        } catch (e: SecurityException) {
            println("isPhoneAccountEnabled: SecurityException while checking PhoneAccount status: ${e.message}")
            false
        }
    }

    /**
     * Guides the user to the system settings screen where they can enable/disable PhoneAccounts.
     * This is necessary if your app's PhoneAccount is registered but not enabled by the user.
     */
    fun guideUserToEnablePhoneAccount(context: Context) {
        val intent = Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // Optionally, you can add your PhoneAccountHandle as an extra
            // to highlight it if the system UI supports it, though it's not guaranteed.
            // intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getPhoneAccountHandle(context))
        }
        try {
            context.startActivity(intent)
            Toast.makeText(context, "Please enable '${PHONE_ACCOUNT_LABEL}' for calling.", Toast.LENGTH_SHORT).show()
        } catch (e: ActivityNotFoundException) {
            // This might happen if no activity can handle this intent (highly unlikely on most devices)
            // or on very restricted devices. Fallback to general call settings.
            Toast.makeText(context, "Cant open change phone account", Toast.LENGTH_SHORT).show()

        }
    }

    private fun getPhoneAccountHandle(context: Context): PhoneAccountHandle {
        return PhoneAccountHandle(
            android.content.ComponentName(context, FakeCallConnectionService::class.java),
            PHONE_ACCOUNT_ID
        )
    }

    fun registerPhoneAccount(context: Context) {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val handle = getPhoneAccountHandle(context)

        try {
            val existingAccount = telecomManager.getPhoneAccount(handle)
            if (existingAccount == null) {
                println("PhoneAccount not found. Registering a new one.")
                val newAccount = PhoneAccount.builder(handle, PHONE_ACCOUNT_LABEL)
                    .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                    // Add other capabilities or properties as needed
//                    .setIcon(Icon.createWithResource(context, R.drawable.ic_launcher_foreground))
                    .build()
                telecomManager.registerPhoneAccount(newAccount)
            } else {
                // Optional: You could check if properties need updating, e.g., if the user
                // changed the app's language and you want to update the label.
                // For most cases, this else block can be empty.
                println("PhoneAccount already registered.")
            }

        } catch (e: SecurityException) {
            println("registerPhoneAccount: SecurityException while registering PhoneAccount: ${e.message}")
        }
    }

    fun triggerFakeCall(
        context: Context,
        callerName: String,
        callerNumber: String,
        callerAvatarUri: String?
    ) {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val handle = getPhoneAccountHandle(context)
        val extras = android.os.Bundle().apply {
            putString(EXTRA_CALLER_NAME, callerName)
            putString(EXTRA_CALLER_NUMBER, callerNumber)
            putString(EXTRA_CALLER_AVATAR_URI, callerAvatarUri)
        }
        telecomManager.addNewIncomingCall(handle, extras)
    }
}
