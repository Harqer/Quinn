/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for PlanFeatureRow.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.theme.*

@Composable
fun PlanFeatureRow(feature: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaveBrand,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = feature,
            style = MaterialTheme.typography.bodyMedium,
            color = MaveOnSurfaceVariant
        )
    }
}