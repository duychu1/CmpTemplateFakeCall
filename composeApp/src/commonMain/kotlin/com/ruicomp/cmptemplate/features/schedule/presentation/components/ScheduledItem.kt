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
import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.stop
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.components.ContactItem
import com.ruicomp.cmptemplate.features.call_history.presentation.components.TimeStamp
import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

            Spacer(modifier = Modifier.width(16.dp))
            // button to stop scheduled call
            Button( shape = MaterialTheme.shapes.medium, onClick = onClickStop) {
                Text(text = stringResource(Res.string.stop))
            }
            Spacer(modifier = Modifier.width(10.dp))

        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun ScheduledItemPreview() {
    val scheduledCall = ScheduledCalled(
        id = 1L,
        name = "John Doe",
        number = "1234567890",
        avatarUrl = null,
        triggerAtMillis = Clock.System.now().toEpochMilliseconds() + 3600000 // 1 hour from now
    )
    ScheduledItem(scheduledCall = scheduledCall)
}
