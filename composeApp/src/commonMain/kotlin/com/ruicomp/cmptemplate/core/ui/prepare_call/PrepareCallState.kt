package com.ruicomp.cmptemplate.core.ui.prepare_call

import com.ruicomp.cmptemplate.app.DefaultValues
import com.ruicomp.cmptemplate.core.models.Contact

data class PrepareCallState(
    val showBottomSheet: Boolean = false,
    val selectedContact: Contact? = null, // Contact can be null initially
    val selectedDelayInSeconds: Int = 0,    // Default to 0 seconds ("Now")
    val delayOptionsInSeconds: List<Int> = DefaultValues.delayOptionsInSeconds
)