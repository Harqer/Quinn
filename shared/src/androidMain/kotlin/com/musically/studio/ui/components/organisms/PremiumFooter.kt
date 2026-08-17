/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for PremiumFooter.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveOnSurfaceVariant
import com.musically.studio.ui.theme.MaveSpacing
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun PremiumFooter(
    isPremium: Boolean,
    onManageSubscription: () -> Unit,
    onRestorePurchases: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaveSpacing().large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isPremium) {
            TextButton(onClick = onManageSubscription) {
                Text(
                    text = "Manage Subscription",
                    color = MaveBrand,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Restore Purchase",
            style = MaterialTheme.typography.bodySmall,
            color = MaveOnSurfaceVariant,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.debouncedClickable { onRestorePurchases() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Subscriptions auto-renew unless cancelled at least 24 hours before the period ends. " +
                    "Manage or cancel anytime via Google Play.",
            style = MaterialTheme.typography.labelSmall,
            color = MaveOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
