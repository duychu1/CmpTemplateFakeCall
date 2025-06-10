package com.ruicomp.cmptemplate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.ruicomp.cmptemplate.di.appModule
import com.ruicomp.cmptemplate.di.platformModule
import com.ruicomp.cmptemplate.presentation.navigation.NavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin

@Composable
@Preview
fun App() {
    startKoin {
        modules(appModule, platformModule)
    }

    MaterialTheme {
        NavGraph()
    }
}