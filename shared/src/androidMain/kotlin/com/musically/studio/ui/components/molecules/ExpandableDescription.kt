/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for ExpandableDescription.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun ExpandableDescription(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 5,
    seeMoreText: String = "See more",
    seeLessText: String = "See less",
    textColor: Color = LocalContentColor.current,
    buttonColor: Color = MaterialTheme.colorScheme.primary
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        val toggleText = if (isExpanded) seeLessText else seeMoreText

        Text(
            text = toggleText,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = buttonColor,
            modifier = Modifier
                .heightIn(min = 20.dp)
                .fillMaxWidth()
                .padding(top = 15.dp)
                .debouncedClickable {
                    isExpanded = !isExpanded
                }
        )
    }
}
