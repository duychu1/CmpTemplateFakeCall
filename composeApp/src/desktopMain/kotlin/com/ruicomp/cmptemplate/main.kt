package com.ruicomp.cmptemplate

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CmpTemplate",
        state = rememberWindowState(width = 400.dp, height = 800.dp),
        resizable = false
    ) {
        App()
    }
}