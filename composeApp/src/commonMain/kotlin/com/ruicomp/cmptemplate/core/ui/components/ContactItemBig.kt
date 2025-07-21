package com.ruicomp.cmptemplate.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruicomp.cmptemplate.core.models.Contact
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactItemBig(
    contact: Contact,
    actions: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ContactAvatar(
            name = contact.name,
            size = 80,
            fontSize = 32.sp,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Spacer(modifier = Modifier.width(16.dp))
        // Name and Number
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(text = contact.number, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
        }

        if (actions != null) {
            actions()
        }
    }
}

@Preview
@Composable
fun ContactItemBigPreview() {
    val contact = Contact(
        id = 1,
        name = "John Doe",
        number = "+1 234 567 890"
    )
    ContactItemBig(
        contact = contact,
        actions = {
            Row {
                IconButton(onClick = { /* Handle Call */ }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
                IconButton(onClick = { /* Handle Delete */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

@Preview
@Composable
fun ContactItemBigNoActionsPreview() {
    ContactItem(
        contact = Contact(
            id = 1,
            name = "John Doe",
            number = "+1 234 567 890"
        )
    )
}