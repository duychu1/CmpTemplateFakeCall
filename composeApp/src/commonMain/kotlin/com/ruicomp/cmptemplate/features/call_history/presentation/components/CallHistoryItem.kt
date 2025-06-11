package com.ruicomp.cmptemplate.features.call_history.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.ui.components.ContactAvatar
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CallHistoryItem(
    item: CallHistory,
    onRecall: (CallHistory) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = item.name,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = item.number, style = MaterialTheme.typography.bodyMedium)
            }

            // Timestamp
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // day and month
                Text(
                    text = formatTimestamp(item.timestamp).split(" ")[0].split("-").drop(1).joinToString("-"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // time
                Text(
                    text = formatTimestamp(item.timestamp).split(" ")[1],
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // icon button recall
            IconButton(onClick = { onRecall(item) }) {
                Icon(Icons.Filled.Call, contentDescription = "Recall")
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date} ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
}

@Preview
@Composable
fun CallHistoryItemPreview() {
    CallHistoryItem(
        item = CallHistory(
            id = 1,
            name = "John Doe",
            number = "+1 234 567 890",
            timestamp = System.currentTimeMillis()
        ),
        onRecall = {}
    )
} 