package com.ruicomp.cmptemplate.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.name_label
import cmptemplate.composeapp.generated.resources.number_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContactInputFields(
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.name_label)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
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
            label = { Text(stringResource(Res.string.number_label)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
    }
}