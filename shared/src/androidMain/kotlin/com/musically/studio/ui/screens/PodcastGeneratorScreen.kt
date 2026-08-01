package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.molecules.AIMessageBubble
import com.musically.studio.ui.components.molecules.UserMessageBubble
import com.musically.studio.ui.components.organisms.PodcastInputBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastGeneratorScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    var promptText by remember { mutableStateOf("") }
    
    // Lumina Synth Design System Colors
    val surfaceColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.surfaceContainerHigh
    val backgroundColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.background
    val primaryElectricViolet = com.musically.studio.ui.theme.MavePurple500
    val secondaryNeonCyan = com.musically.studio.ui.theme.MaveCyan500
    val onSurfaceColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.onSurface
    
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mave Studio", 
                        color = onSurfaceColor,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = onSurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
            val context = LocalContext.current
            
            PodcastInputBar(
                promptText = promptText,
                onPromptTextChange = { promptText = it },
                isRecording = isRecording,
                onRecordToggle = { viewModel.recordVoice(context) },
                onSend = {
                    viewModel.generatePodcast(promptText)
                    promptText = ""
                },
                surfaceColor = surfaceColor,
                primaryElectricViolet = primaryElectricViolet,
                secondaryNeonCyan = secondaryNeonCyan,
                onSurfaceColor = onSurfaceColor
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            reverseLayout = true
        ) {
            val messages = viewModel.messages
            items(messages) { msg ->
                if (msg.isUser) {
                    UserMessageBubble(msg.text)
                } else {
                    AIMessageBubble(
                        msg = msg,
                        viewModel = viewModel,
                        primaryColor = primaryElectricViolet,
                        secondaryColor = secondaryNeonCyan
                    )
                }
            }
        }
    }
}
}
