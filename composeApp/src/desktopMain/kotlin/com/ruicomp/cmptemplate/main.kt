package com.ruicomp.cmptemplate

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ruicomp.cmptemplate.app.App
import com.ruicomp.cmptemplate.di.initKoin
import com.ruicomp.cmptemplate.di.platformModule

fun main() = application {
    initKoin {
        modules(platformModule)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "CmpTemplate",
        state = rememberWindowState(width = 400.dp, height = 800.dp),
        resizable = false
    ) {
        App()
    }
}