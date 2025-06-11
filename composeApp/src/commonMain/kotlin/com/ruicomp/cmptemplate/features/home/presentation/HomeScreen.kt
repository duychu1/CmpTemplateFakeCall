package com.ruicomp.cmptemplate.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.features.home.presentation.components.FeatureCard
import cmptemplate.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCallNow: () -> Unit,
    onScheduleCall: () -> Unit,
    onSavedCaller: () -> Unit,
    onCallHistory: () -> Unit,
    onSettingsClick: () -> Unit = {}
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
                FeatureCard(stringResource(Res.string.feature_call_now), Icons.Default.Call, onCallNow)
                FeatureCard(stringResource(Res.string.feature_schedule_call), Icons.Default.Schedule, onScheduleCall)
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