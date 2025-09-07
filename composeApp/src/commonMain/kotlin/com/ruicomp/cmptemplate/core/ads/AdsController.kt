package com.ruicomp.cmptemplate.core.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class AdsController {

    fun loadAndShowInterstitialAd(
        activity: Any,
        onAdLoaded: () -> Unit,
        onAdFailedToLoad: (error: String) -> Unit,
        onGotoNext: () -> Unit,
    )
}

@Composable
expect fun BannerAdComposable(
    adUnitIds: List<String>,
    modifier: Modifier
)

@Composable
expect fun NativeAdComposable(
    adUnitIds: List<String>,
    modifier: Modifier
)