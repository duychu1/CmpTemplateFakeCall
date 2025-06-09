package com.ruicomp.cmptemplate.presentation.screens.saved_caller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.domain.models.Caller
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCallerScreen(
    onBack: () -> Unit,
    viewModel: SavedCallerViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddCallerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Caller") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCallerDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Caller")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.callers) { caller ->
                CallerItem(
                    caller = caller,
                    onCall = { /*TODO*/ },
                    onDelete = { viewModel.onDeleteCaller(caller.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showAddCallerDialog) {
        AddCallerDialog(
            onDismiss = { showAddCallerDialog = false },
            onConfirm = { name, number ->
                viewModel.onAddCaller(name, number)
                showAddCallerDialog = false
            }
        )
    }
}

@Composable
private fun AddCallerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Caller") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, number) },
                enabled = name.isNotBlank() && number.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CallerItem(
    caller: Caller,
    onCall: (Caller) -> Unit,
    onDelete: (Caller) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = caller.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = caller.number, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { onCall(caller) }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
                IconButton(onClick = { onDelete(caller) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
} 