package com.ruicomp.cmptemplate.features.schedule.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.components.ContactAvatar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactPickerItem(
    contact: Contact,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(name = contact.name)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = contact.number, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun ContactPickerItemPreview() {
    ContactPickerItem(
        contact = Contact(
            id = 1,
            name = "John Doe",
            number = "+1 234 567 890"
        ),
        onClick = {}
    )
} 