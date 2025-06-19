package com.ruicomp.cmptemplate.features.saved_caller.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallBottomSheet
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallSheetContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCallerScreen(
    onBack: () -> Unit,
    viewModel: SavedCallerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiPrepareCallState by viewModel.prepareCallManager.uiState.collectAsStateWithLifecycle()

    SavedCallerScreenContent(
        onBack = onBack,
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

    //prepare call bottom sheet
    PrepareCallBottomSheet(
        prepareCallUiState = uiPrepareCallState,
        onPrepareCallEvent = viewModel.prepareCallManager::onEvent
    )

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SavedCallerScreenContent(
    onBack: () -> Unit,
    uiState: SavedCallerState,
    onEvent: (SavedCallerEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.saved_contacts_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(SavedCallerEvent.ShowAddContactDialog(true)) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.contacts) { contact ->
                        ContactItem(
                            contact = contact,
                            onCall = {
                                onEvent(SavedCallerEvent.SelectContactForCall(contact))
                            },
                            onDelete = { onEvent(SavedCallerEvent.DeleteContact(contact.id)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (uiState.showAddContactDialog) {
        AddContactDialog(
            name = uiState.addContactName,
            number = uiState.addContactNumber,
            onNameChange = { onEvent(SavedCallerEvent.UpdateAddContactName(it)) },
            onNumberChange = { onEvent(SavedCallerEvent.UpdateAddContactNumber(it)) },
            onDismiss = { onEvent(SavedCallerEvent.ShowAddContactDialog(false)) },
            onConfirm = {
                onEvent(SavedCallerEvent.AddContact)
                onEvent(SavedCallerEvent.ShowAddContactDialog(false))
            }
        )
    }

}

@Composable
private fun AddContactDialog(
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
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(Res.string.name_label)) }
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
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

@Composable
private fun ContactItem(
    contact: Contact,
    onCall: (Contact) -> Unit,
    onDelete: (Contact) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.firstOrNull()?.toString()?.uppercase() ?: "",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = contact.number, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { onCall(contact) }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
                IconButton(onClick = { onDelete(contact) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SavedCallerScreenContentPreview() {
    val uiState = SavedCallerState(
        contacts = listOf(
            Contact(id = 1, name = "John Doe", number = "123-456-7890"),
            Contact(id = 2, name = "Jane Smith", number = "098-765-4321")
        ),
        isLoading = false,
        error = null
    )
    SavedCallerScreenContent(onBack = {}, uiState = uiState, onEvent = {})
}