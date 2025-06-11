package com.ruicomp.cmptemplate.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route

@Serializable
data object Home : Route()

@Serializable
data object ScheduleCall : Route()

@Serializable
data object SavedCaller : Route()

@Serializable
data object CallHistory : Route()

@Serializable
data object Settings : Route() 