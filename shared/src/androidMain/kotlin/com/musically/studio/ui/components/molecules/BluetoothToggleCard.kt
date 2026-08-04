package com.musically.studio.ui.components.molecules

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ColorScheme
import com.musically.studio.ui.icons.ToggleOff
import com.musically.studio.ui.icons.ToggleOn
import com.musically.studio.ui.utils.debouncedClickable
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView

@Composable
fun BluetoothToggleCard(
    isBluetoothEnabled: Boolean,
    colors: ColorScheme,
    onToggle: () -> Unit
) {
    val view = LocalView.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
            .background(colors.surfaceContainer, RoundedCornerShape(16.dp))
            .debouncedClickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onToggle()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isBluetoothEnabled) colors.primary.copy(alpha = 0.2f) else colors.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = null, tint = if (isBluetoothEnabled) colors.primary else colors.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Bluetooth", color = colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(if (isBluetoothEnabled) "On" else "Off", color = colors.onSurfaceVariant, fontSize = 14.sp)
            }
        }
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                com.musically.studio.ui.utils.executeDebounced { onToggle() }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isBluetoothEnabled) ToggleOn else ToggleOff,
                contentDescription = "Toggle Bluetooth",
                modifier = Modifier.fillMaxSize(),
                tint = if (isBluetoothEnabled) colors.primary else colors.onSurface
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun BluetoothToggleCardPreview() {
    androidx.compose.material3.MaterialTheme {
        BluetoothToggleCard(
            isBluetoothEnabled = true,
            colors = androidx.compose.material3.darkColorScheme(),
            onToggle = {}
        )
    }
}
