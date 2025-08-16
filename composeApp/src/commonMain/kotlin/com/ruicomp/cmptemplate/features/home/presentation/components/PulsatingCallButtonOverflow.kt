package com.ruicomp.cmptemplate.features.home.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun RowScope.PulsatingCallButtonOverflow(onCall: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "call_button_transition")

    // Pulse animation for the button itself
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ), label = "button_scale"
    )

    // Sonar animation values
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f, // Made ripple larger to emphasize the effect
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ripple_scale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ripple_alpha"
    )

    // The Layout composable solves the size-reporting issue.
    // The graphicsLayer modifier solves the clipping issue.
    Layout(
        modifier = Modifier.align(Alignment.CenterVertically).graphicsLayer { clip = false }, // <-- THE KEY MODIFIER
        content = {
            // Ripple Box (drawn first, so it's in the background)
            Box(
                Modifier
                    .size(56.dp)
                    .scale(rippleScale)
                    .graphicsLayer { alpha = rippleAlpha }
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            )

            // The actual button on top
            FilledIconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(56.dp)
                    .scale(buttonScale)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    modifier = Modifier.fillMaxSize(0.6f),
                    contentDescription = "Call"
                )
            }
        }
    ) { measurables, constraints ->
        // Measure the children (ripple and button) with loose constraints
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val ripplePlaceable = measurables[0].measure(looseConstraints)
        val buttonPlaceable = measurables[1].measure(looseConstraints)
        
        // Report the layout's size as being only the button's size
        layout(buttonPlaceable.width, buttonPlaceable.height) {
            // Place the children. Allow the ripple to be placed outside the layout bounds.
            val rippleX = (buttonPlaceable.width - ripplePlaceable.width) / 2
            val rippleY = (buttonPlaceable.height - ripplePlaceable.height) / 2
            ripplePlaceable.placeRelative(rippleX, rippleY)

            // Place the button at (0,0) which defines the component's origin
            buttonPlaceable.placeRelative(0, 0)
        }
    }
}


// --- Example Usage ---


@Composable
fun CallButtonOverflowPreview() {
    // Use a Box to demonstrate the overflow effect clearly
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .width(300.dp) // Fixed width for demonstration
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Incoming Call", style = MaterialTheme.typography.titleMedium)
                    Text("John Doe", style = MaterialTheme.typography.bodyMedium)
                }

                // The button's animation now draws outside the Card's bounds
                PulsatingCallButtonOverflow(onCall = { })
            }
        }
    }
}