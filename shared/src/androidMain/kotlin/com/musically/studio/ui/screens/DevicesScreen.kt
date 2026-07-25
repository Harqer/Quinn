package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.rememberUpdatedStyleState
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.LocalMaveColorScheme
import com.musically.studio.ui.theme.MaveStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var salonInvitesEnabled by remember { mutableStateOf(true) }
    var showTooltip by remember { mutableStateOf(true) }
    val colors = LocalMaveColorScheme.current
    val devices by viewModel.devices.collectAsStateWithLifecycle()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text("Current device", color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                
                val currentDevice = devices.find { it.isCurrent }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .styleable(style = MaveStyles.currentDeviceCardStyle), // Encapsulated in Styles API
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (currentDevice?.type == com.musically.studio.ui.models.DeviceType.BLUETOOTH) Icons.Default.Bluetooth else Icons.Default.Smartphone, 
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

            item {
                Text("Select a device", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 32.dp, bottom = 16.dp))
            }

            items(devices.filter { !it.isCurrent }) { device ->
                val interactionSource = remember { MutableInteractionSource() }
                val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(interactionSource = interactionSource, indication = null) { 
                            viewModel.selectDevice(device)
                        }
                        .styleable(styleState = styleState, style = MaveStyles.deviceCardStyle)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                if (device.type == com.musically.studio.ui.models.DeviceType.BLUETOOTH) Icons.Default.Bluetooth else Icons.Default.SpeakerGroup, 
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

                        if (device.type == com.musically.studio.ui.models.DeviceType.SPEAKER) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Multiple people can join and control this speaker",
                                    color = colors.onSurface,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Switch(
                                    checked = salonInvitesEnabled,
                                    onCheckedChange = { salonInvitesEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = colors.onPrimary,
                                        checkedTrackColor = colors.primary,
                                        uncheckedThumbColor = colors.onSurface,
                                        uncheckedTrackColor = colors.surfaceContainerHighest
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
