package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speaker
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
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun CurrentDeviceCard(
    currentDevice: AudioDevice?,
    colors: ColorScheme
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val styleState = androidx.compose.foundation.style.rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .styleable(styleState = styleState, style = MaveStyles.currentDeviceCardStyle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (currentDevice?.type == DeviceType.BLUETOOTH) Icons.Default.Bluetooth else Icons.Default.Smartphone, 
            contentDescription = null, 
            tint = colors.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(currentDevice?.name ?: "This phone", color = colors.onBackground, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Speaker, contentDescription = null, tint = colors.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(currentDevice?.subtitle ?: "Speakers", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
