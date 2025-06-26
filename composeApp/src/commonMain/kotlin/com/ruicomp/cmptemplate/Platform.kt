package com.ruicomp.cmptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


interface IFakeCallManager {
    fun isPhoneAccountEnable(): Boolean
    fun requestEnablePhoneAccount()
    fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?, delayMillis:Long = 1000L)
}
