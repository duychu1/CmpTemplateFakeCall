package com.ruicomp.cmptemplate.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.cmptemplate.features.home.presentation.components.FeatureCard
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.permissions.presentation.components.PermissionAware
import com.ruicomp.cmptemplate.core.permissions.presentation.components.PermissionRationaleDialog
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onCallNow: () -> Unit,
    onScheduleCall: () -> Unit,
    onSavedCaller: () -> Unit,
    onCallHistory: () -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        onScheduleCall = onScheduleCall,
        onSavedCaller = onSavedCaller,
        onCallHistory = onCallHistory,
        onSettingsClick = onSettingsClick,
        onEvent = viewModel::onEvent,
        state = uiState
    )

    // This is the Android permission constant
    val phoneNumberPermission = "android.permission.READ_PHONE_NUMBERS"

    // Observe the state from the ViewModel
    val uiPermissionState by viewModel.uiPermissionState.collectAsStateWithLifecycle()

    PermissionAware(
        permission = phoneNumberPermission,
        permissionNameDialog = "Phone",
        permissionState = uiPermissionState, // Pass the state down
        onPermissionStatusChecked = { status -> // Pass events up to the ViewModel
            viewModel.onPermissionStatusChecked(phoneNumberPermission, status)
        },
        onPermissionResult = { isGranted, shouldShowRationale -> // Pass events up
            viewModel.onPermissionResult(isGranted, shouldShowRationale)
        },
        grantedContent = {
            // This UI is unchanged
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Phone number permission granted! (From ViewModel)", textAlign = TextAlign.Center)
            }
        },
        rationaleContent = { onRequestPermission ->
            // This UI is unchanged
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "To automatically fill your phone number, we need permission to read it. Please grant the permission.",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRequestPermission) {
                    Text("Request Permission")
                }
            }
        },
        permanentlyDeniedContent = { onOpenSettings ->
            // This UI is unchanged
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Phone number permission was permanently denied. Please enable it in the app settings.",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onOpenSettings) {
                    Text("Open Settings")
                }
            }
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    onScheduleCall: () -> Unit,
    onSavedCaller: () -> Unit,
    onCallHistory: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onEvent: (HomeEvent) -> Unit = {},
    state: HomeState = HomeState()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.settings_title)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureCard(
                    stringResource(Res.string.feature_call_now),
                    Icons.Default.Call,
                    onClick = { onEvent(HomeEvent.CallNowClicked) }
                )
                FeatureCard(
                    stringResource(Res.string.feature_schedule_call),
                    Icons.Default.Schedule,
                    onScheduleCall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureCard(stringResource(Res.string.feature_saved_caller), Icons.Default.People, onSavedCaller)
                FeatureCard(stringResource(Res.string.feature_call_history), Icons.Default.History, onCallHistory)
            }
        }
        if (state.shouldShowPermissionRationaleDialog) {
            PermissionRationaleDialog(
                title = stringResource(Res.string.permission_required_title),
                message = stringResource(Res.string.make_own_call_permission_message),
                onAgree = { onEvent(HomeEvent.AgreeRationalPermissionDialogClicked) },
                onCancel = { onEvent(HomeEvent.CancelRationalPermissionDialogClicked) },
                onDismiss = { onEvent(HomeEvent.RationalPermissionDialogDismissed) }
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onCallNow = {},
        onScheduleCall = {},
        onSavedCaller = {},
        onCallHistory = {},
        onSettingsClick = {}
    )
} 