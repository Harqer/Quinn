package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val tracks: List<ChatTrack>? = null
)

data class ChatTrack(
    val title: String,
    val artist: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit
) {
    var messages by remember { 
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "1",
                    sender = "ai",
                    text = "Hi! I've been analyzing your recent listening habits. Based on your love for synth-heavy tracks, I think you'll enjoy this new release.",
                    tracks = listOf(ChatTrack("Neon Dreams", "Cyberwave Collective"))
                ),
                ChatMessage(
                    id = "2",
                    sender = "user",
                    text = "That sounds exactly like what I need. Can you find more like this but maybe with a slower tempo?"
                ),
                ChatMessage(
                    id = "3",
                    sender = "ai",
                    text = "Sure thing. Here are a few \"Slow-Synth\" tracks that match that vibe perfectly:",
                    tracks = listOf(
                        ChatTrack("Midnight City Lights", "Digital Sunset"),
                        ChatTrack("Echoes of Tomorrow", "Vapor Theory")
                    )
                )
            )
        )
    }
    
    var inputValue by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mave", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212).copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF282828), CircleShape)
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Add */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF1DB954))
                    }
                    
                    TextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        placeholder = { Text("Ask Mave anything...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (inputValue.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val msg = ChatMessage(id = System.currentTimeMillis().toString(), sender = "user", text = inputValue.trim())
                                messages = messages + msg
                                inputValue = ""
                                scope.launch {
                                    delay(1000)
                                    messages = messages + ChatMessage(
                                        id = System.currentTimeMillis().toString(),
                                        sender = "ai",
                                        text = "I found this based on your request! Enjoy."
                                    )
                                }
                            },
                            modifier = Modifier
                                .background(Color(0xFF1DB954), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    } else {
                        IconButton(onClick = { /* Mic */ }) {
                            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.Gray)
                        }
                        IconButton(onClick = { /* Camera */ }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color.Gray)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.GraphicEq, 
                        contentDescription = null, 
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(48.dp).padding(bottom = 8.dp)
                    )
                    Text("Your personal audio curator. Ready to discover?", color = Color.Gray, fontSize = 14.sp)
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
                                .background(Color(0xFF1DB954).copy(alpha = 0.2f), CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .background(Color(0xFF282828), RoundedCornerShape(16.dp).copy(topStart = RoundedCornerShape(0.dp)))
                                .padding(16.dp)
                        ) {
                            Text(msg.text, color = Color.White, fontSize = 16.sp)
                            
                            if (msg.tracks?.size == 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF181818), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(64.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(msg.tracks[0].title, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(msg.tracks[0].artist, color = Color.Gray, fontSize = 14.sp)
                                    }
                                    Box(
                                        modifier = Modifier.size(40.dp).background(Color(0xFF1DB954), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
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
                                                Icon(Icons.Default.Album, contentDescription = null, tint = Color(0xFF1DB954))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(track.artist, color = Color.Gray, fontSize = 12.sp)
                                            }
                                            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = Color.Gray)
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
                                    .background(Color(0xFF1DB954).copy(alpha = 0.2f), RoundedCornerShape(16.dp).copy(topEnd = RoundedCornerShape(0.dp)))
                                    .padding(16.dp)
                            ) {
                                Text(msg.text, color = Color(0xFF1DB954), fontSize = 16.sp)
                            }
                            Text("DELIVERED", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
