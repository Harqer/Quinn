package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.util.Base64
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.musically.studio.ui.theme.LocalMaveColorScheme
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.components.atoms.animated_images
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    onMenuClick: () -> Unit = {},
    onTrackClick: (String) -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputValue by remember { mutableStateOf("") }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val coroutineScope = rememberCoroutineScope()
    
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val bytes = inputStream.readBytes()
                        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        viewModel.sendVisionFrame(base64, mimeType)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to capture or encode image from URI")
                }
            }
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                    viewModel.sendVisionFrame(base64, "image/jpeg")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to capture or encode image from Camera")
                }
            }
        }
    }
    
    // Adaptive & Edge-to-edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalMaveColorScheme.current.background),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxHeight(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
            TopAppBar(
                title = { Text("Mave", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { cameraLauncher.launch(null) }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = LocalMaveColorScheme.current.onSurface)
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
                            modifier = Modifier.styleable(styleState = styleState, style = MaveStyles.sendButtonStyle)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send", tint = Color.Black)
                        }
                    } else {
                        val context = LocalContext.current
                        IconButton(onClick = { viewModel.recordVoice(context) }) {
                            Icon(Icons.Default.Mic, contentDescription = "Mic")
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    inputValue = "Generate cover art for: "
                                }
                            }
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Generate Cover Art", modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    inputValue = "Generate a music video for: "
                                }
                            }
                        ) {
                            Icon(animated_images, contentDescription = "Generate Video", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.musically.studio.shared.R.drawable.logo),
                    contentDescription = "Mave Background",
                    modifier = Modifier.fillMaxSize().alpha(0.05f),
                    contentScale = ContentScale.Crop
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues) // Handle edge-to-edge correctly
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Removed hardcoded top=24.dp
                ) {
            if (messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your personal audio curator. Ready to discover?", fontSize = 14.sp)
                    }
                }
            }
            
            items(messages) { msg ->
                if (msg.sender == "ai") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .styleable(style = MaveStyles.aiMessageBubbleStyle) // Styles API compliance
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SelectionContainer(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(msg.text, fontSize = 16.sp)
                                }
                                IconButton(onClick = { 
                                    clipboardManager.setPrimaryClip(android.content.ClipData.newPlainText("text", msg.text))
                                    android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                            }
                            
                            if (msg.tracks?.size == 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val trackInteractionSource = remember { MutableInteractionSource() }
                                val trackStyleState = rememberUpdatedStyleState(trackInteractionSource) { it.isEnabled = true }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(interactionSource = trackInteractionSource, indication = null) {
                                            // Use Data Connect track ID if available; msg.id is the chat-message ID and must NOT be used as a track ID
                                            val dcTrackId = msg.tracks?.firstOrNull()?.trackId
                                            if (dcTrackId != null) onTrackClick(dcTrackId)
                                        }
                                        .styleable(styleState = trackStyleState, style = MaveStyles.musicTrackCardStyle), // Styles API compliance
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (msg.coverArtUrl != null) {
                                        coil.compose.AsyncImage(
                                            model = msg.coverArtUrl,
                                            contentDescription = "Track Art",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(64.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
                                    }
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
                                                modifier = Modifier.size(40.dp).background(com.musically.studio.ui.theme.MaveBrand.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { 
                                        clipboardManager.setPrimaryClip(android.content.ClipData.newPlainText("text", msg.text))
                                        android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                    }
                                    SelectionContainer {
                                        Text(msg.text, fontSize = 16.sp)
                                    }
                                }
                            }
                            Text("DELIVERED", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
}
}
