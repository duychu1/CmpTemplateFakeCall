package com.ruicomp.cmptemplate.core.permissions.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.cancel
import cmptemplate.composeapp.generated.resources.ok
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    agreeText: String = stringResource( Res.string.ok),
    cancelText: String = stringResource( Res.string.cancel),
    onAgree: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onAgree) {
                Text(agreeText)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelText)
            }
        }
    )
}

@Preview
@Composable
fun PermissionRationaleDialogPreview() {
    CustomAlertDialog(
        title = "Permission Required",
        message = "This app needs access to your location to provide relevant information.",
        onAgree = {},
        onCancel = {},
        onDismiss = {}
    )
}



