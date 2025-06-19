package com.ruicomp.cmptemplate.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route

@Serializable
data object HomeRoute : Route()

@Serializable
data object ScheduleCallRoute : Route()

@Serializable
data object SavedCallerRoute : Route()

@Serializable
data object CallHistoryRoute : Route()

@Serializable
data object SettingsRoute : Route()

@Serializable
data object LanguageSettingRoute : Route()

@Serializable
data class WebViewRoute(val url: String, val title: String? = null) : Route()