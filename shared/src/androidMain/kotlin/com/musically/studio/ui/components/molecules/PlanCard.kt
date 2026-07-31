package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import com.musically.studio.ui.theme.*

/**
 * A presentation-only card representing a single subscription tier.
 *
 * This component owns no state and makes no network calls — it is purely driven
 * by its parameters. The container screen ([PremiumPlansScreen]) is responsible
 * for state, loading, and user interaction callbacks.
 *
 * @param planName       Human-readable tier name, e.g. "Pro Studio"
 * @param price          Formatted price string, e.g. "$50/mo"
 * @param billingPeriod  Supporting price detail, e.g. "billed monthly"
 * @param features       Ordered list of feature strings shown as check-mark rows
 * @param badge          Optional pill label, e.g. "MOST POPULAR" or "BEST VALUE"
 * @param isHighlighted  When true renders the green-border hero variant
 * @param isCurrentPlan  Replaces the CTA with a "Your Plan" indicator
 * @param isLoading      Replaces the CTA button with a progress indicator
 * @param onSelectClick  Invoked when the user taps the CTA button
 * @param modifier       Standard Compose modifier chain
 * @param style          Styles API override slot — defaults to [MaveStyles.premiumPlanCardStyle]
 */
@Composable
fun PlanCard(
    planName: String,
    price: String,
    billingPeriod: String,
    features: List<String>,
    badge: String? = null,
    isHighlighted: Boolean = false,
    isCurrentPlan: Boolean = false,
    isLoading: Boolean = false,
    onSelectClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {}

    val cardColor = if (isHighlighted) MaveBackground else MaveSurfaceContainer
    val borderColor = if (isHighlighted) MaveBrand else Color.Transparent
    val borderWidth = if (isHighlighted) 2.dp else 0.dp

    val cardA11yDescription = buildString {
        append("$planName plan, $price. ")
        if (badge != null) append("$badge. ")
        if (isCurrentPlan) append("This is your current plan.") else append("Tap to subscribe.")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = cardA11yDescription }
            .styleable(styleState, MaveTheme.styles.premiumPlanCardStyle, style),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(MaveSpacing().small)
        ) {
            // Badge pill
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaveBackground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaveBrand)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Plan name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isHighlighted) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaveBrand,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = planName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted) MaveBrand else MaveOnSurface
                )
            }

            // Price
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaveOnSurface
                )
                Text(
                    text = billingPeriod,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaveOnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaveOnSurfaceVariant.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(4.dp))

            // Feature rows
            features.forEach { feature ->
                Row(
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

            Spacer(modifier = Modifier.height(8.dp))

            // CTA
            when {
                isCurrentPlan -> {
                    // Non-interactive indicator — this is already your plan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaveBrand.copy(alpha = 0.12f))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaveBrand,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your Plan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaveBrand
                        )
                    }
                }

                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = MaveBrand,
                            strokeWidth = 2.5.dp
                        )
                    }
                }

                else -> {
                    Button(
                        onClick = onSelectClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHighlighted) MaveBrand else MaveSurfaceVariant2,
                            contentColor = if (isHighlighted) MaveBackground else MaveOnSurface
                        )
                    ) {
                        Text(
                            text = "Get ${planName.split(" ").first()}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun PlanCardHighlightedPreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            PlanCard(
                planName = "Pro Studio",
                price = "$50",
                billingPeriod = "/ mo",
                features = listOf(
                    "100 AI-generated songs",
                    "150 min Real-time sessions",
                    "200 Cover images",
                    "40 Music videos",
                    "Commercial use license",
                    "Priority generation queue"
                ),
                badge = "MOST POPULAR",
                isHighlighted = true
            )
        }
    }
}

@com.musically.studio.ui.theme.FormFactorPreviews
@Composable
private fun PlanCardCurrentPlanPreview() {
    MaveAppTheme {
        Box(
            modifier = Modifier
                .background(MaveBackground)
                .padding(16.dp)
        ) {
            PlanCard(
                planName = "Basic Creator",
                price = "$20",
                billingPeriod = "/ mo",
                features = listOf(
                    "30 AI-generated songs",
                    "60 min Real-time sessions",
                    "50 Cover images",
                    "10 Music videos"
                ),
                isCurrentPlan = true
            )
        }
    }
}
