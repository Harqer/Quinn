package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.screens.FeatureHighlight
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveOnSurface
import com.musically.studio.ui.theme.MaveOnSurfaceVariant
import com.musically.studio.ui.theme.MaveSurfaceContainer

@Composable
fun FeatureHighlightRow(highlight: FeatureHighlight) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaveSurfaceContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaveBrand.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = highlight.icon,
                contentDescription = null,
                tint = MaveBrand,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = highlight.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = MaveOnSurface
            )
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaveOnSurfaceVariant
            )
        }
    }
}
