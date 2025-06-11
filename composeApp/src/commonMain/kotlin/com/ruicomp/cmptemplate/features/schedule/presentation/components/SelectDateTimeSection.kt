package com.ruicomp.cmptemplate.features.schedule.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cmptemplate.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SelectDateTimeSection(
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
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text(stringResource(Res.string.date_label)) },
                    trailingIcon = {
                        IconButton(onClick = onDateSelect) {
                            Icon(Icons.Default.CalendarToday, contentDescription = stringResource(Res.string.select_date_description))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text(stringResource(Res.string.time_label)) },
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

@Preview
@Composable
fun SelectDateTimeSectionPreview() {
    SelectDateTimeSection(
        date = "Oct 15, 2023",
        onDateSelect = {},
        time = "10:30 AM",
        onTimeSelect = {}
    )
} 