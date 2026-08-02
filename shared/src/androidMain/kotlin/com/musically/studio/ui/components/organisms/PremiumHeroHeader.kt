package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveOnSurface
import com.musically.studio.ui.theme.MaveOnSurfaceVariant

@Composable
fun PremiumHeroHeader(isPremium: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Gradient wordmark
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaveOnSurface)) { append("Mave ") }
                withStyle(SpanStyle(color = MaveBrand)) { append("Premium") }
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )

        Text(
            text = "Unlock the full power of AI music creation.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaveOnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isPremium) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaveBrand.copy(alpha = 0.15f),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaveBrand,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "You're a Premium member",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaveBrand
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
