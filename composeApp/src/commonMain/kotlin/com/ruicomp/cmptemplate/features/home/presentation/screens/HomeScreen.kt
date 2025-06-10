package com.ruicomp.cmptemplate.features.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.features.home.presentation.components.FeatureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCallNow: () -> Unit,
    onScheduleCall: () -> Unit,
    onSavedCaller: () -> Unit,
    onCallHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fake Call") })
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
                FeatureCard("Call Now", Icons.Default.Call, onCallNow)
                FeatureCard("Schedule Call", Icons.Default.Schedule, onScheduleCall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureCard("Saved Caller", Icons.Default.People, onSavedCaller)
                FeatureCard("Call History", Icons.Default.History, onCallHistory)
            }
        }
    }
} 