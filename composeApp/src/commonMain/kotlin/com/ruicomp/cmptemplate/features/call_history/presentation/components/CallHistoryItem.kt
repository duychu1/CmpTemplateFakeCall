package com.ruicomp.cmptemplate.features.call_history.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CallHistoryItem(
    item: CallHistory,
    onRecall: (CallHistory) -> Unit
) {
    ContactItem(
        contact = item.asContact(),
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Timestamp
                TimeStamp(item.timestamp)

                // icon button recall
                IconButton(onClick = { onRecall(item) }) {
                    Icon(Icons.Filled.Call, contentDescription = "Recall")
                }
            }
        }
    )
}

@Composable
fun RowScope.TimeStamp(timestamp: Long) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // day and month
        Text(
            text = formatTimestamp(timestamp).split(" ")[0].split("-").drop(1)
                .joinToString("-"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // time
        Text(
            text = formatTimestamp(timestamp).split(" ")[1],
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            timestamp = Clock.System.now().toEpochMilliseconds()
        ),
        onRecall = {}
    )
}