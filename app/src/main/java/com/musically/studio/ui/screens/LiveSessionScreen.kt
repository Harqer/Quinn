package com.musically.studio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.models.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val isLiveSessionActive by viewModel.isLiveSessionActive.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val thinkingText by viewModel.thinkingText.collectAsStateWithLifecycle()
    val generatedPrompts by viewModel.generatedPrompts.collectAsStateWithLifecycle()
    val isWearableConnected by viewModel.isWearableConnected.collectAsStateWithLifecycle()
    val isWearableStreamingEnabled by viewModel.isWearableFrameStreamingEnabled.collectAsStateWithLifecycle()
    val messages = viewModel.messages

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!isLiveSessionActive) {
            viewModel.startLiveSession()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopLiveSession() }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D0F10), Color(0xFF121416), Color(0xFF0A0C0E))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mave Live", style = MaterialTheme.typography.titleMedium,
                        color = Color.White, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(
                            if (isLiveSessionActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E), CircleShape))
                        Text(
                            text = if (isLiveSessionActive) "Connected" else "Disconnected",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLiveSessionActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                        )
                    }
                }
                Row {
                    if (isWearableConnected) {
                        IconButton(onClick = { viewModel.toggleWearableFrameStreaming() }) {
                            Icon(
                                imageVector = if (isWearableStreamingEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Toggle Wearable Camera Stream",
                                tint = if (isWearableStreamingEnabled) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }
                }
            }

            // Generated prompts banner
            AnimatedVisibility(
                visible = generatedPrompts.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2430)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Generated Music Vibes", style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF9EAABF), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        generatedPrompts.take(3).forEach { prompt ->
                            Row(modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(prompt, style = MaterialTheme.typography.bodySmall,
                                    color = Color.White, maxLines = 2)
                            }
                        }
                    }
                }
            }

            // Chat Messages
            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty() && !isLiveSessionActive) {
                    EmptySessionState(onStartSession = { viewModel.startLiveSession() })
                } else if (messages.isEmpty() && thinkingText.isBlank()) {
                    SessionWaitingState()
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (thinkingText.isNotBlank()) {
                            item { ThinkingBubble(text = thinkingText) }
                        }
                        items(messages, key = { it.hashCode() }) { message ->
                            ChatBubble(message = message)
                        }
                    }
                }
                if (messages.isEmpty() && thinkingText.isNotBlank()) {
                    ThinkingBubble(
                        text = thinkingText,
                        modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                    )
                }
            }

            // Bottom Input Area
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = Color(0xFF161820),
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = onNavigateToCamera,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E2430))
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null,
                                modifier = Modifier.size(16.dp), tint = Color(0xFF9EAABF))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera", style = MaterialTheme.typography.labelMedium, color = Color(0xFF9EAABF))
                        }
                        FilledTonalButton(
                            onClick = onNavigateToGallery,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E2430))
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null,
                                modifier = Modifier.size(16.dp), tint = Color(0xFF9EAABF))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery", style = MaterialTheme.typography.labelMedium, color = Color(0xFF9EAABF))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text("Describe a vibe...", color = Color(0xFF5A6270),
                                    style = MaterialTheme.typography.bodyMedium)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFF2A2D35),
                                focusedContainerColor = Color(0xFF1A1D23),
                                unfocusedContainerColor = Color(0xFF1A1D23),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            trailingIcon = {
                                if (inputText.isNotBlank()) {
                                    IconButton(onClick = {
                                        viewModel.sendTextCommand(inputText.trim())
                                        inputText = ""
                                    }) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send",
                                            tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        )
                        FloatingActionButton(
                            onClick = {
                                if (!isLiveSessionActive) viewModel.startLiveSession()
                                viewModel.recordVoice()
                            },
                            modifier = Modifier.size(52.dp),
                            containerColor = if (isRecording) Color(0xFFE53935) else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Stop Recording" else "Start Voice Input",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier.size(32.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier.widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 4.dp,
                    topEnd = if (message.isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp, bottomEnd = 16.dp
                ))
                .background(if (message.isUser) MaterialTheme.colorScheme.primary else Color(0xFF1E2430))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = message.text, style = MaterialTheme.typography.bodyMedium,
                color = if (message.isUser) Color.White else Color(0xFFCDD5E0))
        }
    }
}

@Composable
private fun ThinkingBubble(text: String, modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "think").animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "thinkAlpha"
    )
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(32.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.widthIn(max = 280.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(Color(0xFF1E2430))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9EAABF).copy(alpha = alpha), maxLines = 4)
        }
    }
}

@Composable
private fun EmptySessionState(onStartSession: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.WifiOff, contentDescription = null,
            tint = Color(0xFF4A5260), modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Session not connected", style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF9EAABF), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap below to start your real-time music generation session",
            style = MaterialTheme.typography.bodyMedium, color = Color(0xFF4A5260),
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStartSession) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Session")
        }
    }
}

@Composable
private fun SessionWaitingState() {
    val alpha by rememberInfiniteTransition(label = "wait").animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "waitAlpha"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.GraphicEq, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Connected — say something or type a vibe",
            style = MaterialTheme.typography.bodyLarge, color = Color(0xFF9EAABF),
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("You can also tap the camera icon to generate music from a photo",
            style = MaterialTheme.typography.bodySmall, color = Color(0xFF4A5260),
            textAlign = TextAlign.Center)
    }
}
