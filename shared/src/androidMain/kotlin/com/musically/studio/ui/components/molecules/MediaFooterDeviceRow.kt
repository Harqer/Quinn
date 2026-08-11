package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.models.AudioDevice

@Composable
fun MediaFooterDeviceRow(
    devices: List<AudioDevice>,
    onDeviceClick: () -> Unit,
    onShare: () -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .semantics(mergeDescendants = true) {}
                .clickable { onDeviceClick() }
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Bluetooth, contentDescription = "Device", tint = com.musically.studio.ui.theme.MaveBrand, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(devices.firstOrNull()?.name ?: "Phone Speaker", color = com.musically.studio.ui.theme.MaveBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
            IconButton(onClick = onQueueClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Queue", tint = Color.White)
            }
        }
    }
}
