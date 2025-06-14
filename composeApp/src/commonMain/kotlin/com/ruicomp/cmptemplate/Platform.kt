package com.ruicomp.cmptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


interface IFakeCallManager {
    fun isPhoneAccountEnable(): Boolean
    fun isPhonePermissionGranted(): Boolean
    fun requestPermission()
    fun triggerFakeCall(callerName: String, callerNumber: String, callerAvatarUrl: String?)
}
