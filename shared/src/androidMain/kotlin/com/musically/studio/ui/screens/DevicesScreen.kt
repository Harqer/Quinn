package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var salonInvitesEnabled by remember { mutableStateOf(true) }
    var showTooltip by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your devices", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text("Current device", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF282828), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Smartphone, contentDescription = null, tint = Color(0xFF1DB954))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("This phone", color = Color.White, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speaker, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speakers", color = Color(0xFF1DB954), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Text("Select a device", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 32.dp, bottom = 16.dp))
                
                // Laptop
                DeviceItem(
                    icon = { Icon(Icons.Default.LaptopMac, contentDescription = null, tint = Color.Gray) },
                    name = "Alexandra's Laptop"
                )
                
                // Bureau
                DeviceItem(
                    icon = { Icon(Icons.Default.SpeakerGroup, contentDescription = null, tint = Color.Gray) },
                    name = "Bureau",
                    subtitle = "Google Cast"
                )
                
                // Salon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFF282828), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF9C27B0).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.SpeakerGroup, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Salon", color = Color.White, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Cast, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Connecting...", color = Color(0xFF1DB954), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.DarkGray, CircleShape)
                                        .border(1.dp, Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Multiple people can join and control this speaker",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Switch(
                                checked = salonInvitesEnabled,
                                onCheckedChange = { salonInvitesEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF1DB954),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.DarkGray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Invite", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        
                        if (showTooltip) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2E77ED), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Invite nearby friends to queue songs and control what's playing on this speaker",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { showTooltip = false }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                
                // Commode
                DeviceItem(
                    icon = { Icon(Icons.Default.SpeakerGroup, contentDescription = null, tint = Color.Gray) },
                    name = "Commode",
                    subtitle = "Google Cast"
                )

                // Meta Wearables
                DeviceItem(
                    icon = { Text("👓", fontSize = 24.sp) },
                    name = "Ray-Ban Meta",
                    subtitle = "Bluetooth"
                )
            }
        }
    }
}

@Composable
fun DeviceItem(
    icon: @Composable () -> Unit,
    name: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (subtitle.contains("Cast")) {
                        Icon(Icons.Default.Cast, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    } else if (subtitle.contains("Bluetooth")) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
