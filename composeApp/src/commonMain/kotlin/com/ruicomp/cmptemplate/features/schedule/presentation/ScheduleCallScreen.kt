package com.ruicomp.cmptemplate.features.schedule.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.cmptemplate.core.models.Contact
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.components.ContactInputFields
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import com.ruicomp.cmptemplate.core.ui.components.TimePickerDialog
import com.ruicomp.cmptemplate.features.call_history.presentation.components.TimeStamp
import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import com.ruicomp.cmptemplate.features.schedule.presentation.components.ContactInformationSection
import com.ruicomp.cmptemplate.features.schedule.presentation.components.ContactPickerItem
import com.ruicomp.cmptemplate.features.schedule.presentation.components.ScheduledItem
import com.ruicomp.cmptemplate.features.schedule.presentation.components.SelectDateTimeSection
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

            ScheduledCallsList(
                scheduledCalls = uiState.scheduledCalls,
                onCancelSchedule = { callId ->
                    onEvent(ScheduleCallEvent.CancelScheduleCall(callId))
                }
            )

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
private fun ScheduledCallsList(
    scheduledCalls: List<ScheduledCalled>,
    onCancelSchedule: (Int) -> Unit
) {
    var isScheduledListExpanded by remember { mutableStateOf(false) }

    if (scheduledCalls.size == 1) {
        ScheduledItem(
            scheduledCall = scheduledCalls.first(),
            onClickStop = { onCancelSchedule(scheduledCalls.first().id.toInt()) }
        )
    } else if (scheduledCalls.size > 1) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.inversePrimary)
                .clickable { isScheduledListExpanded = !isScheduledListExpanded }
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Scheduling ${scheduledCalls.size} calls")
            Icon(
                imageVector = if (isScheduledListExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = if (isScheduledListExpanded) "Collapse" else "Expand"
            )
        }

        Spacer(Modifier.height(4.dp))

        AnimatedVisibility(
            visible = isScheduledListExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                scheduledCalls.forEach { call ->
                    ScheduledItem(
                        scheduledCall = call,
                        onClickStop = { onCancelSchedule(call.id.toInt()) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ScheduledCallsListPreview() {
    val scheduledCalls = listOf(
        ScheduledCalled(
            id = 1,
            name = "John Doe",
            number = "1234567890",
            triggerAtMillis = Clock.System.now().toEpochMilliseconds() + 3600000 // 1 hour from now
        ),
        ScheduledCalled(
            id = 2,
            name = "Jane Smith",
            number = "0987654321",
            triggerAtMillis = Clock.System.now().toEpochMilliseconds() + 7200000 // 2 hours from now
        )
    )
    Surface {
        ScheduledCallsList(
            scheduledCalls = scheduledCalls,
            onCancelSchedule = {}
        )
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

@Preview
@Composable
fun ScheduleCallScreenPreview() {
    ScheduleCallScreenContent(
        onBack = {},
        uiState = ScheduleCallState(),
        onEvent = {}
    )
}