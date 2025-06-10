package com.ruicomp.cmptemplate.features.schedule.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.database.models.Contact
import com.ruicomp.cmptemplate.features.schedule.presentation.ScheduleCallViewModel
import kotlinx.datetime.*
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCallScreen(
    onBack: () -> Unit,
    viewModel: ScheduleCallViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds()
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute
    )
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val formattedDate by remember(selectedDate) {
        derivedStateOf {
            selectedDate?.let {
                val instant = Instant.fromEpochMilliseconds(it)
                val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                val monthName = localDate.month.name.lowercase().take(3).replaceFirstChar { char -> char.uppercase() }
                "${monthName} ${localDate.dayOfMonth.toString().padStart(2, '0')}, ${localDate.year}"
            } ?: ""
        }
    }

    val formattedTime by remember(selectedTime) {
        derivedStateOf {
            selectedTime?.let { (hour, minute) ->
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
            } ?: ""
        }
    }

    val isFormValid by derivedStateOf {
        uiState.name.isNotBlank() && uiState.number.isNotBlank() && selectedDate != null && selectedTime != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Call") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onScheduleCall("$formattedDate at $formattedTime") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = isFormValid
            ) {
                Text("Schedule Call")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ContactInformationSection(
                name = uiState.name,
                number = uiState.number,
                onPickContact = { viewModel.showContactSheet() },
                onNameChange = { viewModel.onNameChange(it) },
                onNumberChange = { viewModel.onNumberChange(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            SelectDateTimeSection(
                date = formattedDate,
                onDateSelect = { showDatePicker = true },
                time = formattedTime,
                onTimeSelect = { showTimePicker = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedDate = datePickerState.selectedDateMillis
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    selectedTime = timePickerState.hour to timePickerState.minute
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (uiState.isContactSheetVisible) {
        ModalBottomSheet(onDismissRequest = { viewModel.hideContactSheet() }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.contacts) { contact ->
                    ContactPickerItem(
                        contact = contact,
                        onClick = { viewModel.onContactSelected(contact) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactPickerItem(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = contact.number, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                content()
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        containerColor = containerColor
    )
}

@Composable
private fun ContactInformationSection(
    name: String,
    number: String,
    onPickContact: () -> Unit,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Contact Information", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Caller Name") },
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
                    label = { Text("Phone Number") },
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
                    Text("Pick from Contacts")
                }
            }
        }
    }
}

@Composable
private fun SelectDateTimeSection(
    date: String,
    onDateSelect: () -> Unit,
    time: String,
    onTimeSelect: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Select Date & Time", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = onDateSelect) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text("Time") },
                    trailingIcon = {
                        IconButton(onClick = onTimeSelect) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Time")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
            }
        }
    }
} 