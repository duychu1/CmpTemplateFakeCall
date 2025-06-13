package com.ruicomp.cmptemplate

import android.os.Build
import com.ruicomp.cmptemplate.features.fakecall.ActualFakeCallManager

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()