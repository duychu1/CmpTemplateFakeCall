package com.ruicomp.cmptemplate.di

import com.ruicomp.cmptemplate.core.database.di.databaseModule
import com.ruicomp.cmptemplate.core.datastore.di.dataStoreModule
import com.ruicomp.cmptemplate.core.permissions.permissionModule
import com.ruicomp.cmptemplate.features.call_history.di.callHistoryModule
import com.ruicomp.cmptemplate.features.home.di.homeModule
import com.ruicomp.cmptemplate.features.language_setting.di.languageSettingModule
import com.ruicomp.cmptemplate.features.saved_caller.di.savedCallerModule
import com.ruicomp.cmptemplate.features.schedule.di.scheduleModule
import com.ruicomp.cmptemplate.features.settings.di.settingsModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    includes(
        databaseModule,
        dataStoreModule,
        permissionModule,
        homeModule,
        scheduleModule,
        savedCallerModule,
        callHistoryModule,
        settingsModule,
        languageSettingModule
    )
}

expect val platformModule: Module 