package com.ruicomp.cmptemplate.core.ui.prepare_call

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.core.ui.components.ContactAvatar
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.components.ContactItemBig
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PrepareCallSheetContent(
    contact: Contact,
    delayOptions: List<Int>,
    selectedDelayInSeconds: Int,
    onDelaySelected: (Int) -> Unit,
    onStartCall: () -> Unit,
) {
    val timeOptions = delayOptions.map {
        when (it) {
            0 -> stringResource(Res.string.time_option_now)
            5 -> stringResource(Res.string.time_option_5s)
            10 -> stringResource(Res.string.time_option_10s)
            15 -> stringResource(Res.string.time_option_15s)
            else -> "${it}s"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Avatar and Contact Info
        ContactItemBig(contact = contact)

        // Selected Time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            val selectedIndex = delayOptions.indexOf(selectedDelayInSeconds).takeIf { it >= 0 } ?: 0
            val selectedTimeLabel = timeOptions.getOrNull(selectedIndex) ?: "${selectedDelayInSeconds}s"
            Text(
                text = stringResource(Res.string.selected_time_prefix) + selectedTimeLabel,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        // Time Options
        delayOptions.forEachIndexed { idx, delay ->
            val time = timeOptions[idx]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedDelayInSeconds == delay) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .clickable { onDelaySelected(delay) }
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedDelayInSeconds == delay) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(Res.string.selected_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = time,
                        color = if (selectedDelayInSeconds == delay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedDelayInSeconds == delay) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Action Button
        Button(
            onClick = onStartCall,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(Icons.Default.Call, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(Res.string.start_fake_call_button))
        }
    }
}

@Preview
@Composable
fun FakeCallSheetContentPreview() {
    Surface {
        PrepareCallSheetContent(
            contact = Contact(
                id = 1,
                name = "John Doe",
                number = "+1 234 567 890"
            ),
            delayOptions = listOf(0, 5, 10, 15),
            selectedDelayInSeconds = 0,
            onDelaySelected = {},
            onStartCall = {},
        )
    }
}