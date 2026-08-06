package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musically.studio.billing.GenerationBlockReason
import com.musically.studio.billing.SubscriptionTierLimits
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.theme.*

/**
 * Bottom sheet shown when a user attempts to generate music, podcasts, or start a live
 * session after reaching their monthly tier limit.
 *
 * Provides clear information about the limit and a direct CTA to upgrade to a higher tier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageLimitBottomSheet(
    reasonName: String,
    viewModel: MainViewModel,
    onNavigateToPremium: () -> Unit,
    onDismiss: () -> Unit
) {
    val reason = try {
        GenerationBlockReason.valueOf(reasonName)
    } catch (_: Exception) {
        GenerationBlockReason.SONGS_LIMIT_REACHED
    }

    val currentProductId by viewModel.currentProductId.collectAsState()
    val tierLimits = viewModel.tierLimits
    val tierName = SubscriptionTierLimits.displayNameFor(currentProductId)

    val (title, description, limitText) = when (reason) {
        GenerationBlockReason.SONGS_LIMIT_REACHED -> Triple(
            "Song Limit Reached",
            "You've used all your AI song generations for this billing period on the $tierName plan.",
            "Limit: ${tierLimits.songsPerMonth} songs / month"
        )
        GenerationBlockReason.PODCASTS_LIMIT_REACHED -> Triple(
            "Podcast Limit Reached",
            "You've used all your AI podcast episode generations for this billing period on the $tierName plan.",
            "Limit: ${tierLimits.podcastEpsPerMonth} episodes / month"
        )
        GenerationBlockReason.REALTIME_LIMIT_REACHED -> Triple(
            "Real-time Session Limit Reached",
            "You've used all your live real-time session minutes for this billing period on the $tierName plan.",
            "Limit: ${tierLimits.realtimeMinutesPerMonth} minutes / month"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaveSurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaveBrand,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaveOnSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaveOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaveBackground
            ) {
                Text(
                    text = limitText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaveBrand,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onDismiss()
                    onNavigateToPremium()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaveBrand,
                    contentColor = MaveBackground
                )
            ) {
                Text(
                    text = "Upgrade Plan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Not Now",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaveOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
