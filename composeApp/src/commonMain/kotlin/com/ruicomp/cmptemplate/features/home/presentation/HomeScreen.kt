package com.ruicomp.cmptemplate.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.cmptemplate.features.home.presentation.components.FeatureCard
import cmptemplate.composeapp.generated.resources.*
import com.ruicomp.cmptemplate.core.permissions.phoneaccount.PhoneAccountPermissionEvent
import com.ruicomp.cmptemplate.core.permissions.presentation.components.CustomAlertDialog
import com.ruicomp.cmptemplate.core.permissions.presentation.components.PermissionAware
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
    val uiHomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiBasePermissionState by viewModel.basePermissionManager.uiBasePermissionState.collectAsStateWithLifecycle()
    val uiPhoneAccountPermissionState by viewModel.phoneAccountPermissionManager.permissionState.collectAsStateWithLifecycle()

    HomeScreenContent(
        onScheduleCall = onScheduleCall,
        onSavedCaller = onSavedCaller,
        onCallHistory = onCallHistory,
        onSettingsClick = onSettingsClick,
        onEvent = viewModel::onEvent,
        state = uiHomeState
    )


    val phonePermission = HomeViewModel.READ_PHONE_NUMBERS_PERMISSION
    if (uiBasePermissionState.permissionAwareStates[phonePermission] == true) {
        PermissionAware(
            permission = phonePermission, // Pass the specific permission string
            permissionNameDialog = stringResource(Res.string.PHONE), // This could also be made dynamic
            onShowPermissionAwareChange = { show -> viewModel.basePermissionManager.onShowPermissionAwareChange(phonePermission, show) },
            onPermissionStatusChecked = { status -> viewModel.basePermissionManager.onPermissionStatusChecked(phonePermission, status) },
            onPermissionResult = { status -> viewModel.basePermissionManager.onPermissionResult(phonePermission, status) },
        )
    }

    if (uiPhoneAccountPermissionState.showPhoneAccountRationaleSetting) {
        CustomAlertDialog(
            agreeText = stringResource(Res.string.settings_title),
            title = stringResource(Res.string.permission_required_title),
            message = stringResource(Res.string.make_own_call_permission_message),
            onAgree = { viewModel.phoneAccountPermissionManager.onEvent(PhoneAccountPermissionEvent.AgreeRationalPermissionDialogClicked) },
            onCancel = { viewModel.phoneAccountPermissionManager.onEvent(PhoneAccountPermissionEvent.CancelRationalPermissionDialogClicked) },
            onDismiss = { viewModel.phoneAccountPermissionManager.onEvent(PhoneAccountPermissionEvent.RationalPermissionDialogDismissed) }
        )
    }
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
                FeatureCard(
                    stringResource(Res.string.feature_saved_caller),
                    Icons.Default.People,
                    onSavedCaller
                )
                FeatureCard(
                    stringResource(Res.string.feature_call_history),
                    Icons.Default.History,
                    onCallHistory
                )
            }
        }

    }
}

@Preview
@Composable
fun HomeScreenContentPreview() {
    HomeScreenContent(
        onScheduleCall = {},
        onSavedCaller = {},
        onCallHistory = {},
        onSettingsClick = {},
        onEvent = {},
        state = HomeState()
    )
}
