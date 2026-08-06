package com.musically.studio.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.LocalMaveColorScheme
import com.musically.studio.ui.components.molecules.BluetoothToggleCard
import com.musically.studio.ui.components.molecules.CurrentDeviceCard
import com.musically.studio.ui.components.molecules.DeviceItemCard
import com.musically.studio.ui.components.molecules.SelectDeviceHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(true) }
    val colors = LocalMaveColorScheme.current
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val availableDevices by remember {
        derivedStateOf {
            devices.filter { !it.isCurrent }
        }
    }

    val isBluetoothEnabled by viewModel.isBluetoothEnabled.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAudioDevices()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxHeight(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
            TopAppBar(
                title = { Text("Your devices", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(320.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BluetoothToggleCard(
                        isBluetoothEnabled = isBluetoothEnabled,
                        colors = colors,
                        onToggle = {
                            if (!isBluetoothEnabled) {
                                try {
                                    val intent = android.content.Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Log or ignore if permission missing
                                }
                            } else {
                                viewModel.startBluetoothDiscovery(context)
                            }
                        }
                    )

                    Text("Current device", color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    
                    val currentDevice = devices.find { it.isCurrent }
                    
                    CurrentDeviceCard(
                        currentDevice = currentDevice,
                        colors = colors
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SelectDeviceHeader(
                    isBluetoothEnabled = isBluetoothEnabled,
                    isScanning = isScanning,
                    colors = colors,
                    context = context,
                    onFindDevicesClick = { viewModel.startBluetoothDiscovery(context) }
                )
            }

            items(availableDevices, key = { it.name }) { device ->
                DeviceItemCard(
                    device = device,
                    colors = colors,
                    onClick = {
                        if (device.name.contains("Meta", ignoreCase = true) || device.name.contains("Ray-Ban", ignoreCase = true)) {
                            if (context is android.app.Activity) {
                                val isRegistered = com.meta.wearable.dat.core.Wearables.registrationState.value == com.meta.wearable.dat.core.types.RegistrationState.REGISTERED
                                if (!isRegistered) {
                                    com.meta.wearable.dat.core.Wearables.startRegistration(context)
                                }
                            }
                        }
                        viewModel.selectDevice(device)
                    }
                )
            }
        }
        }
    }
}


