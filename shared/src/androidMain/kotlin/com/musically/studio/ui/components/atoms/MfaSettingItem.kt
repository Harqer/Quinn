package com.musically.studio.ui.components.atoms

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.musically.studio.ui.theme.MaveGreenLight

@Composable
fun MfaSettingItem(
    hasMfa: Boolean,
    onNavigateToMfa: () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text("Two-Factor Authentication", color = Color.White, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(
                text = if (hasMfa) "Enabled via Phone" else "Add extra security to your account",
                color = if (hasMfa) MaveGreenLight else Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingContent = {
            if (!hasMfa) {
                TextButton(onClick = onNavigateToMfa) {
                    Text(text = "Enable", color = MaveGreenLight)
                }
            }
        }
    )
}