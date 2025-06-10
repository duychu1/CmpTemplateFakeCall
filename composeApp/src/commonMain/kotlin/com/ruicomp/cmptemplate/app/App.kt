package com.ruicomp.cmptemplate.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.ruicomp.cmptemplate.navigation.NavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        NavGraph()
    }
} 