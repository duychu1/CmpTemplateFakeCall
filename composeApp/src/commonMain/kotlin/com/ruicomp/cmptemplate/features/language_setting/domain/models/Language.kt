package com.ruicomp.cmptemplate.features.language_setting.domain.models

data class Language(
    val code: String,
    val name: String,
    val flagResId: Int,
    val isSelected: Boolean = false,
    val colorBackground: String? = null,
)