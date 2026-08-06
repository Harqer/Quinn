package com.musically.studio.ui.utils

import androidx.compose.foundation.LocalIndication
import androidx.compose.ui.composed

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.musically.studio.ui.utils.debouncedClickable

private var globalLastClickTime = 0L

/**
 * Executes an action only if a certain amount of time has passed since the last debounced action.
 * Uses a global timer, which also prevents multi-touch issues where users press two buttons simultaneously.
 */
fun executeDebounced(debounceInterval: Long = 500L, action: () -> Unit) {
    val now = SystemClock.uptimeMillis()
    if (now - globalLastClickTime >= debounceInterval) {
        globalLastClickTime = now
        action()
    }
}


/**
 * A custom clickable modifier that debounces rapid clicks.
 */
fun Modifier.debouncedClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    debounceInterval: Long = 500L,
    onClick: () -> Unit
): Modifier = this.clickable(
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = {
        executeDebounced(debounceInterval, onClick)
    }
)

fun Modifier.debouncedClickable(
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource,
    indication: androidx.compose.foundation.Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    debounceInterval: Long = 500L,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = {
        executeDebounced(debounceInterval, onClick)
    }
)
