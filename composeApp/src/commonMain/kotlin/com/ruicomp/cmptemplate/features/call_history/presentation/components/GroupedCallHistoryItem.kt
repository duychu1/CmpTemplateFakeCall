package com.ruicomp.cmptemplate.features.call_history.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GroupedCallHistoryItem(
    historyItems: List<CallHistory>,
    onRecall: (CallHistory) -> Unit,
    onDelete: (CallHistory) -> Unit = {},
    defaultExpanded: Boolean = false,
) {
    val mostRecentItem = historyItems.first()
    var isExpanded by remember { mutableStateOf(defaultExpanded) }

    Column(modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant)) {
        ContactItem(
            contact = mostRecentItem.asContact(),
            onClick = { isExpanded = !isExpanded },
            actions = {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    if (historyItems.size > 1) {
                        Text(text = "(${historyItems.size})", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    DeleteIconButton(onClick = { onDelete(mostRecentItem) })

                    // Timestamp
                    TimeStamp(mostRecentItem.timestamp)

                    // icon button recall
                    IconButton(onClick = { onRecall(mostRecentItem) }) {
                        Icon(Icons.Filled.Call, contentDescription = "Recall")
                    }
                }
            }
        )

        // Expanded list of individual calls
        AnimatedVisibility(visible = isExpanded && historyItems.size > 1) {
            Column() {

                historyItems.drop(1).forEach { historyItem ->
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    CallHistoryItem(
                        item = historyItem,
                        onRecall = { onRecall(historyItem) },
                        onDelete = { onDelete(historyItem) },
//                            showContactInfo = false // Hide name/number as it's in the header
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GroupedCallHistoryItemPreview() {
    GroupedCallHistoryItem(
        historyItems = listOf(
            CallHistory(id = 1, name = "John Doe", number = "+123456789", timestamp = 1672531200000L),
            CallHistory(id = 2, name = "John Doe", number = "+123456789", timestamp = 1672534800000L),
        ),
        onRecall = {}
    )
}
@Preview
@Composable
fun GroupedCallHistoryItemExpandedPreview() {
    GroupedCallHistoryItem(
        historyItems = listOf(
            CallHistory(id = 1, name = "John Doe", number = "+123456789", timestamp = 1672531200000L),
            CallHistory(id = 2, name = "John Doe", number = "+123456789", timestamp = 1672534800000L),
            CallHistory(id = 3, name = "John Doe", number = "+123456789", timestamp = 1672533300000L),

            ),
        defaultExpanded = true,
        onRecall = {},
    )
}