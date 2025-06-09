package com.ruicomp.cmptemplate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.ruicomp.cmptemplate.presentation.navigation.NavGraph
import org.koin.core.context.startKoin
import com.ruicomp.cmptemplate.di.appModule
import com.ruicomp.cmptemplate.di.platformModule

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