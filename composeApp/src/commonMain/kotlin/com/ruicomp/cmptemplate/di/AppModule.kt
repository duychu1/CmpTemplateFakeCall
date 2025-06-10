package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.core.di.coreModule
import com.ruicomp.cmptemplate.features.call_history.di.callHistoryModule
import com.ruicomp.cmptemplate.features.home.di.homeModule
import com.ruicomp.cmptemplate.features.saved_caller.di.savedCallerModule
import com.ruicomp.cmptemplate.features.schedule.di.scheduleModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    includes(
        coreModule,
        homeModule,
        scheduleModule,
        savedCallerModule,
        callHistoryModule
    )
}

expect val platformModule: Module 