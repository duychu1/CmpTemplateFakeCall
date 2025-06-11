package com.ruicomp.cmptemplate.features.language_setting.presentation

sealed class LanguageSettingEvent {
    data class SelectLanguage(val code: String) : LanguageSettingEvent()
}
