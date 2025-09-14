package com.ruicomp.cmptemplate

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.common.control.BaseApplication
import com.common.control.BuildConfig
import com.common.control.model.PurchaseModel
import com.ruicomp.cmptemplate.di.initKoin
import com.ruicomp.cmptemplate.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CmpTemplateApplication(
    override val adjustAppToken: String? = null,
    override val openAppAdId: String= "ca-app-pub-3940256099942544/9257395921",
    override val policyUrl: String = "",
    override val subjectSupport: String = "",
    override val emailSupport: String  = "",
    override val isInitBilling: Boolean = true,
    override val purchaseList: List<PurchaseModel> = listOf<PurchaseModel>(PurchaseModel(PRODUCT_SUBS, PurchaseModel.ProductType.SUBS)),
    override val purchaseListInApp: List<PurchaseModel> = listOf<PurchaseModel>(PurchaseModel(PRODUCT_LIFETIME, PurchaseModel.ProductType.INAPP)),
    override val firstActForOpenApp: Class<*> = MainActivity::class.java
) : BaseApplication(), Application.ActivityLifecycleCallbacks {
    companion object{
        const val PRODUCT_SUBS = "sub"
        const val PRODUCT_LIFETIME = "lifetime"
    }
    val TAG = "CmpTemplateApplication"
    private val lsActivity = ArrayList<Activity>()

    override fun onApplicationCreate() {
        initKoin {
            androidLogger()
            androidContext(this@CmpTemplateApplication)
            modules(platformModule)
        }

        registerActivityLifecycleCallbacks(this)
        EventLogger.init(applicationContext)
    }

    override fun hasAdjust(): Boolean {
        return false
    }

    override fun hasAds(): Boolean {
        return true
    }

    override fun isShowDialogLoadingAd(): Boolean {
        return true
    }

    override fun isShowAdsTest(): Boolean {
//        return BuildConfig.TEST_AD || BuildConfig.DEBUG
        return BuildConfig.DEBUG
    }

    override fun enableAdsResume(): Boolean {
        return false
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }


} 