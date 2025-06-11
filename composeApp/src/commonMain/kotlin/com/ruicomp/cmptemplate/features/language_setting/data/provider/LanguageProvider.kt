package com.ruicomp.cmptemplate.features.language_setting.data.provider

import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.flag_china
import cmptemplate.composeapp.generated.resources.flag_en
import cmptemplate.composeapp.generated.resources.flag_france
import cmptemplate.composeapp.generated.resources.flag_germany
import cmptemplate.composeapp.generated.resources.flag_hi
import cmptemplate.composeapp.generated.resources.flag_it
import cmptemplate.composeapp.generated.resources.flag_japan
import cmptemplate.composeapp.generated.resources.flag_pl
import cmptemplate.composeapp.generated.resources.flag_russia
import cmptemplate.composeapp.generated.resources.flag_south_korea
import cmptemplate.composeapp.generated.resources.flag_spain_es
import cmptemplate.composeapp.generated.resources.flag_taiwan
import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language

object LanguageProvider {
    val languages = listOf(
        Language("en", "English", Res.drawable.flag_en, isSelected = true),
        Language("pl", "Polska", Res.drawable.flag_pl),
        Language("hi", "हिंदी", Res.drawable.flag_hi),
        Language("it", "Italia", Res.drawable.flag_it),
        Language("de", "Deutsch", Res.drawable.flag_germany),
        Language("fr", "Français", Res.drawable.flag_france),
        Language("es", "Español", Res.drawable.flag_spain_es),
        Language("ru", "Русский", Res.drawable.flag_russia),
        Language("zh-CN", "简体中文", Res.drawable.flag_china),
        Language("zh-TW", "繁體中文", Res.drawable.flag_taiwan),
        Language("ja", "日本語", Res.drawable.flag_japan),
        Language("ko", "한국어", Res.drawable.flag_south_korea),


        )

    val defaultLanguageCode = languages.first().code

    const val KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE"
}