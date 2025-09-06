package com.common.control

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.common.control.manager.AdmobManagerKt
import com.common.control.manager.AppOpenManagerKt
import com.common.control.manager.PurchaseManagerKt
import com.common.control.model.PurchaseModel
import com.common.control.utils.AppUtilsKt
import com.google.android.datatransport.backend.cct.BuildConfig

abstract class BaseApplicationKt : Application() {

    override fun onCreate() {
        super.onCreate()

//        SharePrefUtils.getInstance().init(this)
        AdmobManagerKt.getInstance().init(this, if (isShowAdsTest()) AdmobManagerKt.getInstance().getDeviceId(this) else "")
        AdmobManagerKt.getInstance().hasAds = hasAds()

        onApplicationCreate()

        if (enableAdsResume()) {
            AppOpenManagerKt.getInstance().init(this, openAppAdId)
        }
        AdmobManagerKt.getInstance().isShowLoadingDialog = isShowDialogLoadingAd()

        AppUtilsKt.policyUrl = policyUrl
        AppUtilsKt.subject = subjectSupport
        AppUtilsKt.email = emailSupport
        AdmobManagerKt.getInstance().hasLog = true
        if (isInitBilling) {
//            PurchaseManager.getInstance().init(this, purchaseList)
            PurchaseManagerKt.getInstance().init(this, purchaseListInApp) // Assuming this is the preferred one
        }

        if (hasAdjust()) {
            AdmobManagerKt.getInstance().hasAdjust = true
            val environment = if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
            val config = AdjustConfig(this, adjustAppToken, environment)
            config.setLogLevel(LogLevel.VERBOSE)
            //remove log
//        config.setLogLevel(LogLevel.SUPRESS)
//            Adjust.onCreate(config) // Adjust.onCreate(config) should be called here based on original logic
            registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
        }
    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    protected abstract fun onApplicationCreate()
    protected abstract fun hasAdjust(): Boolean
    protected abstract val adjustAppToken: String
    protected abstract fun hasAds(): Boolean
    protected abstract fun isShowDialogLoadingAd(): Boolean
    protected abstract fun isShowAdsTest(): Boolean
    protected abstract fun enableAdsResume(): Boolean
    protected abstract val openAppAdId: String
    protected abstract val policyUrl: String
    protected abstract val subjectSupport: String
    protected abstract val emailSupport: String
    protected abstract val isInitBilling: Boolean
    protected abstract val purchaseList: List<PurchaseModel> // Kept for completeness, though commented out in usage
    protected abstract val purchaseListInApp: List<PurchaseModel>

    abstract val firstActForOpenApp: Class<*> // Changed to val for Kotlin property style
}
