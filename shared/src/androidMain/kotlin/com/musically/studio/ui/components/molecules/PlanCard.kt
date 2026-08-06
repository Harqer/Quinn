package com.musically.studio.ui.components.molecules
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.musically.studio.ui.components.atoms.PlanFeatureRow
import com.musically.studio.ui.components.atoms.PlanCtaButton
import com.musically.studio.ui.theme.*

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

    val cardA11yDescription = "$planName plan, $price. " + (badge?.let { "$it. " } ?: "") +
        (if (isCurrentPlan) "This is your current plan." else "Tap to subscribe.")

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
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaveBackground,
                    modifier = Modifier.clip(MaterialTheme.shapes.extraSmall).background(MaveBrand).padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isHighlighted) Icon(Icons.Default.Star, contentDescription = null, tint = MaveBrand, modifier = Modifier.size(18.dp))
                Text(text = planName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isHighlighted) MaveBrand else MaveOnSurface)
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = price, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaveOnSurface)
                Text(text = billingPeriod, style = MaterialTheme.typography.bodySmall, color = MaveOnSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaveOnSurfaceVariant.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(4.dp))

            features.forEach { feature -> PlanFeatureRow(feature = feature) }

            Spacer(modifier = Modifier.height(8.dp))

            PlanCtaButton(
                planName = planName,
                isHighlighted = isHighlighted,
                isCurrentPlan = isCurrentPlan,
                isLoading = isLoading,
                onSelectClick = onSelectClick
            )
        }
    }
}
