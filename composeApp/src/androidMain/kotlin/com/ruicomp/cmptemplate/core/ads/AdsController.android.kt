package com.ruicomp.cmptemplate.core.ads

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManagerKt
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

actual class AdsController(private val context: Context) {

    actual fun loadAndShowInterstitialAd(
        activity: Any,
        onAdLoaded: () -> Unit,
        onAdFailedToLoad: (String) -> Unit,
        onGotoNext: () -> Unit
    ) {

        AdmobManagerKt.Companion.getInstance().loadAlternateInter(
            context,
            listOf("ca-app-pub-3940256099942544/1033173712"),
            object : AdCallback() {
                override fun onAdFailedToLoad(i: LoadAdError) {
                    super.onAdFailedToLoad(i)
                    onAdFailedToLoad(i.message)
                    onGotoNext()
                }

                override fun onResultInterstitialAd(interstitialAd: InterstitialAd) {
                    super.onResultInterstitialAd(interstitialAd)
                    onAdLoaded()

                    AdmobManagerKt.Companion.getInstance().showInterstitial(
                        context = activity as Activity,
                        interstitialAd = interstitialAd,
                        callback = object : AdCallback() {
                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
//                    logEvent("splash_ad_inter_view")

                            }

                            override fun onNextScreen() {
                                super.onNextScreen()
                                onGotoNext()

                            }

                            override fun onClickClose() {
                                super.onClickClose()
//                    logEvent("splash_ad_inter_close_click")
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
//                    logEvent("splash_ad_inter_click")
                            }
                        }
                    )

                }

            }
        )

    }
}

@Composable
actual fun BannerAdComposable(
    adUnitIds: List<String>,
    modifier: Modifier
) {
    BannerAdvertView(adUnitIds, modifier)
}

@Composable
actual fun NativeAdComposable(
    adUnitIds: List<String>,
    adSize: NativeAdSize,
    modifier: Modifier
) {
    NativeAdvertView(adUnitIds, adSize, modifier)
}