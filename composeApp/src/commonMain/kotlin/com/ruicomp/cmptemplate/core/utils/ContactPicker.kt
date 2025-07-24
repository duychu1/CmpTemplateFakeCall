package com.ruicomp.cmptemplate.core.utils

import androidx.compose.runtime.Composable

data class PickedContact(val name: String, val phoneNumber: String)

/**
 * An object to provide contact picking functionality.
 */
expect object ContactPicker {
    /**
     * Registers a contact picker launcher.
     * This Composable function should be called in your Composable screen.
     *
     * @param onContactPicked A callback that will be invoked with the PickedContact, or null if no contact was selected.
     * @return A lambda function that, when invoked, will launch the system contact picker.
     */
    @Composable
    fun RegisterPicker(onContactPicked: (PickedContact?) -> Unit): () -> Unit
}
