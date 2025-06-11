package com.ruicomp.cmptemplate.features.settings.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.features.settings.presentation.components.SettingsGroup
import com.ruicomp.cmptemplate.features.settings.presentation.components.SettingsItem
import cmptemplate.composeapp.generated.resources.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLanguageClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onRateUsClick: () -> Unit = {},
    onShareAppClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back_button_description)
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
                .padding(vertical = 8.dp)
        ) {
            // Language Setting
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                SettingsItem(
                    icon = Icons.Default.Translate,
                    title = stringResource(Res.string.language_setting),
                    subtitle = "English", // This would come from actual language setting
                    onClick = onLanguageClick
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About Group
            SettingsGroup(title = stringResource(Res.string.about_group_title)) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Shield,
                        title = stringResource(Res.string.privacy_policy),
                        onClick = onPrivacyPolicyClick
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp, end = 24.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = stringResource(Res.string.terms_and_conditions),
                        onClick = onTermsClick
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp, end = 24.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Star,
                        title = stringResource(Res.string.rate_us),
                        onClick = onRateUsClick
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp, end = 24.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Share,
                        title = stringResource(Res.string.share_app),
                        onClick = onShareAppClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onBack = {})
}