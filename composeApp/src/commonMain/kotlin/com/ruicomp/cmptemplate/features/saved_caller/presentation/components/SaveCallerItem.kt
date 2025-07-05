package com.ruicomp.cmptemplate.features.saved_caller.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun SaveCallerItem(
    contact: Contact,
    onCall: (Contact) -> Unit,
    onDelete: (Contact) -> Unit
) {
    ContactItem(
        contact = contact,
        actions = {
            Row {
                IconButton(onClick = { onCall(contact) }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
                IconButton(onClick = { onDelete(contact) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

@Preview
@Composable
fun SaveCallerItemPreview() {
    val contact = Contact(name = "John Doe", number = "1234567890")
    SaveCallerItem(
        contact = contact,
        onCall = {},
        onDelete = {})
}
