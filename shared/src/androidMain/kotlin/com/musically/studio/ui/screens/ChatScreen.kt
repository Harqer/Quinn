package com.musically.studio.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musically.studio.shared.R
import com.musically.studio.ui.components.organisms.ChatInputArea
import com.musically.studio.ui.components.organisms.ChatMessageList
import com.musically.studio.ui.theme.LocalMaveColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream

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
                ChatInputArea(
                    inputValue = inputValue,
                    onValueChange = { inputValue = it },
                    onSend = {
                        viewModel.sendMessage(inputValue)
                        inputValue = ""
                    },
                    onAttachImage = { imagePickerLauncher.launch("image/*") },
                    onCameraCapture = { cameraLauncher.launch(null) },
                    onVoiceRecord = { viewModel.recordVoice(context) },
                    onGenerateCoverArt = {
                        coroutineScope.launch {
                            inputValue = "Generate cover art for: "
                        }
                    },
                    onGenerateVideo = {
                        coroutineScope.launch {
                            inputValue = "Generate a music video for: "
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.mave_brand_dark),
                    contentDescription = "Mave Background",
                    modifier = Modifier.fillMaxSize().alpha(0.05f),
                    contentScale = ContentScale.Crop
                )
                ChatMessageList(
                    messages = messages,
                    paddingValues = paddingValues,
                    context = context,
                    clipboardManager = clipboardManager,
                    onTrackClick = onTrackClick
                )
            }
        }
    }
}
