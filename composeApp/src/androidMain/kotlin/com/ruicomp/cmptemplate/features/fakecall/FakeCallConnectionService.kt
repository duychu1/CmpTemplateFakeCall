package com.ruicomp.cmptemplate.features.fakecall

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.net.toUri

class FakeCallConnectionService : ConnectionService() {
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val extras = request?.extras
        val callerName = extras?.getString(FakeCallManager.EXTRA_CALLER_NAME) ?: "NameTest"
        Log.d("FakeCallConnectionSvc", "Incoming call from $callerName")
        val callerNumber = extras?.getString(FakeCallManager.EXTRA_CALLER_NUMBER) ?: "+65467890909"
        Log.d("FakeCallConnectionSvc", "Incoming call from $callerNumber")
        val callerAvatarUri = extras?.getString(FakeCallManager.EXTRA_CALLER_AVATAR_URI)
        return FakeCallConnection(callerName, callerNumber, callerAvatarUri).apply {
            setCallerDisplayName(callerName, TelecomManager.PRESENTATION_ALLOWED)
            setAddress("tel:$callerNumber".toUri(), TelecomManager.PRESENTATION_ALLOWED)
        }
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        Log.e("FakeCallConnectionSvc", "Failed to create incoming connection")
    }

    inner class FakeCallConnection(
        private val callerName: String,
        private val callerNumber: String,
        private val callerAvatarUri: String?
    ) : Connection() {

        override fun onAnswer() {
            Log.d("FakeCallConnectionSvc", "onAnswer")
            setActive()
//            handler.postDelayed({
//                setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
//                destroy()
//            }, 5000) // Auto disconnect after 5 seconds
        }

        override fun onDisconnect() {
            Log.d("FakeCallConnectionSvc", "onDisconnect")
            setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            destroy()
        }

        override fun onReject() {
            Log.d("FakeCallConnectionSvc", "onReject")
            setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            destroy()
        }
    }
}

