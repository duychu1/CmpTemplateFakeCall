package com.ruicomp.cmptemplate.features.schedule.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import com.ruicomp.cmptemplate.features.call_history.presentation.components.TimeStamp
import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScheduledItem(scheduledCall: ScheduledCalled, onClickStop: () -> Unit = {}) {
    ContactItem(
        contact = Contact(
            id = scheduledCall.id,
            name = scheduledCall.name,
            number = scheduledCall.number
            // If your Contact model supports avatarUrl, you can add:
            // avatarUrl = scheduledCall.avatarUrl
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Timestamp
            TimeStamp(scheduledCall.triggerAtMillis)

            Spacer(modifier = Modifier.width(8.dp))
            // button to stop scheduled call
            Button(onClick = onClickStop) {
                Text("Stop")
            }
        }
    }
}

@Preview
@Composable
fun ScheduledItemPreview() {
    val scheduledCall = ScheduledCalled(
        id = 1L,
        name = "John Doe",
        number = "1234567890",
        avatarUrl = null,
        triggerAtMillis = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + 3600000 // 1 hour from now
    )
    ScheduledItem(scheduledCall = scheduledCall)
}
