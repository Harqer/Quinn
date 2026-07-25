package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.rememberUpdatedStyleState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musically.studio.ui.theme.LocalMaveColorScheme
import com.musically.studio.ui.theme.MaveStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputValue by remember { mutableStateOf("") }
    
    // Adaptive & Edge-to-edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalMaveColorScheme.currentValue.background),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxHeight(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
            TopAppBar(
                title = { Text("Mave", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars) // Edge-to-Edge compliance
                    .windowInsetsPadding(WindowInsets.ime)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .styleable(style = MaveStyles.chatInputRowStyle), // Styles API compliance
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Add */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    
                    TextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        placeholder = { Text("Ask Mave anything...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (inputValue.isNotBlank()) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
                        
                        IconButton(
                            onClick = {
                                viewModel.sendMessage(inputValue)
                                inputValue = ""
                            },
                            interactionSource = interactionSource,
                            modifier = Modifier.styleable(state = styleState, style = MaveStyles.sendButtonStyle)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    } else {
                        IconButton(onClick = { /* Mic */ }) {
                            Icon(Icons.Default.Mic, contentDescription = "Mic")
                        }
                        IconButton(onClick = { /* CameraX Feature Integration */ }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = LocalMaveColorScheme.currentValue.onSurface)
                        }
                    }
                }
            }
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues) // Handle edge-to-edge correctly
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Removed hardcoded top=24.dp
            ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.GraphicEq, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp).padding(bottom = 8.dp)
                    )
                    Text("Your personal audio curator. Ready to discover?", fontSize = 14.sp)
                }
            }
            
            items(messages) { msg ->
                if (msg.sender == "ai") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .styleable(style = MaveStyles.aiMessageBubbleStyle) // Styles API compliance
                        ) {
                            Text(msg.text, fontSize = 16.sp)
                            
                            if (msg.tracks?.size == 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val trackInteractionSource = remember { MutableInteractionSource() }
                                val trackStyleState = rememberUpdatedStyleState(trackInteractionSource) { it.isEnabled = true }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(interactionSource = trackInteractionSource, indication = null) {}
                                        .styleable(state = trackStyleState, style = MaveStyles.musicTrackCardStyle), // Styles API compliance
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(64.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(msg.tracks[0].title, fontWeight = FontWeight.Bold)
                                        Text(msg.tracks[0].artist, fontSize = 14.sp)
                                    }
                                    Box(
                                        modifier = Modifier.size(40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                                    }
                                }
                            } else if ((msg.tracks?.size ?: 0) > 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    msg.tracks?.forEach { track ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(40.dp).background(Color(0xFF1DB954).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Album, contentDescription = null)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(track.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(track.artist, fontSize = 12.sp)
                                            }
                                            Icon(Icons.Default.AddCircle, contentDescription = "Add")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .styleable(style = MaveStyles.userMessageBubbleStyle) // Styles API compliance
                            ) {
                                Text(msg.text, fontSize = 16.sp)
                            }
                            Text("DELIVERED", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
