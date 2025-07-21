package com.ruicomp.cmptemplate.features.saved_caller.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.components.ContactInputFields
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InputContactDialog(
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = stringResource(Res.string.dialog_add),
    dismissText: String = stringResource(Res.string.dialog_cancel)
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
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

@Preview
@Composable
private fun AddContactDialogPreview() {
    var name by remember { mutableStateOf("John Doe") }
    var number by remember { mutableStateOf("+1234567890") }

    InputContactDialog(
        name = name,
        number = number,
        onNameChange = { name = it },
        onNumberChange = { number = it },
        onDismiss = {},
        onConfirm = {}
    )
}

