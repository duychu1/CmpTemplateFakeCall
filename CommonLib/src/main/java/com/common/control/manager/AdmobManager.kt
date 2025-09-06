package com.common.control.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.common.control.R
import com.common.control.dialog.PrepareLoadingAdsDialogKt
import com.common.control.interfaces.AdCallback
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections
import java.util.Locale

class AdmobManagerKt private constructor() {
    val errAd = LoadAdError(2, "No Ad", "", null, null)
    var hasAds = true
    var isShowLoadingDialog = false
    var customTimeLoadingDialog: Long = 1500
    var hasAdjust = false // Consider if this property is still needed or how it's used
    var hasLog = false
    var trackRevenueSolar: TrackRevenueSolar = TrackRevenueSolar() // Ensure TrackRevenueSolar is also available in Kotlin or Java

    fun init(context: Context, deviceID: String) {
        try {
            log("init: Admob")
            MobileAds.initialize(context) { }
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList(deviceID)).build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initializeFacebookAds(context)
    }

    private fun initializeFacebookAds(context: Context) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            AudienceNetworkAds.buildInitSettings(context).withInitListener { }.initialize()
        }
    }

    val adRequest: AdRequest?
        get() {
            if (!hasAds || PurchaseManagerKt.getInstance().isPurchased() ) {
                return null
            }
            return AdRequest.Builder().build()
        }

    fun loadInterAds(context: Activity, id: String, callback: AdCallback?) {
        log("request inter: $id")
        val request = adRequest
        if (request == null) {
            callback?.onAdFailedToLoad(errAd)
            return
        }
        InterstitialAd.load(
            context,
            id,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Interstitial ad failed to load: ${'$'}{loadAdError.message}")
                    callback?.onAdFailedToLoad(loadAdError)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    log("Interstitial ad loaded: $id")
                    callback?.onResultInterstitialAd(interstitialAd)
                    interstitialAd.setOnPaidEventListener { adValue ->
                        trackRevenueSolar.trackRevenueInterSolar(adValue, interstitialAd)
                    }
                }
            })
    }

    fun loadInterAds(context: Context, id: String, callback: AdCallback?) { // Overload for non-Activity context if needed
        log("request inter (context): $id")
        val request = adRequest
        if (request == null) {
            callback?.onAdFailedToLoad(errAd)
            return
        }
        InterstitialAd.load(
            context,
            id,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Interstitial ad (context) failed to load: ${'$'}{loadAdError.message}")
                    callback?.onAdFailedToLoad(loadAdError)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    log("Interstitial ad (context) loaded: $id")
                    callback?.onResultInterstitialAd(interstitialAd)
                    interstitialAd.setOnPaidEventListener { adValue ->
                        trackRevenueSolar.trackRevenueInterSolar(adValue, interstitialAd)
                    }
                }
            })
    }

    fun log(s: String) {
        if (hasLog) {
            Log.d("AdmobManagerKt_KT", s) // Added _KT to differentiate from potential Java logs
        }
    }

    fun loadAndShowSplashOpenApp(context: Context, id: String, callback: AdCallback?) {
        log("loadAndShowSplashOpenApp: $id")
        val request = adRequest
        if (request == null) {
            callback?.onAdFailedToLoad(errAd)
            callback?.onNextScreen()
            return
        }

        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                log("Splash OpenAppAd loaded: $id")
                appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        log("Splash OpenAppAd dismissed.")
                        callback?.onAdClosed()
                        callback?.onNextScreen()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        log("Splash OpenAppAd failed to show: ${'$'}{adError.message}")
                        callback?.onNextScreen() // Ensure next screen is called
                    }
                }
                if (context is Activity && !context.isFinishing) {
                    appOpenAd.show(context)
                    appOpenAd.setOnPaidEventListener { adValue ->
                        trackRevenueSolar.trackRevenueOpenAppAd(adValue, appOpenAd)
                    }
                } else {
                    log("Splash OpenAppAd: Context is not a valid Activity or is finishing.")
                    callback?.onNextScreen()
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                log("Splash OpenAppAd failed to load: ${'$'}{loadAdError.message}")
                callback?.onAdFailedToLoad(loadAdError) // Propagate the error
                callback?.onNextScreen()
            }
        }
        AppOpenAd.load(context, id, request, loadCallback)
    }

    fun loadAppOpenAd(context: Context, id: String, callback: AdCallback) {
        log("loadAppOpenAd: $id")
        val request = adRequest
        if (request == null) {
            callback.onAdFailedToLoad(errAd)
            callback.onNextScreen() // Ensure next screen is called
            return
        }
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                log("AppOpenAd loaded: $id")
                callback.onResultOpenAppAd(appOpenAd)
                 appOpenAd.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueOpenAppAd(adValue, appOpenAd)
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                log("AppOpenAd failed to load: ${'$'}{loadAdError.message}")
                callback.onAdFailedToLoad(loadAdError)
                callback.onNextScreen() // Ensure next screen is called
            }
        }
        AppOpenAd.load(context, id, request, loadCallback)
    }

    fun showAppOpenAd(activity: Activity, appOpenAd: AppOpenAd?, callback: AdCallback?) {
        if (appOpenAd == null) {
            log("Cannot show AppOpenAd: ad is null")
            callback?.onAdFailedToShowFullScreenContent(errAd) 
            callback?.onNextScreen()
            return
        }
        log("showAppOpenAd")
        appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                log("AppOpenAd dismissed.")
                callback?.onAdClosed()
                callback?.onNextScreen()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                log("AppOpenAd failed to show: ${'$'}{adError.message}")
                callback?.onNextScreen()
            }

             override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                log("AppOpenAd showed full screen.")
                callback?.onAdShowedFullScreenContent()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                log("AppOpenAd impression.")
                callback?.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                log("AppOpenAd clicked.")
                callback?.onAdClicked()
            }
        }
        if (!activity.isFinishing) {
            appOpenAd.show(activity)
        } else {
             log("AppOpenAd: Activity is finishing, cannot show ad.")
            callback?.onNextScreen()
        }
    }


    fun showInterstitial(
        context: Activity,
        interstitialAd: InterstitialAd?, // Made nullable to match the check
        hasLoadingWhenShow: Boolean,
        callback: AdCallback?, // Made nullable to match checks
    ) {
        if (interstitialAd == null || PurchaseManagerKt.getInstance().isPurchased()) {
            callback?.let {
                it.onAdFailedToShowFullScreenContent(errAd) // Assuming errAd is accessible
                it.onAdClosed()
                it.onNextScreen()
            }
            return
        }

        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                context.sendBroadcast(Intent(PrepareLoadingAdsDialogKt.ACTION_DISMISS_DIALOG))
                if (AppOpenManagerKt.getInstance().isInitialized()) {
                    AppOpenManagerKt.getInstance().enableAppResume()
                }
                callback?.let {
                    it.onAdClosed()
                    it.onNextScreen()
                    it.onClickClose()
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) { // Note: AdError is non-null here in the original
                if (AppOpenManagerKt.getInstance().isInitialized()) {
                    AppOpenManagerKt.getInstance().enableAppResume()
                }
                context.sendBroadcast(Intent(PrepareLoadingAdsDialogKt.ACTION_DISMISS_DIALOG))
                callback?.let {
                    // Consider if you should pass 'adError' to the callback here as well
                    // e.g., it.onAdFailedToShowFullScreenContent(adError)
                    it.onAdClosed()
                    it.onNextScreen()
                }
            }

            override fun onAdShowedFullScreenContent() {
                if (!hasLoadingWhenShow) {
                    context.sendBroadcast(Intent(PrepareLoadingAdsDialogKt.ACTION_DISMISS_DIALOG))
                } else {
                    context.sendBroadcast(Intent(PrepareLoadingAdsDialogKt.ACTION_CLEAR_TEXT_AD))
                }
                callback?.onAdShowedFullScreenContent()
            }

            override fun onAdClicked() {
                super.onAdClicked() // Good practice to call super
                callback?.onAdClicked()
            }
        }

        // Check context validity and lifecycle state
        if (context.isDestroyed || !ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            callback?.let {
                it.onAdClosed()
                it.onNextScreen()
            }
            return
        }

        var timeShowLoadingDlg = 0L
        if (isShowLoadingDialog) { // Assuming isShowLoadingDialog is a property
            PrepareLoadingAdsDialogKt.start(context)
            timeShowLoadingDlg = customTimeLoadingDialog // Assuming customTimeLoadingDialog is a property
        }

        if (AppOpenManagerKt.getInstance().isInitialized()) {
            AppOpenManagerKt.getInstance().disableAppResume()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            log("show inter: ${interstitialAd.adUnitId}") // Assuming log() and hasLog are accessible
            interstitialAd.show(context)
        }, timeShowLoadingDlg)
    }

    fun loadRewardAd(context: Context, id: String, adLoadCallback: RewardedAdLoadCallback) {
        log("Request RewardAd :$id")

        val request: AdRequest? = adRequest
        if (request == null) {
            adLoadCallback.onAdFailedToLoad(errAd)
            return
        }

        RewardedAd.load(context, id, request, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                super.onAdLoaded(rewardedAd)
                // Listener to get AD value
                rewardedAd.onPaidEventListener = OnPaidEventListener { adValue: AdValue? ->
                    trackRevenueSolar.trackRevenueRewardAd(
                        adValue,
                        rewardedAd
                    )
                }
            }
        })
    }

    fun showRewardAd(activity: Activity, rewardedAd: RewardedAd?, callback: AdCallback?) {
        if (rewardedAd == null) {
            log("Cannot show RewardedAd: ad is null")
            callback?.onAdFailedToShowFullScreenContent(errAd) // Using onAdFailedToShow as it implies ad is not usable
            return
        }
        log("showRewardAds")
        rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                log("RewardedAd dismissed.")
                callback?.onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                log("RewardedAd failed to show: ${'$'}{adError.message}")
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                log("RewardedAd showed full screen.")
                callback?.onAdShowedFullScreenContent()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                log("RewardedAd impression.")
                callback?.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                log("RewardedAd clicked.")
                callback?.onAdClicked()
            }
        }
        if (!activity.isFinishing) {
            rewardedAd.show(activity) { rewardItem ->
                log("User earned reward: ${'$'}{rewardItem.amount} ${'$'}{rewardItem.type}")
                callback?.onUserEarnedReward(rewardItem)
            }
        } else {
            log("Reward ad not shown: Activity is finishing.")
        }
    }

    fun loadBanner(
        activity: Activity,
        id: String,
        adContainer: FrameLayout,
        callback: AdCallback?,
    ) {
        log("loadBanner: $id")
        val adView = AdView(activity)
        adView.adUnitId = id
        val adSize = getAdSize(activity)
        adView.setAdSize(adSize)

        val request = adRequest
        if (request == null) {
            adContainer.visibility = View.GONE // Hide container if ads disabled
            callback?.onAdFailedToLoad(errAd)
            return
        }
        try {
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    log("Banner ad loaded: $id")
                    callback?.onAdLoaded()
                    adContainer.removeAllViews()
                    adContainer.addView(adView)
                    adContainer.visibility = View.VISIBLE
                    adView.setOnPaidEventListener { adValue ->
                        trackRevenueSolar.trackRevenueBannerSolar(adValue, adView, id)
                    }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    log("Banner ad impression: $id")
                    callback?.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    log("Banner ad clicked: $id")
                    callback?.onAdClicked()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Banner ad failed to load: ${'$'}{loadAdError.message} for ID: $id")
                    adContainer.visibility = View.GONE // Hide on fail
                    callback?.onAdFailedToLoad(loadAdError)
                }
            }
            adView.loadAd(request)
        } catch (e: Exception) {
            e.printStackTrace()
            log("Exception in loadBanner: ${'$'}{e.message}")
            adContainer.visibility = View.GONE
            callback?.onAdFailedToLoad(LoadAdError(0, e.message ?: "Banner load exception", "Banner", null, null))
        }
    }

    private fun getAdSize(activity: Activity): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density

        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun preloadNative(context: Context, id: String, callback: AdCallback) {
        log("preloadNative: $id")
        loadUnifiedNativeAd(context, id, object : AdCallback() {
            override fun onNativeAds(nativeAd: NativeAd) {
                log("Native ad preloaded: $id")
                callback.onNativeAds(nativeAd)
                nativeAd.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id)
                }
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                log("Native ad preload failed: ${'$'}{error.message} for ID: $id")
                callback.onAdFailedToLoad(error)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                log("Native ad (preloaded) impression: $id")
                callback.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                log("Native ad (preloaded) clicked: $id")
                callback.onAdClicked()
            }
        })
    }
    
    // New method as requested in a previous interaction, assuming it was for Kotlin conversion
    fun loadAndGetNativeAds(activity: Activity, id: String, callback: AdCallback) {
        log("loadAndGetNativeAds: $id for activity: ${'$'}{activity.localClassName}")
        val request = adRequest
        if (request == null) {
            log("AdRequest is null, cannot load native ad for $id.")
            callback.onAdFailedToLoad(errAd)
            return
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder()
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_ANY) 
            .setVideoOptions(videoOptions)
            .build()

        val adLoader = AdLoader.Builder(activity, id)
            .forNativeAd { nativeAd: NativeAd ->
                log("Native ad loaded successfully: ${'$'}{nativeAd.headline} for $id")
                callback.onNativeAds(nativeAd)
                nativeAd.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Native ad failed to load: ${'$'}{loadAdError.message}, Code: ${'$'}{loadAdError.code} for $id")
                    callback.onAdFailedToLoad(loadAdError)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    log("Native ad impression recorded for $id.")
                    callback.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    log("Native ad clicked for $id.")
                    callback.onAdClicked()
                }
            })
            .withNativeAdOptions(adOptions)
            .build()

        log("Loading native ad for $id...")
        adLoader.loadAd(request)
    }

    fun preloadFullScreenNative(context: Context, id: String, callback: AdCallback) {
        log("preloadFullScreenNative: $id")
        loadFullScreenUnifiedNativeAd(context, id, object : AdCallback() {
            override fun onNativeAds(nativeAd: NativeAd) {
                log("Fullscreen native ad preloaded: $id")
                callback.onNativeAds(nativeAd)
                nativeAd.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id)
                }
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                log("Fullscreen native ad preload failed: ${'$'}{error.message} for ID: $id")
                callback.onAdFailedToLoad(error)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                log("Fullscreen native ad (preloaded) clicked: $id")
                callback.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                log("Fullscreen native ad (preloaded) impression: $id")
                callback.onAdImpression()
            }
        })
    }

    // New method as requested in a previous interaction, assuming it was for Kotlin conversion
    fun populateNativeAdView(activity: Activity, nativeAd: NativeAd, frameLayout: FrameLayout, layoutId: Int) {
        log("Populating NativeAdView for ad: ${'$'}{nativeAd.headline} with layout ID: $layoutId")
        frameLayout.removeAllViews() // Clear previous views
        val inflater = LayoutInflater.from(activity)
        val adView = inflater.inflate(layoutId, frameLayout, false) as NativeAdView

        onBindAdView(nativeAd, adView) // Reuse onBindAdView logic

        frameLayout.addView(adView)
        frameLayout.visibility = View.VISIBLE
        log("NativeAdView populated and added to FrameLayout for ad: ${'$'}{nativeAd.headline}")
    }


    
    fun showNative(
        context: Context,
        nativeAd: NativeAd?,
        placeHolder: FrameLayout,
        type: NativeAdType, // Ensure NativeAdType enum is defined
        isHideInvisibility: Boolean = false,
    ) {
        log("showNative: ${'$'}{nativeAd != null}, type: $type, isHideInvisibility: $isHideInvisibility")
        if (nativeAd == null) {
            if (isHideInvisibility) {
                placeHolder.visibility = View.INVISIBLE
            } else {
                placeHolder.visibility = View.GONE
            }
            return
        }

        placeHolder.visibility = View.VISIBLE
        nativeAd.responseInfo?.mediationAdapterClassName?.let {
            log("Native ad source: $it")
        }
        
        val customNativeLayoutId = getLayoutNative(type)
        @SuppressLint("InflateParams")
        val nativeAdView = LayoutInflater.from(context).inflate(customNativeLayoutId, null) as NativeAdView
        onBindAdView(nativeAd, nativeAdView)
        placeHolder.removeAllViews()
        placeHolder.addView(nativeAdView)
    }

    private fun getLayoutNative(type: NativeAdType): Int {
        return when (type) {
            NativeAdType.BIG -> R.layout.custom_native_meta_big
            NativeAdType.SMALL -> R.layout.custom_native_meta_small
            NativeAdType.MEDIUM -> R.layout.custom_native_meta_regular
            NativeAdType.FULLSCREEN -> R.layout.custom_full_screen_native_ads
            // Consider having a default or throwing an exception for unexpected types
            else -> R.layout.custom_native_ads_2 // Defaulting, ensure this is intended
        }
    }

    
    fun loadNative(
        context: Context,
        id: String,
        placeHolder: FrameLayout,
        nativeAdType: NativeAdType,
        callback: AdCallback? = null,
    ) {
        log("loadNative request: $id, type: $nativeAdType")
        loadUnifiedNativeAd(context, id, object : AdCallback() {
            override fun onNativeAds(nativeAd: NativeAd) {
                log("Native ad loaded for loadNative: $id")
                showNative(context, nativeAd, placeHolder, nativeAdType)
                nativeAd.setOnPaidEventListener { adValue ->
                    trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id)
                }
                callback?.onNativeAds(nativeAd)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                log("Native ad failed for loadNative: ${'$'}{error.message} for ID: $id")
                placeHolder.removeAllViews()
                placeHolder.visibility = View.GONE
                callback?.onAdFailedToLoad(error)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                log("Native ad (loadNative) impression: $id")
                callback?.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                log("Native ad (loadNative) clicked: $id")
                callback?.onAdClicked()
            }
        })
    }

    fun loadFullScreenUnifiedNativeAd(context: Context, id: String, callback: AdCallback?) {
        log("loadFullScreenUnifiedNativeAd: $id")
        val request = adRequest
        if (request == null) {
            callback?.onAdFailedToLoad(errAd)
            return
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder()
            .setMediaAspectRatio(MediaAspectRatio.ANY) // Or specific like PORTRAIT/LANDSCAPE
            .setVideoOptions(videoOptions)
            .build()

        val adLoader = AdLoader.Builder(context, id)
            .forNativeAd { nativeAd: NativeAd ->
                log("Fullscreen Unified Native ad loaded: $id")
                callback?.onNativeAds(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Fullscreen Unified Native ad failed: ${'$'}{loadAdError.message} for ID: $id")
                    callback?.onAdFailedToLoad(loadAdError)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    log("Fullscreen Unified Native ad impression: $id")
                    callback?.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    log("Fullscreen Unified Native ad clicked: $id")
                    callback?.onAdClicked()
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(request)
    }

    private fun registerDialogBehaviorReceiver(context: Context, placeHolder: FrameLayout?) {
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.action?.let {
                    if (placeHolder != null) {
                        when (it) {
                            ACTION_CLOSE_NATIVE_ADS -> placeHolder.visibility = View.GONE
                            ACTION_OPEN_NATIVE_ADS -> placeHolder.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        val intentFilter = initDialogBehaviorIntentFilter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }
        // TODO: Consider unregistering this receiver when the context (Activity/Fragment) is destroyed.
    }

    private fun initDialogBehaviorIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(ACTION_CLOSE_NATIVE_ADS)
            addAction(ACTION_OPEN_NATIVE_ADS)
        }
    }

    private fun loadUnifiedNativeAd(context: Context, id: String, callback: AdCallback?) {
        log("loadUnifiedNativeAd: $id")
        val request = adRequest
        if (request == null) {
            callback?.onAdFailedToLoad(errAd)
            return
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        // Defaulting to PORTRAIT as in original Java, consider making this configurable or ANY
        val adOptions = NativeAdOptions.Builder()
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT) 
            .setVideoOptions(videoOptions)
            .build()

        val adLoader = AdLoader.Builder(context, id)
            .forNativeAd { nativeAd: NativeAd ->
                log("Unified Native ad loaded: $id")
                callback?.onNativeAds(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("Unified Native ad failed: ${'$'}{loadAdError.message} for ID: $id")
                    callback?.onAdFailedToLoad(loadAdError)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    log("Unified Native ad impression: $id")
                    callback?.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    log("Unified Native ad clicked: $id")
                    callback?.onAdClicked()
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(request)
    }

    private fun onBindAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        // Optional views from original Java code (commented out as they were in Java)
//        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
//        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as? TextView)?.text = nativeAd.headline
        nativeAd.mediaContent?.let { 
            adView.mediaView?.mediaContent = it
//            adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        }

        (adView.bodyView as? TextView)?.text = nativeAd.body
        adView.bodyView?.visibility = if (nativeAd.body.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        (adView.callToActionView as? TextView)?.text = nativeAd.callToAction
        adView.callToActionView?.visibility = if (nativeAd.callToAction.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        (adView.priceView as? TextView)?.text = nativeAd.price
        adView.priceView?.visibility = if (nativeAd.price.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        (adView.storeView as? TextView)?.text = nativeAd.store
        adView.storeView?.visibility = if (nativeAd.store.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as? RatingBar)?.rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }
        
        (adView.advertiserView as? TextView)?.text = nativeAd.advertiser
        adView.advertiserView?.visibility = if (nativeAd.advertiser.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        adView.setNativeAd(nativeAd)

        val vc = nativeAd.mediaContent?.videoController
        if (vc != null && vc.hasVideoContent()) {
            log("Video status: Ad contains a video asset.")
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    log("Video status: Video playback ended.")
                    super.onVideoEnd()
                }
            }
        } else {
            log("Video status: Ad does not contain a video asset.")
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return md5(androidId).uppercase(Locale.getDefault())
    }

    private fun md5(s: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuilder()
            for (b in messageDigest) {
                val h = StringBuilder(Integer.toHexString(0xFF and b.toInt()))
                while (h.length < 2) h.insert(0, "0")
                hexString.append(h)
            }
            return hexString.toString()
        } catch (ignored: NoSuchAlgorithmException) {
        }
        return ""
    }

    companion object {
        const val ACTION_CLOSE_NATIVE_ADS = "ACTION_CLOSE_NATIVE_ADS"
        const val ACTION_OPEN_NATIVE_ADS = "ACTION_OPEN_NATIVE_ADS"

        @SuppressLint("StaticFieldLeak") // If context is stored, this might be an issue. Review usage.
        @Volatile
        private var instance: AdmobManagerKt? = null

        @JvmStatic
        fun getInstance(): AdmobManagerKt =
            instance ?: synchronized(this) {
                instance ?: AdmobManagerKt().also { instance = it }
            }

    }

    enum class NativeAdType {
        BIG, SMALL, MEDIUM, FULLSCREEN // Add any other types used
    }
    // --- Begin ported methods from AdmobManagerKt.java 976-1343 ---

    fun getAdCollapsibleBannerRequest(): AdRequest? {
        if (!hasAds || PurchaseManagerKt.getInstance().isPurchased()) {
            return null
        }
        val extras = android.os.Bundle()
        extras.putString("collapsible", "bottom")
        return AdRequest.Builder()
            .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java, extras)
            .build()
    }

    fun loadAlternateCollapsibleBanner(
        activity: Activity,
        idsInput: List<String>,
        adContainer: FrameLayout?,
        adListenerCallback: AdListener? = null,
    ) {
        val ids = idsInput.toMutableList()
        if (ids.isEmpty()) {
            log("loadAlternateCollapsibleBanner: All IDs failed or list empty.")
            adContainer?.removeAllViews()
            adContainer?.visibility = View.GONE
            adListenerCallback?.onAdFailedToLoad(
                LoadAdError(
                    0,
                    "All Ad IDs failed or list was empty.",
                    "com.google.android.gms.ads",
                    null,
                    null
                )
            )
            return
        }
        val currentId = ids[0]
        log("loadAlternateCollapsibleBanner: Attempting to load ID: $currentId")
        val request = getAdCollapsibleBannerRequest()
        if (request == null) {
            log("loadAlternateCollapsibleBanner: AdRequest is null for ID: $currentId")
            adContainer?.removeAllViews()
            adContainer?.visibility = View.GONE
            adListenerCallback?.onAdFailedToLoad(errAd)
            return
        }
        try {
            val adView = AdView(activity)
            adView.adUnitId = currentId
            val adSize = getCollapsibleBannerAdSize(activity)
            adView.setAdSize(adSize)
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    log("loadAlternateCollapsibleBanner: Ad loaded successfully for ID: $currentId")
                    adContainer?.removeAllViews()
                    adContainer?.visibility = View.VISIBLE
                    adContainer?.addView(adView)
                    adView.setOnPaidEventListener { adValue ->
                        trackRevenueSolar?.trackRevenueBannerSolar(adValue, adView, currentId)
                    }
                    adListenerCallback?.onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    log("loadAlternateCollapsibleBanner: Failed to load ID: $currentId. Error: ${loadAdError.message}")
                    ids.removeAt(0)
                    loadAlternateCollapsibleBanner(activity, ids, adContainer, adListenerCallback)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    adListenerCallback?.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    adListenerCallback?.onAdImpression()
                }
            }
            adView.loadAd(request)
        } catch (e: Exception) {
            log("loadAlternateCollapsibleBanner: Exception during ad setup for ID: $currentId. Error: ${e.message}")
            e.printStackTrace()
            ids.removeAt(0)
            loadAlternateCollapsibleBanner(activity, ids, adContainer, adListenerCallback)
        }
    }

    fun loadCollapsibleBanner(
        activity: Activity,
        id: String,
        adContainer: FrameLayout,
    ) {
        log("Request Banner :$id")
        val request = getAdCollapsibleBannerRequest()
        if (request == null) {
            adContainer.removeAllViews()
            adContainer.visibility = View.GONE
            return
        }
        try {
            val adView = AdView(activity)
            adView.adUnitId = id
            val adSize = getCollapsibleBannerAdSize(activity)
            adView.setAdSize(adSize)
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adView.loadAd(request)
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    adContainer.removeAllViews()
                    adContainer.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    adContainer.removeAllViews()
                    adContainer.visibility = View.VISIBLE
                    adContainer.addView(adView)
                    adView.setOnPaidEventListener { adValue ->
                        trackRevenueSolar.trackRevenueBannerSolar(
                            adValue,
                            adView,
                            id
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCollapsibleBannerAdSize(activity: Activity): AdSize {
        val widthPixels: Float
        val density: Float
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android R (API 30) and above, use activity.display and resources.displayMetrics
            val outMetrics = activity.resources.displayMetrics
            widthPixels = outMetrics.widthPixels.toFloat()
            density = outMetrics.density
        } else {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            widthPixels = outMetrics.widthPixels.toFloat()
            density = outMetrics.density
        }
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun loadAlternateInter(context: Context, idsInput: List<String>, callback: AdCallback) {
        val ids = idsInput.toMutableList()
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateInter: empty")
            return
        }
        loadInterAds(context, ids[0], object : AdCallback() {
            override fun onAdFailedToLoad(i: LoadAdError) {
                super.onAdFailedToLoad(i)
                Log.w("AdmobLogger", "loadAlternateInter: fail-${ids[0]}")
                ids.removeAt(0)
                if (ids.isEmpty()) {
                    callback.onAdFailedToLoad(i)
                } else {
                    loadAlternateInter(context, ids, callback)
                }
            }

            override fun onResultInterstitialAd(interstitialAd: InterstitialAd) {
                super.onResultInterstitialAd(interstitialAd)
                callback.onResultInterstitialAd(interstitialAd)
                Log.i("AdmobLogger", "loadAlternateInter: success-${ids[0]}")
            }
        })
    }

    fun preloadAlternateNative(context: Context, idsInput: List<String>, callback: AdCallback) {
        val ids = idsInput.toMutableList()
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateNative: empty")
            callback.onNativeAds(null)
            return
        }
        preloadNative(context, ids[0], object : AdCallback() {
            override fun onAdFailedToLoad(i: LoadAdError) {
                super.onAdFailedToLoad(i)
                Log.w("AdmobLogger", "loadAlternateNative: fail-${ids[0]}")
                ids.removeAt(0)
                preloadAlternateNative(context, ids, callback)
            }

            override fun onNativeAds(nativeAd: NativeAd) {
                super.onNativeAds(nativeAd)
                callback.onNativeAds(nativeAd)
                Log.i("AdmobLogger", "loadAlternateNative: success-${ids[0]}")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                callback.onAdImpression()
            }
        })
    }

    fun preloadFullScreenAlternateNative(
        context: Context,
        idsInput: List<String>,
        callback: AdCallback,
    ) {
        val ids = idsInput.toMutableList()
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternatefsNative: empty")
            callback.onNativeAds(null)
            return
        }
        preloadFullScreenNative(context, ids[0], object : AdCallback() {
            override fun onAdFailedToLoad(i: LoadAdError) {
                super.onAdFailedToLoad(i)
                Log.w("AdmobLogger", "loadAlternatefsNative: fail-${ids[0]}")
                ids.removeAt(0)
                preloadAlternateNative(context, ids, callback)
            }

            override fun onNativeAds(nativeAd: NativeAd) {
                super.onNativeAds(nativeAd)
                callback.onNativeAds(nativeAd)
                Log.i("AdmobLogger", "loadAlternatefsNative: success-${ids[0]}")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                callback.onAdImpression()
            }
        })
    }

    fun loadAlternateBanner(
        activity: Activity,
        ids: MutableList<String>,
        adContainer: FrameLayout,
        callback: AdCallback? = null,
    ) {
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateBanner: empty")
            return
        }
        loadBanner(activity, ids[0], adContainer, object : AdCallback() {
            override fun onAdFailedToLoad(i: LoadAdError) {
                super.onAdFailedToLoad(i)
                Log.w("AdmobLogger", "loadAlternateBanner: fail-${ids[0]}")
                Log.w("AdmobLogger", "error: ${i.message}")
                ids.removeAt(0)
                if (ids.isNotEmpty()) {
                    adContainer.visibility = View.VISIBLE
                    loadAlternateBanner(activity, ids, adContainer, callback)
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("AdmobLogger", "loadAlternateBanner: success-${ids[0]}")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback?.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                callback?.onAdImpression()
            }
        })
    }
    // --- End ported methods ---
}
