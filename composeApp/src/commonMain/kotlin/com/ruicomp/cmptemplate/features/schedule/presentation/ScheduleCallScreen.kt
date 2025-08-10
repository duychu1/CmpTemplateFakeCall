package com.ruicomp.cmptemplate.features.schedule.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.cmptemplate.core.models.Contact
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.components.ContactInputFields
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCallScreen(
    onBack: () -> Unit,
    viewModel: ScheduleCallViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ScheduleCallScreenContent(
        onBack = onBack,
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCallScreenContent(
    onBack: () -> Unit,
    uiState: ScheduleCallState,
    onEvent: (ScheduleCallEvent) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.selectedDateMillis ?: Clock.System.now().toEpochMilliseconds()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    var showTimePicker by remember { mutableStateOf(false) }



    val isFormValid by remember(
        uiState.name,
        uiState.number,
        uiState.selectedDateMillis,
        uiState.selectedHour,
        uiState.selectedMinute
    ) {
        derivedStateOf {
            uiState.name.isNotBlank() && uiState.number.isNotBlank() &&
                    uiState.selectedDateMillis != null && uiState.selectedHour != null && uiState.selectedMinute != null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.schedule_call_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onEvent(ScheduleCallEvent.Schedule("${uiState.formattedDate} at ${uiState.formattedTime}")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = isFormValid && !uiState.isScheduling
            ) {
                Text(stringResource(Res.string.schedule_call_button))
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
                onPickContact = { onEvent(ScheduleCallEvent.ShowContactSheet) },
                onNameChange = { onEvent(ScheduleCallEvent.NameChanged(it)) },
                onNumberChange = { onEvent(ScheduleCallEvent.NumberChanged(it)) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            SelectDateTimeSection(
                date = uiState.formattedDate,
                onDateSelect = { showDatePicker = true },
                time = uiState.formattedTime,
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
                    datePickerState.selectedDateMillis?.let {
                        onEvent(ScheduleCallEvent.DateSelected(it))
                    }
                }) {
                    Text(stringResource(Res.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.selectedHour ?: currentTime.hour,
            initialMinute = uiState.selectedMinute ?: currentTime.minute
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(ScheduleCallEvent.TimeSelected(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) {
                    Text(stringResource(Res.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (uiState.isContactSheetVisible) {
        ModalBottomSheet(onDismissRequest = { onEvent(ScheduleCallEvent.HideContactSheet) }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(uiState.contacts) { contact ->
                    ContactPickerItem(
                        contact = contact,
                        onClick = { onEvent(ScheduleCallEvent.SelectContact(contact)) }
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
            .padding(vertical = 8.dp),
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
    title: String = stringResource(Res.string.select_time_title),
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
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
        Text(stringResource(Res.string.contact_information_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactInputFields(
                    name = name,
                    number = number,
                    onNameChange = onNameChange,
                    onNumberChange = onNumberChange,

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

@Composable
private fun SelectDateTimeSection(
    date: String,
    onDateSelect: () -> Unit,
    time: String,
    onTimeSelect: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(stringResource(Res.string.select_date_time_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.date_label)) },
                        trailingIcon = {
                            IconButton(onClick = onDateSelect) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        
                    )
                    // This Box will intercept the click and prevent the OutlinedTextField from gaining focus
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(onClick = onDateSelect, indication = null, interactionSource = remember { MutableInteractionSource() })
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = time,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.time_label)) },
                        trailingIcon = {
                            IconButton(onClick = onTimeSelect) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Time") // Consider Icons.Default.AccessTime
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                    )
                    // This Box will intercept the click and prevent the OutlinedTextField from gaining focus
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(onClick = onTimeSelect, indication = null, interactionSource = remember { MutableInteractionSource() })
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ScheduleCallScreenPreview() {
    ScheduleCallScreenContent(
        onBack = {},
        uiState = ScheduleCallState(),
        onEvent = {}
    )
}