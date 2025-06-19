package com.ruicomp.cmptemplate.core.ui.prepare_call // Or your preferred package

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepareCallBottomSheet(
    prepareCallUiState: PrepareCallState,
    onPrepareCallEvent: (PrepareCallEvent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(prepareCallUiState.showBottomSheet, sheetState.isVisible) {
        if (!prepareCallUiState.showBottomSheet && sheetState.isVisible) {
            launch { // Use a local coroutine scope for the suspend function
                sheetState.hide()
            }
        }
    }

    if (prepareCallUiState.showBottomSheet && prepareCallUiState.selectedContact != null) {
        ModalBottomSheet(
            onDismissRequest = { onPrepareCallEvent(PrepareCallEvent.HideSheet) },
            sheetState = sheetState,
            // You can add other ModalBottomSheet properties here if needed (e.g., windowInsets, dragHandle)
        ) {
            // Ensure selectedContact is not null, which should be the case if showBottomSheet is true
            PrepareCallSheetContent(
                contact = prepareCallUiState.selectedContact,
                delayOptions = prepareCallUiState.delayOptionsInSeconds,
                selectedDelayInSeconds = prepareCallUiState.selectedDelayInSeconds,
                onDelaySelected = { delay -> onPrepareCallEvent(PrepareCallEvent.SelectTime(delay)) },
                onStartCall = {
                    onPrepareCallEvent(PrepareCallEvent.StartCall)
                    // The LaunchedEffect above will handle hiding the sheet when
                    // prepareCallUiState.showBottomSheet becomes false after the call starts.
                }
            )
        }
    }
}