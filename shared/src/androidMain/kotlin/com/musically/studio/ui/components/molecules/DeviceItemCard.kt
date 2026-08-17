/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for DeviceItemCard.kt
 */

package com.musically.studio.ui.components.molecules

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.models.DeviceType
import androidx.compose.material3.ColorScheme
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.rememberUpdatedStyleState
import com.musically.studio.ui.theme.MaveStyles

import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun DeviceItemCard(
    device: AudioDevice,
    colors: ColorScheme,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable { onClick() }
            .styleable(styleState = styleState, style = MaveStyles.deviceCardStyle),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    if (device.type == DeviceType.BLUETOOTH) Icons.Default.Bluetooth else Icons.Default.SpeakerGroup, 
                    contentDescription = null, 
                    tint = colors.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(device.name, color = colors.onBackground, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cast, contentDescription = null, tint = colors.primary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(device.subtitle, color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DeviceItemCardPreview() {
    val dummyDevice = AudioDevice(
        id = "1",
        name = "Ray-Ban Meta",
        subtitle = "Connected",
        type = DeviceType.BLUETOOTH,
        isCurrent = false
    )
    androidx.compose.material3.MaterialTheme {
        DeviceItemCard(
            device = dummyDevice,
            colors = androidx.compose.material3.darkColorScheme(),
            onClick = {}
        )
    }
}
