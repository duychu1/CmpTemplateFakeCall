package com.ruicomp.cmptemplate.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.app.DefaultValues
import com.ruicomp.cmptemplate.core.models.Contact
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeContactItem(
    contact: Contact,
    listOfDelays: List<Int> = DefaultValues.delayOptionsInSeconds,
    selectedDelay: Int = listOfDelays.first(),
    onDelaySelected: (Int) -> Unit = {},
    onEdit: () -> Unit = {},
    onCall: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        ) {
        // Name and Number
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
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
                Text(
                    text = contact.number,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
//                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
            }
        }

        FilledTonalIconButton(
            onClick = onEdit,
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Delete")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Dropdown for delay selection
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { expanded = true }
            ) {
                Text(
                    text = "$selectedDelay s",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOfDelays.forEach { delay ->
                    DropdownMenuItem(
                        text = { Text("$delay s") },
                        onClick = {
                            onDelaySelected(delay)
                            expanded = false
                        })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        FilledIconButton(
            onClick = onCall,
            modifier = Modifier.size(56.dp).align(Alignment.CenterVertically),
        ) {
            Icon(
                imageVector =Icons.Default.Call,
                modifier = Modifier.fillMaxHeight(0.6f).fillMaxWidth(0.6f),
                contentDescription = "Call"
            )
        }
    }
}

@Preview
@Composable
fun HomeContactItemPreview() {
    var selectedDelay by remember { mutableStateOf(0) }
    val contact = Contact(name = "John Doe", number = "123456789")

    HomeContactItem(
        contact = contact,
        selectedDelay = selectedDelay,
        onDelaySelected = { selectedDelay = it })
}

