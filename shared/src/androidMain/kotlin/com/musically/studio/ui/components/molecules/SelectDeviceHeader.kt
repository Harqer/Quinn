package com.musically.studio.ui.components.molecules

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ColorScheme

@Composable
fun SelectDeviceHeader(
    isBluetoothEnabled: Boolean,
    isScanning: Boolean,
    colors: ColorScheme,
    context: Context,
    onFindDevicesClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Select a device", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        if (isBluetoothEnabled) {
            Button(
                onClick = onFindDevicesClick,
                enabled = !isScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.onSurface,
                    contentColor = colors.surface
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.BluetoothSearching, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isScanning) "Scanning..." else "Find Devices", fontWeight = FontWeight.Bold)
            }
        }
    }
}
