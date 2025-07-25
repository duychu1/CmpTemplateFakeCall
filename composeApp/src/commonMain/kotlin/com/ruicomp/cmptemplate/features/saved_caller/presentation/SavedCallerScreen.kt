package com.ruicomp.cmptemplate.features.saved_caller.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.permissions.presentation.components.PermissionAware
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallBottomSheet
// Add this import
import com.ruicomp.cmptemplate.core.utils.ContactPicker
import com.ruicomp.cmptemplate.features.saved_caller.presentation.components.InputContactDialog
import com.ruicomp.cmptemplate.features.saved_caller.presentation.components.SaveCallerItem
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
    val uiBasePermissionState by viewModel.uiBasePermissionState.collectAsStateWithLifecycle()

    // 1. Register the contact picker and get a launcher function
    val launchPicker = ContactPicker.RegisterPicker { pickedContact ->
        if (pickedContact == null) {
            // Handle the case where no contact was picked
            //todo
            return@RegisterPicker
        }
        viewModel.onEvent(SavedCallerEvent.ContactPicked(pickedContact.name, pickedContact.phoneNumber))
    }

    // 2. Effect to launch the picker when the ViewModel signals
    LaunchedEffect(uiState.triggerContactPickerLaunch) {
        if (uiState.triggerContactPickerLaunch) {
            launchPicker() // Call the launcher function obtained from RegisterPicker
            viewModel.onEvent(SavedCallerEvent.ResetContactPickerLaunchTrigger) // Reset the trigger
        }
    }

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

    // Permission Handling for READ_CONTACTS
    val contactsPermission = SavedCallerViewModel.READ_CONTACTS_PERMISSION
    if (uiBasePermissionState.permissionAwareStates[contactsPermission] == true) {
        PermissionAware(
            permission = contactsPermission,
            permissionNameDialog = stringResource(Res.string.contacts_permission_name), // Ensure this string exists
            initialed = uiBasePermissionState.initialized,
            onEvent = viewModel.basePermissionManager::onEvent
            )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SavedCallerScreenContent(
    onBack: () -> Unit,
    uiState: SavedCallerState,
    onEvent: (SavedCallerEvent) -> Unit
) {

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
                    item {
                        Button(
                            onClick = { onEvent(SavedCallerEvent.ImportContactsFromSystemClicked) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContactPhone, contentDescription = "Import Contacts")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.add_contact_from_system))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    items(uiState.contacts) { contact ->
                        SaveCallerItem(
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
        InputContactDialog(
            name = uiState.addContactName,
            number = uiState.addContactNumber,
            onNameChange = { onEvent(SavedCallerEvent.UpdateAddContactName(it)) },
            onNumberChange = { onEvent(SavedCallerEvent.UpdateAddContactNumber(it)) },
            onDismiss = { onEvent(SavedCallerEvent.ShowAddContactDialog(false)) },
            onConfirm = {
                onEvent(SavedCallerEvent.AddContact)
                // Close dialog after confirming
                onEvent(SavedCallerEvent.ShowAddContactDialog(false))
            }
        )
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
