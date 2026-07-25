package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Videocam
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var isMetaConnected by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var hudProjectionMode by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    
    val containerColor = if (hudProjectionMode) Color.Black else Color(0xFF121212)
    val textColor = if (hudProjectionMode) Color(0xFF9BBFFF) else Color.White
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = containerColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Devices & Meta Wearables", color = textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Ray-Ban Meta Smart Glasses & Intelligent Eyewear", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (isMetaConnected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isMetaConnected) "CONNECTED" else "DISCONNECTED",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Main Connection Card
            Text("CURRENT DEVICE", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            val cardBg = if (isMetaConnected) Color(0xFF282828) else Color(0xFF1E1E1E)
            val cardBorder = if (isMetaConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color(0xFF333333)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                    .clickable {
                        isScanning = true
                        statusMessage = if (isMetaConnected) "Disconnecting..." else "Connecting to Meta Wearables..."
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1500)
                            isMetaConnected = !isMetaConnected
                            isScanning = false
                            statusMessage = if (isMetaConnected) "Ray-Ban Meta Smart Glasses Connected Successfully!" else "Meta Wearables Disconnected"
                        }
                    }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isMetaConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF333333)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder for Glasses Icon
                    Text("👓", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ray-Ban Meta Smart Glasses", color = textColor, fontWeight = FontWeight.Bold)
                    Text(
                        if (isMetaConnected) "Connected • Battery 85%" else "Tap to scan and pair via Bluetooth / WebRTC",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isMetaConnected) Color(0xFF333333) else MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = if (isMetaConnected) textColor else Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isMetaConnected) "DISC" else "CONN", color = if (isMetaConnected) textColor else Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(statusMessage, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // HUD Projection
            Text("GLASSES PROJECTION & DISPLAY", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Glimmer HUD Projection Mode", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sets pure black additive background (#000000) optimized for display glasses HUD projection.", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = hudProjectionMode,
                    onCheckedChange = { hudProjectionMode = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Camera & Sensors
            Text("HARDWARE CAMERA & SENSORS", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Camera permissions active. Connect Ray-Ban Meta glasses to stream live video feeds into Mave Lyria.", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}
