package com.ruicomp.cmptemplate.features.settings.presentation

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    object RateUsClick : SettingsEvent()
    object ShareAppClick : SettingsEvent()
}

