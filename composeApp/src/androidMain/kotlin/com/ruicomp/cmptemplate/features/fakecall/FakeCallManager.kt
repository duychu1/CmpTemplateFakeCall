package com.ruicomp.cmptemplate.features.fakecall

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat

class FakeCallManager {
    companion object {
        const val PHONE_ACCOUNT_ID = "fake_call_account_id"
        const val PHONE_ACCOUNT_LABEL = "Fake Call"
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

    fun guideUserToGrantPermission(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
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
                    // .setIcon(Icon.createWithResource(context, R.drawable.ic_voip_logo))
                    .build()
                telecomManager.registerPhoneAccount(newAccount)
            } else {
                // Optional: You could check if properties need updating, e.g., if the user
                // changed the app's language and you want to update the label.
                // For most cases, this else block can be empty.
                println("PhoneAccount already registered.")
            }

        } catch (e: SecurityException) {
            println("Permission MANAGE_OWN_CALLS is required to register a PhoneAccount.")
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
