package com.ruicomp.cmptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


interface IFakeCallManager {
    fun isPhoneAccountEnable(): Boolean
    fun requestEnablePhoneAccount()
    fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String? = null, delayMillis:Long = 1000L)
    fun scheduleExactFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String? = null, triggerAtMillis: Long)
    fun cancelCall()
}
