package com.musically.studio.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionOptionsBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
            Text(
                text = "Live Session Options",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val onShare: () -> Unit = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out my live music session on Mave!")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
                onDismiss()
            }

            val headlineShare = "Share Session"
            val shareIcon = Icons.Default.Share
            ListItem(
                headlineContent = { Text(headlineShare, color = MaterialTheme.colorScheme.onSurface) },
                leadingContent = { Icon(shareIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.clickable(onClick = onShare)
            )
            
            val onClear: () -> Unit = {
                viewModel.stopLiveSession()
                viewModel.startLiveSession()
                onDismiss()
            }

            val headlineClear = "Clear History"
            val clearIcon = Icons.Default.Clear
            ListItem(
                headlineContent = { Text(headlineClear, color = MaterialTheme.colorScheme.error) },
                leadingContent = { Icon(clearIcon, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.clickable(onClick = onClear)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
