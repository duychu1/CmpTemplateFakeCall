// AnimatedErrorText.kt
package com.ruicomp.cmptemplate.features.schedule.presentation.components // Or your preferred package

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A composable that displays an error message with an animation similar to a toast.
 * It appears when a new [errorMessage] is provided and disappears after a specified [durationMillis].
 *
 * @param errorMessage The error message string to display. If null, the composable is not visible.
 * @param modifier Optional Modifier for this composable.
 * @param durationMillis The duration in milliseconds for which the error message is visible.
 * @param onDismiss Optional callback invoked when the error message has finished its hiding animation.
 *                  This can be used to signal that the error has been "consumed".
 * @param enterTransition The enter animation for the error message.
 * @param exitTransition The exit animation for the error message.
 */
@Composable
fun AnimatedErrorText(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    durationMillis: Long = 1500L, // Default duration
    onDismiss: (() -> Unit)? = null,
    enterTransition: EnterTransition = fadeIn() + expandVertically(expandFrom = Alignment.Top),
    exitTransition: ExitTransition = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
) {
    var showErrorAnimation by remember { mutableStateOf(false) }

    // This effect triggers when 'errorMessage' changes.
    // We use 'Unit' as the key for the inner LaunchedEffect to ensure the delay timer
    // correctly cancels and restarts if 'errorMessage' changes while one is active.
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showErrorAnimation = true // Make the error visible
            // Launch a new coroutine for the delay to ensure it's tied to the current errorMessage
            launch {
                delay(durationMillis)
                showErrorAnimation = false // Start hiding animation
                // Wait for the exit animation to complete before calling onDismiss
                // This is a common pattern, but AnimatedVisibility doesn't directly offer a completion callback for exit.
                // A fixed delay slightly longer than typical exit animations can be a pragmatic approach.
                // For more precise control, you might need more complex animation state management.
                delay(300L) // Estimate for exit animation, adjust if needed
                onDismiss?.invoke()
            }
        } else {
            // If errorMessage becomes null externally (e.g., cleared by ViewModel),
            // ensure showErrorAnimation is also false.
            showErrorAnimation = false
        }
    }

    AnimatedVisibility(
        visible = showErrorAnimation,
        modifier = modifier,
        enter = enterTransition,
        exit = exitTransition
    ) {
        // The content of AnimatedVisibility should ideally depend on errorMessage directly
        // to ensure it recomposes when the error message text changes, even if showErrorAnimation
        // state is managed separately.
        Text(
            text = errorMessage ?: "", // Provide a non-null string, though it won't be visible if errorMessage is null
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                    MaterialTheme.shapes.small
                )
                .padding(8.dp)
        )
    }
}
