package com.ruicomp.cmptemplate.features.language_setting.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cmptemplate.composeapp.generated.resources.Res
import cmptemplate.composeapp.generated.resources.back_button_description
import cmptemplate.composeapp.generated.resources.language_setting_title
import com.ruicomp.cmptemplate.features.language_setting.data.provider.LanguageProvider
import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language
import com.ruicomp.cmptemplate.features.language_setting.presentation.components.LanguageItem
import com.ruicomp.cmptemplate.features.language_setting.presentation.components.LanguageList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun LanguageSettingScreen(
    onBack: () -> Unit = {},
    viewModel: LanguageSettingViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LanguageSettingScreenContent(
        uiState = uiState,
        event = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingScreenContent(
    uiState: LanguageSettingState,
    event: (LanguageSettingEvent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.language_setting_title)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LanguageList(
                        languages = uiState.languages,
                        onSelect = { event(LanguageSettingEvent.SelectLanguage(it)) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LanguageSettingScreenPreview() {
    LanguageSettingScreenContent(
        onBack = {},
        uiState = LanguageSettingState(
            languages = LanguageProvider.languages,
            isLoading = false,
            error = null
        ),
        event = {}
    )
}


