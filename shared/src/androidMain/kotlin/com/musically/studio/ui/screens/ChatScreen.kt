package com.musically.studio.ui.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musically.studio.ui.components.organisms.ChatInputArea
import com.musically.studio.ui.components.organisms.ChatMessageList
import com.musically.studio.ui.theme.LocalMaveColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userPhotoUrl: String? = null,
    userDisplayName: String? = null,
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

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.recordVoice(context)
        }
    }

    val onMicClick: () -> Unit = {
        val hasMicPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasMicPermission) {
            viewModel.recordVoice(context)
        } else {
            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }
    
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
                    title = { Text("Mave Studio", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        Box(modifier = Modifier.padding(start = 16.dp)) {
                            com.musically.studio.ui.components.atoms.UserAvatarButton(
                                photoUrl = userPhotoUrl,
                                displayName = userDisplayName,
                                onClick = onMenuClick
                            )
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
                    onVoiceRecord = onMicClick,
                    onGenerateCoverArt = {
                        coroutineScope.launch {
                            inputValue = "Generate cover art for: "
                        }
                    },
                    onGenerateVideo = {
                        coroutineScope.launch {
                            inputValue = "Generate a music video for: "
                        }
                    },
                    onGeneratePodcast = {
                        viewModel.generateNarrativeSeries(topic = inputValue.takeIf { it.isNotBlank() } ?: "a random topic", type = "podcast")
                        inputValue = ""
                    },
                    onGenerateAudiobook = {
                        viewModel.generateNarrativeSeries(topic = inputValue.takeIf { it.isNotBlank() } ?: "a random story", type = "audiobook")
                        inputValue = ""
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
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
