package com.ruicomp.cmptemplate.core.ui.prepare_call

import org.koin.dsl.module

val prepareCallModule = module {
    factory { PrepareCallManager(get(), get()) }
}