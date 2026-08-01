package com.musically.studio.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.*
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
