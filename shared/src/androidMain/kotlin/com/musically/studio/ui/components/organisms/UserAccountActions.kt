/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for UserAccountActions.kt
 */

package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.AccountDeletionState
import com.musically.studio.ui.components.molecules.AccountActionCard

@Composable
fun UserAccountActions(
    deletionState: AccountDeletionState?,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onNavigateToDevices: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        AccountActionCard(
            title = "Sign Out",
            subtitle = "Sign out of your Mave Studio account",
            icon = Icons.AutoMirrored.Filled.Logout,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleColor = MaterialTheme.colorScheme.onSurface,
            onClick = onSignOutClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountActionCard(
            title = "Devices",
            subtitle = "Manage connected smart glasses and wearables",
            icon = Icons.Default.Phone,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleColor = MaterialTheme.colorScheme.onSurface,
            onClick = onNavigateToDevices
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountActionCard(
            title = "Delete Account",
            subtitle = "Permanently remove your account and all songs",
            icon = Icons.Default.PersonOff,
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
            iconTintColor = MaterialTheme.colorScheme.onErrorContainer,
            titleColor = MaterialTheme.colorScheme.error,
            onClick = onDeleteAccountClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (deletionState is AccountDeletionState.Loading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.error,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Deleting account…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
