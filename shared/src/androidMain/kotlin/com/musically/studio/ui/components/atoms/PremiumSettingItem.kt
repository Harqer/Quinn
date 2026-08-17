/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for PremiumSettingItem.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.musically.studio.ui.theme.MaveGreenLight
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun PremiumSettingItem(
    isPremium: Boolean,
    onNavigateToPremium: () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text("Mave Premium", color = Color.White, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(
                text = if (isPremium) "Premium — active" else "Upgrade to unlock full AI creation",
                color = if (isPremium) MaveGreenLight else Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingContent = {
            TextButton(onClick = onNavigateToPremium) {
                Text(text = if (isPremium) "Manage" else "View Plans", color = MaveGreenLight)
            }
        },
        modifier = Modifier.debouncedClickable { onNavigateToPremium() }
    )
}