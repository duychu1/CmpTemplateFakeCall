package com.ruicomp.cmptemplate.core.ads

import android.app.Activity
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.common.control.manager.AdmobManagerKt

@Composable
fun BannerAdvertView(
    adUnitIds: List<String>,
    modifier: Modifier = Modifier,
) {
    // Restore adUnitId as an internal remembered value
    val adUnitId = remember { adUnitIds }

    val context = LocalContext.current
    val activity = context as? Activity

    println("BannerAdvertView recomposing. adUnitId hash: ${adUnitId.hashCode()}, content: $adUnitId")

    if (activity == null) {
        println("BannerAdvertView: Activity is null, cannot display ad.")
        return
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            println("BannerAdvertView: factory creating FrameLayout")
            FrameLayout(ctx)
        },
        update = { frameLayoutContainer ->
            println("BannerAdvertView: update - Calling loadAlternateBanner.")
            AdmobManagerKt.getInstance().loadAlternateBanner(activity, adUnitId.toMutableList(), frameLayoutContainer, null)
        }
    )
}
