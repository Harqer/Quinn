/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for MaveButton.kt
 */

package com.musically.studio.ui.components.atoms

import com.musically.studio.ui.utils.debouncedClickable
import com.musically.studio.ui.utils.bounceClick

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.*
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun MaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }

    Box(
        modifier = if (enabled) {
            modifier.bounceClick(
                onClick = onClick,
                interactionSource = interactionSource
            )
        } else {
            modifier
        }
            .styleable(styleState, MaveStyles.maveButtonStyle, style)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
