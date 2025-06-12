package com.ruicomp.cmptemplate.features.fakecall

import android.os.Handler
import android.os.Looper
import android.telecom.Connection
import android.telecom.DisconnectCause

class FakeCallConnection(
    private val callerName: String,
    private val callerNumber: String,
    private val callerAvatarUri: String?
) : Connection() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onShowIncomingCallUi() {
        setRinging()
    }

    override fun onAnswer() {
        setActive()
        handler.postDelayed({
            setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            destroy()
        }, 5000) // Auto disconnect after 5 seconds
    }

    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onReject() {
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }
}

