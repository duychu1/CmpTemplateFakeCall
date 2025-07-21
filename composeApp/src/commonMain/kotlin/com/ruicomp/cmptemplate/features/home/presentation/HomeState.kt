package com.ruicomp.cmptemplate.features.home.presentation

import com.ruicomp.cmptemplate.core.models.Contact

data class HomeState(
    val test: Boolean = false,
    val contact: Contact = Contact(
        id = -5,
        name = "Unknown",
        number = "0123456789"
    ),
    val showInputContactDialog: Boolean = false,
    val nameTmp: String = "",
    val numberTmp: String = "",
    val selectedDelaySeconds: Int = 0
)