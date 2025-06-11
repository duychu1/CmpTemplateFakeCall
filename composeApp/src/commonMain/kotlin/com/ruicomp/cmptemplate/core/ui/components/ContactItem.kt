package com.ruicomp.cmptemplate.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.models.Contact
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactItem(
    contact: Contact,
    onCall: ((Contact) -> Unit)? = null,
    onDelete: ((Contact) -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(name = contact.name)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = contact.number,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (actions != null) {
                actions()
            } else {
                Row {
                    if (onCall != null) {
                        IconButton(onClick = { onCall(contact) }) {
                            Icon(Icons.Default.Call, contentDescription = "Call")
                        }
                    }
                    if (onDelete != null) {
                        IconButton(onClick = { onDelete(contact) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ContactItemPreview() {
    ContactItem(
        contact = Contact(
            id = 1,
            name = "John Doe",
            number = "+1 234 567 890"
        ),
        onCall = {},
        onDelete = {}
    )
}

@Preview
@Composable
fun ContactItemNoActionsPreview() {
    ContactItem(
        contact = Contact(
            id = 1,
            name = "John Doe",
            number = "+1 234 567 890"
        )
    )
} 