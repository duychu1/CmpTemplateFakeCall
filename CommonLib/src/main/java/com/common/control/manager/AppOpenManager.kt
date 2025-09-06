
package com.common.control.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.common.control.BaseApplicationKt
import com.common.control.dialog.WelcomeBackDialog
import com.common.control.interfaces.AdCallback
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenManagerKt private constructor() : Application.ActivityLifecycleCallbacks, LifecycleEventObserver {
    
    companion object {
        private const val TAG = "AppOpenManagerKt"
        private const val AD_TIMEOUT_HOURS = 4L
        private const val SHOW_AD_DELAY_MS = 300L
        private const val DISMISS_AD_DELAY_MS = 1000L
        
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AppOpenManagerKt? = null
        
        fun getInstance(): AppOpenManagerKt {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppOpenManagerKt().also { INSTANCE = it }
            }
        }
    }
    
    // Properties
    private var appResumeAd: AppOpenAd? = null
    private var appResumeAdId: String = ""
    private var currentActivity: Activity? = null
    private var myApplication: Application? = null
    private var loadTime: Long = 0
    private var dialog: WelcomeBackDialog? = null
    private var timeShowLoading: Long = 100
    private var startLoading: Boolean = false
    
    // State flags
    var isShowingAd: Boolean = false
        private set
    private var isInitialized: Boolean = false
    private var isAppResumeEnabled: Boolean = true
    
    // Collections
    private val disabledAppOpenList = mutableListOf<Class<*>>()
    
    // Dependencies
    private val trackRevenueSolar = TrackRevenueSolar()
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // Public getters
    fun getAppResumeAd(): AppOpenAd? = appResumeAd
    fun getAppResumeAdId(): String = appResumeAdId
    fun getCurrentActivity(): Activity? = currentActivity
    fun getMyApplication(): Application? = myApplication
    fun isShowingOpenAd(): Boolean = isShowingAd
    fun isAppResumeEnabled(): Boolean = isAppResumeEnabled
    fun getDisabledAppOpenList(): List<Class<*>> = disabledAppOpenList.toList()
    fun getLoadTime(): Long = loadTime
    fun isInitialized(): Boolean = isInitialized
    
    // Initialization
    fun init(application: Application, appOpenAdId: String) {
        isInitialized = true
        myApplication = application
        appResumeAdId = appOpenAdId
        
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    // Configuration methods
    fun setAppResumeAdId(appResumeAdId: String) {
        this.appResumeAdId = appResumeAdId
    }
    
    fun setTimeShowLoading(timeShowLoading: Long) {
        this.timeShowLoading = timeShowLoading
    }
    
    fun disableAppResumeWithActivity(activityClass: Class<*>) {
        Log.d(TAG, "disableAppResumeWithActivity: ${activityClass.name}")
        if (!disabledAppOpenList.contains(activityClass)) {
            disabledAppOpenList.add(activityClass)
        }
    }
    
    fun enableAppResumeWithActivity(activityClass: Class<*>) {
        Log.d(TAG, "enableAppResumeWithActivity: ${activityClass.name}")
        disabledAppOpenList.remove(activityClass)
    }
    
    fun disableAppResume() {
        isAppResumeEnabled = false
    }
    
    fun enableAppResume() {
        isAppResumeEnabled = true
    }
    
    // Ad loading methods
    fun fetchAd() {
        fetchAd(null)
    }
    
    fun fetchAd(callback: AdCallback?) {
        Log.d("AppOpenLogger", "fetchAd:")
        
        if (isAdAvailable()) {
            callback?.onAdLoaded()
            return
        }
        
        val request = AdmobManagerKt.getInstance().adRequest ?: return
        
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appResumeAd = ad
                loadTime = Date().time
                ad.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueOpenAppAd(adValue, ad)
                }
                callback?.onAdLoaded()
            }
            
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                appResumeAd = null
                callback?.onAdFailedToLoad(loadAdError)
            }
        }
        
        AdmobManagerKt.getInstance().log("Request OpenAd: $appResumeAdId")
        Log.d("AppOpenLogger", "request: $appResumeAdId")
        
        myApplication?.let { app ->
            AppOpenAd.load(app, appResumeAdId, request, loadCallback)
        }
    }
    
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour = 3600000
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }
    
    fun isAdAvailable(): Boolean {
        return appResumeAd != null && wasLoadTimeLessThanNHoursAgo(AD_TIMEOUT_HOURS)
    }
    
    // Ad showing methods
    fun showAdIfAvailable() {
        val activity = currentActivity
        if (activity == null || !AdmobManagerKt.getInstance().hasAds) {
            return
        }
        
        Log.d(TAG, "showAdIfAvailable: ${ProcessLifecycleOwner.get().lifecycle.currentState}")
        
        if (!ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "showAdIfAvailable: return")
            return
        }
        
        if (isAdAvailable()) {
            Log.d(TAG, "Will show ad.")
            val fullScreenContentCallback = createFullScreenContentCallback()
            showAdsWithLoading(fullScreenContentCallback)
        }
    }
    
    private fun createFullScreenContentCallback() = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            appResumeAd = null
            mainHandler.postDelayed({
                isShowingAd = false
            }, DISMISS_AD_DELAY_MS)
            dismissDialogLoading()
        }
        
        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            dismissDialogLoading()
            appResumeAd = null
            fetchAd()
        }
        
        override fun onAdShowedFullScreenContent() {
            isShowingAd = true
        }
    }
    
    private fun dismissDialogLoading() {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showAdsWithLoading(fullScreenContentCallback: FullScreenContentCallback) {
        if (isShowingAd || appResumeAd == null) {
            return
        }
        
        val activity = currentActivity ?: return
        
        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            try {
                dismissDialogLoading()
                dialog = WelcomeBackDialog(activity)
                dialog?.show()
            } catch (e: Exception) {
                dialog = null
                e.printStackTrace()
            }
            
            appResumeAd?.setFullScreenContentCallback(fullScreenContentCallback)
            
            mainHandler.postDelayed({
                if (dialog?.isShowing == true) {
                    AdmobManagerKt.getInstance().log("Show OpenAd: $appResumeAdId")
                    appResumeAd?.show(activity)
                }
            }, timeShowLoading)
        }
    }
    
    // Activity lifecycle callbacks
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // No implementation needed
    }
    
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        
        if (!startLoading) {
            val app = myApplication
            if (app is BaseApplicationKt) {
                if (app.firstActForOpenApp.name == activity.javaClass.name) {
                    startLoading = true
                }
            }
        }
        
        if (startLoading) {
            fetchAd()
        }
    }
    
    override fun onActivityPaused(activity: Activity) {
        // No implementation needed
    }
    
    override fun onActivityStopped(activity: Activity) {
        // No implementation needed
    }
    
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // No implementation needed
    }
    
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }
    
    // Lifecycle observer
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            onResume()
        }
    }
    
    private fun onResume() {
        if (!isAppResumeEnabled) {
            Log.d(TAG, "onResume: app resume is disabled")
            return
        }
        
        val activity = currentActivity ?: return
        
        for (disabledActivity in disabledAppOpenList) {
            if (disabledActivity.name == activity.javaClass.name) {
                Log.d(TAG, "onStart: activity is disabled")
                return
            }
        }
        
        if (activity.javaClass.name != AdActivity::class.java.name) {
            mainHandler.postDelayed({
                showAdIfAvailable()
            }, SHOW_AD_DELAY_MS)
        }
    }
    
    fun hideNativeOrBannerWhenShowOpenApp(activity: Activity, frAd: FrameLayout) {
        // TODO: Implement if needed - currently no-op as in original
    }
}
