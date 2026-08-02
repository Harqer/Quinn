package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun UserMessageBubble(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .styleable(styleState, MaveStyles.userMessageBubbleStyle, style)
        ) {
            Text(text, color = com.musically.studio.ui.theme.MaveGray200)
        }
    }
}
