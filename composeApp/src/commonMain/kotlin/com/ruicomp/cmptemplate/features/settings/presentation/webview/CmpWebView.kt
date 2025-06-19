package com.ruicomp.cmptemplate.features.settings.presentation.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator // Or LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

/**
 * A simple Composable to display web content from a given URL
 * using the compose-webview-multiplatform library.
 */
@Composable
fun CmpWebView(
    url: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val webViewState = rememberWebViewState(url = url)
    // The library might also provide a navigator if you need more control,
    // e.g., val navigator = rememberWebViewNavigator()

    Box(modifier = modifier) {
        WebView(
            state = webViewState,
            modifier = Modifier.fillMaxSize(),
        )

        // Observe loading state from the library's WebViewState
        val loadingState = webViewState.loadingState
        if (loadingState is LoadingState.Loading) {
            CircularProgressIndicator( // Or LinearProgressIndicator
                // progress = { loadingState.progress }, // If library provides progress in LoadingState
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Handle errors if the library's state provides error information
        webViewState.errorsForCurrentRequest.firstOrNull()?.let { error ->
            // Note: Error handling specifics depend on how the library exposes errors.
            // This is a guess based on typical patterns. Check library's API.
            Text(
                text = "Error: ${error.description ?: "Unknown error"}",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}