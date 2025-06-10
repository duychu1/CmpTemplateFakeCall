package com.ruicomp.cmptemplate

import androidx.compose.ui.window.ComposeUIViewController
import com.ruicomp.cmptemplate.app.App
import com.ruicomp.cmptemplate.di.initKoin
import com.ruicomp.cmptemplate.di.platformModule

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(platformModule)
        }
    }
) {
    App()
}