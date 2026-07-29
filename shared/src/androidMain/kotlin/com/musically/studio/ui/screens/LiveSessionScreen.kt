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
    onNavigateToGallery: () -> Unit,
    onMoreOptionsClick: () -> Unit = {}
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
    val context = androidx.compose.ui.platform.LocalContext.current

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
        colors = listOf(com.musically.studio.ui.theme.MaveGray800, com.musically.studio.ui.theme.MaveBackgroundVariant5, com.musically.studio.ui.theme.MaveGray850)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                            if (isLiveSessionActive) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400, CircleShape))
                        Text(
                            text = if (isLiveSessionActive) "Connected" else "Disconnected",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLiveSessionActive) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400
                        )
                    }
                }
                Row {
                    if (isWearableConnected) {
                        IconButton(onClick = { viewModel.toggleWearableFrameStreaming() }) {
                            Icon(
                                imageVector = if (isWearableStreamingEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Toggle Wearable Camera Stream",
                                tint = if (isWearableStreamingEnabled) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400
                            )
                        }
                    }
                    IconButton(onClick = onMoreOptionsClick) {
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
                    colors = CardDefaults.cardColors(containerColor = com.musically.studio.ui.theme.MaveDarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Generated Music Vibes", style = MaterialTheme.typography.labelMedium,
                            color = com.musically.studio.ui.theme.MaveBlueGray400, fontWeight = FontWeight.SemiBold)
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
            }
        },
        bottomBar = {
            // Bottom Input Area
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding(),
                color = com.musically.studio.ui.theme.MaveBackgroundVariant2,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text("Describe a song...", color = com.musically.studio.ui.theme.MaveGray500,
                                    style = MaterialTheme.typography.bodyMedium)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = com.musically.studio.ui.theme.MaveSurfaceVariant4,
                                focusedContainerColor = com.musically.studio.ui.theme.MaveBackgroundVariant4,
                                unfocusedContainerColor = com.musically.studio.ui.theme.MaveBackgroundVariant4,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            leadingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = onNavigateToCamera) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = com.musically.studio.ui.theme.MaveBlueGray400)
                                    }
                                    IconButton(onClick = onNavigateToGallery) {
                                        Icon(Icons.Default.Add, contentDescription = "Upload", tint = com.musically.studio.ui.theme.MaveBlueGray400)
                                    }
                                }
                            },
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
                                viewModel.recordVoice(context)
                            },
                            modifier = Modifier.size(52.dp),
                            containerColor = if (isRecording) com.musically.studio.ui.theme.MaveRed600 else MaterialTheme.colorScheme.primary,
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
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            if (messages.isEmpty() && !isLiveSessionActive) {
                EmptySessionState(onStartSession = { viewModel.startLiveSession() })
            } else if (messages.isEmpty() && thinkingText.isBlank()) {
                SessionWaitingState()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxHeight().widthIn(max = 840.dp).fillMaxWidth(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = paddingValues.calculateTopPadding() + 8.dp,
                            bottom = paddingValues.calculateBottomPadding() + 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (thinkingText.isNotBlank()) {
                            item { ThinkingBubble(text = thinkingText) }
                        }
                        items(messages, key = { it.hashCode() }) { message ->
                            ChatBubble(message = message, viewModel = viewModel)
                        }
                    }
                }
            }
            if (messages.isEmpty() && thinkingText.isNotBlank()) {
                ThinkingBubble(
                    text = thinkingText,
                    modifier = Modifier.align(Alignment.TopCenter)
                        .padding(top = paddingValues.calculateTopPadding() + 16.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val track = tracks.find { it.id == message.trackId }
    val bgColor = remember(message.trackId) {
        val hash = kotlin.math.abs(message.trackId?.hashCode() ?: message.hashCode())
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            val imageUrl = track?.album?.images?.firstOrNull()?.url
            if (imageUrl != null) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Track Art",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier.size(32.dp)
                        .background(bgColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
        val context = androidx.compose.ui.platform.LocalContext.current
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (message.isUser) {
                IconButton(onClick = { 
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(message.text))
                    android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = Color.White)
                }
            }
            Box(
                modifier = Modifier.widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(
                        topStart = if (message.isUser) 16.dp else 4.dp,
                        topEnd = if (message.isUser) 4.dp else 16.dp,
                        bottomStart = 16.dp, bottomEnd = 16.dp
                    ))
                    .background(if (message.isUser) MaterialTheme.colorScheme.primary else bgColor.copy(alpha = 0.3f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) Color.White else com.musically.studio.ui.theme.MaveBlueGray200)
            }
            if (!message.isUser) {
                IconButton(onClick = { 
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(message.text))
                    android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = com.musically.studio.ui.theme.MaveBlueGray200)
                }
            }
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
                .background(com.musically.studio.ui.theme.MaveDarkSurfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodySmall,
                color = com.musically.studio.ui.theme.MaveBlueGray400.copy(alpha = alpha), maxLines = 4)
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
            tint = com.musically.studio.ui.theme.MaveGray600, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Session not connected", style = MaterialTheme.typography.titleMedium,
            color = com.musically.studio.ui.theme.MaveBlueGray400, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap below to start your real-time music generation session",
            style = MaterialTheme.typography.bodyMedium, color = com.musically.studio.ui.theme.MaveGray600,
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
        Text("Connected — say something or type a song description",
            style = MaterialTheme.typography.bodyLarge, color = com.musically.studio.ui.theme.MaveBlueGray400,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("You can also tap the camera icon to generate music from a photo",
            style = MaterialTheme.typography.bodySmall, color = com.musically.studio.ui.theme.MaveGray600,
            textAlign = TextAlign.Center)
    }
}
