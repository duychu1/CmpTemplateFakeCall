package com.ruicomp.cmptemplate.features.language_setting.domain.models

import org.jetbrains.compose.resources.DrawableResource

data class Language(
    val code: String,
    val name: String,
    val flagResId: DrawableResource?,
    val isSelected: Boolean = false,
    val colorBackground: String? = null,
)