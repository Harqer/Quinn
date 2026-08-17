package com.musically.studio.ui.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Applies a scale-down "bounce" effect when pressed, and performs an action when clicked.
 * Includes optional background haptics for tactile feedback.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier = composed {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by actualInteractionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "BounceClickScale"
    )

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            // Offload haptic to a background coroutine to avoid degrading UI thread performance
            coroutineScope.launch(Dispatchers.Default) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Light tactile feedback
            }
        }
    }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = actualInteractionSource,
            indication = null,
            onClick = {
                // Background thread haptic for click
                coroutineScope.launch(Dispatchers.Default) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            }
        )
}

/**
 * Dedicated modifier for wearables/small targets needing more aggressive haptics and scaling.
 */
fun Modifier.hapticClick(
    scaleDown: Float = 0.90f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "HapticClickScale"
    )

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            coroutineScope.launch(Dispatchers.Default) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                onClick()
            }
        )
}

/**
 * Adds a shimmering sweep gradient over the element for skeleton loading states.
 */
fun Modifier.shimmerEffect(isLoading: Boolean = true): Modifier = composed {
    if (!isLoading) return@composed this

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslation"
    )

    this.drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset.Zero,
                end = Offset(x = translateAnim, y = translateAnim)
            )
        )
    }
}
