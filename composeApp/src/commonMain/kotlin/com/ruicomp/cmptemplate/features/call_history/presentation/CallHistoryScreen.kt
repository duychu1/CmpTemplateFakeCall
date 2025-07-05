package com.ruicomp.cmptemplate.features.call_history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallSheetContent
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.ui.prepare_call.PrepareCallBottomSheet
import com.ruicomp.cmptemplate.features.call_history.presentation.components.CallHistoryItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    onBack: () -> Unit,
    viewModel: CallHistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiPrepareCallState by viewModel.prepareCallManager.uiState.collectAsStateWithLifecycle()

    CallHistoryScreenContent(
        onBack = onBack,
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

    // Prepare call bottom sheet
    PrepareCallBottomSheet(
        prepareCallUiState = uiPrepareCallState,
        onPrepareCallEvent = viewModel.prepareCallManager::onEvent
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CallHistoryScreenContent(
    onBack: () -> Unit,
    uiState: CallHistoryState,
    onEvent: (CallHistoryEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.call_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.no_call_history_message))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.history) { historyItem ->
                        CallHistoryItem(
                            item = historyItem,
                            onRecall = {
                                onEvent(CallHistoryEvent.SelectHistoryForRecall(historyItem))
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CallHistoryScreenContentPreview() {
    CallHistoryScreenContent(
        onBack = {},
        uiState = CallHistoryState(
            history = listOf(
                CallHistory(id = 1, name = "John Doe", number = "123456789", timestamp = 1678886400000L),
                CallHistory(id = 2, name = "Jane Smith", number = "987654321", timestamp = 1678886400000L)
            ),
            isLoading = false,
            error = null,
            showBottomSheet = false,
            selectedHistoryForRecall = null
        ),
        onEvent = {}
    )
}


private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date} ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
}

