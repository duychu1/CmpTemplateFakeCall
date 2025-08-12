package com.ruicomp.cmptemplate.features.schedule.data.models

data class ScheduledCalled(
    val id: Long = 1000,         // Unique per PendingIntent/call, use for request code
    val name: String,
    val number: String,
    val avatarUrl: String? = null,
    val triggerAtMillis: Long     // Absolute time when the call should trigger
)
