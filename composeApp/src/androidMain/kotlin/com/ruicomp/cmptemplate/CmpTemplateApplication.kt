package com.ruicomp.cmptemplate

import android.app.Application
import com.ruicomp.cmptemplate.di.initKoin
import com.ruicomp.cmptemplate.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CmpTemplateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CmpTemplateApplication)
            modules(platformModule)
        }
    }
} 