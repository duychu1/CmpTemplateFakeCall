package com.ruicomp.cmptemplate.features.saved_caller.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.components.ContactInputFields
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddContactDialog(
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.add_new_contact_title)) },
        text = {
            ContactInputFields(name, number, onNameChange, onNumberChange)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = name.isNotBlank() && number.isNotBlank()
            ) {
                Text(stringResource(Res.string.dialog_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.dialog_cancel))
            }
        }
    )
}

@Preview
@Composable
private fun AddContactDialogPreview() {
    var name by remember { mutableStateOf("John Doe") }
    var number by remember { mutableStateOf("+1234567890") }

    AddContactDialog(
        name = name,
        number = number,
        onNameChange = { name = it },
        onNumberChange = { number = it },
        onDismiss = {},
        onConfirm = {}
    )
}

