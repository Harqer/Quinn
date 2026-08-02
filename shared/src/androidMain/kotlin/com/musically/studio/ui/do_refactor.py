import os

BASE_DIR = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui"
SCREENS_DIR = os.path.join(BASE_DIR, "screens")
COMPONENTS_DIR = os.path.join(BASE_DIR, "components")
ATOMS_DIR = os.path.join(COMPONENTS_DIR, "atoms")
MOLECULES_DIR = os.path.join(COMPONENTS_DIR, "molecules")
ORGANISMS_DIR = os.path.join(COMPONENTS_DIR, "organisms")

os.makedirs(ATOMS_DIR, exist_ok=True)
os.makedirs(MOLECULES_DIR, exist_ok=True)
os.makedirs(ORGANISMS_DIR, exist_ok=True)

chat_bubble_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.models.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage, viewModel: MainViewModel, modifier: Modifier = Modifier) {
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
        val context = androidx.compose.ui.platform.LocalContext.current
        val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (message.isUser) {
                IconButton(onClick = { 
                    clipboardManager.setPrimaryClip(android.content.ClipData.newPlainText("text", message.text))
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
                    clipboardManager.setPrimaryClip(android.content.ClipData.newPlainText("text", message.text))
                    android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = com.musically.studio.ui.theme.MaveBlueGray200)
                }
            }
        }
    }
}
"""

thinking_bubble_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ThinkingBubble(text: String, modifier: Modifier = Modifier) {
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
"""

empty_session_state_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptySessionState(onStartSession: () -> Unit) {
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
"""

session_waiting_state_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SessionWaitingState() {
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
"""

live_top_bar_code = """package com.musically.studio.ui.components.organisms

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@Composable
fun LiveSessionTopBar(
    viewModel: MainViewModel,
    isLiveSessionActive: Boolean,
    isWearableConnected: Boolean,
    isWearableStreamingEnabled: Boolean,
    generatedPrompts: List<String>,
    onNavigateBack: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
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
}
"""

live_bottom_bar_code = """package com.musically.studio.ui.components.organisms

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@Composable
fun LiveSessionBottomBar(
    viewModel: MainViewModel,
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    isRecording: Boolean,
    isLiveSessionActive: Boolean,
    context: Context,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
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
                    onValueChange = onInputTextChanged,
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
                                onInputTextChanged("")
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
"""

with open(os.path.join(MOLECULES_DIR, "ChatBubble.kt"), "w") as f: f.write(chat_bubble_code)
with open(os.path.join(MOLECULES_DIR, "ThinkingBubble.kt"), "w") as f: f.write(thinking_bubble_code)
with open(os.path.join(MOLECULES_DIR, "EmptySessionState.kt"), "w") as f: f.write(empty_session_state_code)
with open(os.path.join(MOLECULES_DIR, "SessionWaitingState.kt"), "w") as f: f.write(session_waiting_state_code)
with open(os.path.join(ORGANISMS_DIR, "LiveSessionTopBar.kt"), "w") as f: f.write(live_top_bar_code)
with open(os.path.join(ORGANISMS_DIR, "LiveSessionBottomBar.kt"), "w") as f: f.write(live_bottom_bar_code)

live_screen_imports = """package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.molecules.*
import com.musically.studio.ui.components.organisms.*
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
            LiveSessionTopBar(
                viewModel = viewModel,
                isLiveSessionActive = isLiveSessionActive,
                isWearableConnected = isWearableConnected,
                isWearableStreamingEnabled = isWearableStreamingEnabled,
                generatedPrompts = generatedPrompts,
                onNavigateBack = onNavigateBack,
                onMoreOptionsClick = onMoreOptionsClick
            )
        },
        bottomBar = {
            LiveSessionBottomBar(
                viewModel = viewModel,
                inputText = inputText,
                onInputTextChanged = { inputText = it },
                isRecording = isRecording,
                isLiveSessionActive = isLiveSessionActive,
                context = context,
                onNavigateToCamera = onNavigateToCamera,
                onNavigateToGallery = onNavigateToGallery
            )
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
"""

with open(os.path.join(SCREENS_DIR, "LiveSessionScreen.kt"), "w") as f: f.write(live_screen_imports)


recent_track_item_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack

@Composable
fun RecentTrackItem(
    track: MaveTrack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(com.musically.studio.ui.theme.MaveSurfaceContainer)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = track.album.images.firstOrNull()?.url
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.DarkGray)
            )
        }
        Text(
            text = track.name,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
"""

mave_card_code = """package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack

@Composable
fun MaveCard(
    track: MaveTrack,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        val imageUrl = track.album.images.firstOrNull()?.url
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = track.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.name,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artists.firstOrNull()?.name ?: "",
            color = com.musically.studio.ui.theme.MaveGray300,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
"""

recent_tracks_grid_code = """package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.RecentTrackItem

@Composable
fun RecentTracksGrid(
    tracks: List<MaveTrack>,
    onTrackClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val rows = tracks.chunked(2)
        rows.forEach { rowTracks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTracks.forEach { track ->
                    RecentTrackItem(
                        track = track,
                        modifier = Modifier.weight(1f),
                        onClick = { onTrackClick(track.id) }
                    )
                }
                // Fill empty space if odd number
                if (rowTracks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
"""

mave_carousel_code = """package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.MaveCard

@Composable
fun MaveCarousel(
    title: String,
    tracks: List<MaveTrack>,
    onTrackClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tracks) { track ->
                MaveCard(
                    track = track,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }
    }
}
"""

category_cards_row_code = """package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCardsRow(onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        "Pop" to Color(0xFF9333EA), // Purple
        "Indie" to Color(0xFF059669), // Emerald
        "Workout" to Color(0xFFE11D48), // Rose
        "Chill" to Color(0xFF0284C7) // Sky Blue
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Generate a Vibe",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { (name, color) ->
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .clickable { onCategoryClick(name) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
"""

mave_home_top_bar_code = """package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveHomeTopBar(
    photoUrl: String?,
    displayName: String?,
    onNavigateToProfile: () -> Unit
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToProfile() }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(com.musically.studio.ui.theme.MaveBrand)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = displayName?.firstOrNull()?.toString()?.uppercase() ?: "M"
                        Text(
                            text = initial,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = greeting,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = com.musically.studio.ui.theme.MaveBackground.copy(alpha = 0.9f)
        )
    )
}
"""

with open(os.path.join(MOLECULES_DIR, "RecentTrackItem.kt"), "w") as f: f.write(recent_track_item_code)
with open(os.path.join(MOLECULES_DIR, "MaveCard.kt"), "w") as f: f.write(mave_card_code)
with open(os.path.join(ORGANISMS_DIR, "RecentTracksGrid.kt"), "w") as f: f.write(recent_tracks_grid_code)
with open(os.path.join(ORGANISMS_DIR, "MaveCarousel.kt"), "w") as f: f.write(mave_carousel_code)
with open(os.path.join(ORGANISMS_DIR, "CategoryCardsRow.kt"), "w") as f: f.write(category_cards_row_code)
with open(os.path.join(ORGANISMS_DIR, "MaveHomeTopBar.kt"), "w") as f: f.write(mave_home_top_bar_code)


home_screen_imports = """package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.organisms.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveHomeScreen(
    viewModel: MainViewModel,
    onNavigateToProfile: () -> Unit = {},
    onTrackClick: (String) -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchCommunityTracks()
    }

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            MaveHomeTopBar(
                photoUrl = viewModel.getUserPhotoUrl(),
                displayName = viewModel.getUserDisplayName(),
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        if (isLoading && tracks.isEmpty() && communityTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = com.musically.studio.ui.theme.MaveBrand)
            }
        } else if (errorMessage != null && tracks.isEmpty() && communityTracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage ?: "An error occurred", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    com.musically.studio.ui.components.atoms.MaveButton(
                        text = "Retry",
                        onClick = { 
                            viewModel.clearCatalogError()
                            viewModel.fetchUserTracks()
                            viewModel.fetchCommunityTracks() 
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                item {
                    CategoryCardsRow(
                        onCategoryClick = { category ->
                            viewModel.sendTextCommand("Generate a $category song")
                            viewModel.navigateTo(com.musically.studio.ui.navigation.Route.LiveSession)
                        }
                    )
                }

                val recentTracks = (if (tracks.isNotEmpty()) tracks else communityTracks).take(6)
                if (recentTracks.isNotEmpty()) {
                    item {
                        RecentTracksGrid(
                            tracks = recentTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                val madeForYouTracks = if (tracks.isNotEmpty()) tracks.take(5) else communityTracks.take(5)
                if (madeForYouTracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Made for you",
                            tracks = madeForYouTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                if (communityTracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Community Songs",
                            tracks = communityTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                if (tracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Recently played",
                            tracks = tracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }
            }
        }
    }
}
"""

with open(os.path.join(SCREENS_DIR, "MaveHomeScreen.kt"), "w") as f: f.write(home_screen_imports)

print("Done generating files!")
