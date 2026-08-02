package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveLogo

@Composable
fun EmptyLibraryState(onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Mave Molecule: High-fidelity empty state
            MaveLogo(size = 100, modifier = Modifier.alpha(0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your Studio is empty",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Strike your first song with Mave to start building your personal orchestra.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            MaveButton(
                text = "Create a Song",
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
