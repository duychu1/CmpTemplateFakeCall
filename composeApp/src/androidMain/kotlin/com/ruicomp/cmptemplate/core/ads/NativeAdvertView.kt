package com.ruicomp.cmptemplate.core.ads

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManagerKt
import com.google.android.gms.ads.nativead.NativeAd

// Assume NativeAdSize is an enum defined in your project, for example:
// enum class NativeAdSize {
//     MEDIUM,
//     SMALL // Add other sizes as needed
// }
// Ensure it's imported if it's in a different file/package.

@Composable
fun NativeAdvertView(
    adUnitIds: List<String>,
    adSize: NativeAdSize, // Input parameter
    modifier: Modifier = Modifier,
) {
    // Restore adUnitId as an internal remembered value
    val adUnitId = remember { adUnitIds }
    var currentNativeAd by remember { mutableStateOf<NativeAd?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    println("NativeAdvertView recomposing. adUnitId hash: ${adUnitId.hashCode()}, content: $adUnitId")

    if (activity == null) {
        println("NativeAdvertView: Activity is null, cannot display ad.")
        return
    }

    // Convert NativeAdSize to AdmobManagerKt.NativeAdType
    val admobNativeAdType = when (adSize) {
        NativeAdSize.Small -> AdmobManagerKt.NativeAdType.SMALL
        NativeAdSize.Medium -> AdmobManagerKt.NativeAdType.MEDIUM
        NativeAdSize.Large -> AdmobManagerKt.NativeAdType.BIG
        NativeAdSize.Fullscreen -> AdmobManagerKt.NativeAdType.FULLSCREEN
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            println("NativeAdvertView: factory creating FrameLayout")
            FrameLayout(ctx)
        },
        update = { frameLayoutContainer ->
            println("NativeAdvertView: update - Calling loadAlternateBanner.")
            AdmobManagerKt.getInstance().preloadAlternateNative(
                context,
                adUnitId,
                object : AdCallback(){
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        currentNativeAd?.destroy()
                        currentNativeAd = nativeAd
                        AdmobManagerKt.getInstance().showNative(
                            context,
                            nativeAd,
                            frameLayoutContainer,
                            admobNativeAdType
                        )
                        Log.d("NativeAdvertView","Show NativeAd")
                    }
                    override fun onAdImpression() {
                        super.onAdImpression()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                    }
                }
            )
        }
    )
}
