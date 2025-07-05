package com.ruicomp.cmptemplate.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ruicomp.cmptemplate.navigation.NavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    appViewModel: AppViewModel = koinInject()
) {
    val languageInitialized by appViewModel.isLocalizationInitialized.collectAsState()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            if (languageInitialized) {
                NavGraph()
            } else {
                // Optional: Show a loading indicator while language is being initialized
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
} 