/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for SearchTopBar.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.utils.debouncedClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(com.musically.studio.ui.theme.MaveBrand)
                        .debouncedClickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = com.musically.studio.ui.theme.MaveBackground),
        scrollBehavior = scrollBehavior
    )
}
