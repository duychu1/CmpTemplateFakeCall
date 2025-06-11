package com.ruicomp.cmptemplate.features.schedule.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cmptemplate.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactInformationSection(
    name: String,
    number: String,
    onPickContact: () -> Unit,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(stringResource(Res.string.contact_information_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(Res.string.caller_name_label)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Caller Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = number,
                    onValueChange = {
                        val allowedChars = "0123456789+ -()#"
                        if (it.all { char -> allowedChars.contains(char) }) {
                            onNumberChange(it)
                        }
                    },
                    label = { Text(stringResource(Res.string.phone_number_label)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onPickContact,
                    modifier = Modifier.fillMaxWidth(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(stringResource(Res.string.pick_from_contacts_button))
                }
            }
        }
    }
}

@Preview
@Composable
fun ContactInformationSectionPreview() {
    ContactInformationSection(
        name = "John Doe",
        number = "+1 234 567 890",
        onPickContact = {},
        onNameChange = {},
        onNumberChange = {}
    )
} 