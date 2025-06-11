package com.ruicomp.cmptemplate.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingContent(
    isLoading: Boolean,
    errorMessage: String? = null,
    isEmpty: Boolean = false,
    emptyContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
        isEmpty -> {
            emptyContent()
        }
        else -> {
            content()
        }
    }
}

@Preview
@Composable
fun LoadingContentPreview_Loading() {
    LoadingContent(
        isLoading = true,
        content = {}
    )
}

@Preview
@Composable
fun LoadingContentPreview_Error() {
    LoadingContent(
        isLoading = false,
        errorMessage = "An error occurred",
        content = {}
    )
}

@Preview
@Composable
fun LoadingContentPreview_Empty() {
    LoadingContent(
        isLoading = false,
        isEmpty = true,
        emptyContent = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items available")
            }
        },
        content = {}
    )
} 