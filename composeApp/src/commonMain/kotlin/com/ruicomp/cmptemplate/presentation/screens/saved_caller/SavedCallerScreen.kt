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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.domain.models.Caller

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCallerScreen(onBack: () -> Unit) {
    val sampleCallers = listOf(
        Caller(1, "John Doe", "123-456-7890"),
        Caller(2, "Jane Smith", "098-765-4321")
    )

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
            FloatingActionButton(onClick = { /* TODO: Add new caller */ }) {
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
            items(sampleCallers) { caller ->
                CallerItem(caller = caller, onCall = { /*TODO*/ }, onDelete = { /*TODO*/ })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
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