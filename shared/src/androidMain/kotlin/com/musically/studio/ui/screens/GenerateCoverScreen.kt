package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.atoms.GenerateCoverButton
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import com.musically.studio.ui.components.molecules.CustomPromptInput
import com.musically.studio.ui.components.molecules.ModeSwitcherToggle
import com.musically.studio.ui.components.molecules.StylePresetPills
import com.musically.studio.ui.components.organisms.PreviewCanvasCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateCoverScreen(
    viewModel: MainViewModel,
    trackId: String?,
    initialType: String = "image",
    onBack: () -> Unit,
    onCoverGenerated: (String) -> Unit = {}
) {
    var coverType by remember { mutableStateOf(if (initialType == "video") "video" else "image") }
    var customPrompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedCoverUrl by remember { mutableStateOf<String?>(null) }

    val dynamicPresets by viewModel.generatedPrompts.collectAsState()
    
    LaunchedEffect(Unit) {
        if (dynamicPresets.isEmpty()) {
            viewModel.generateMusicPrompts("")
        }
    }

    val presets = if (dynamicPresets.isNotEmpty()) dynamicPresets else listOf("Vibrant Synthwave", "Minimalist Neon", "Abstract Cyberpunk", "Retro Vinyl")
    var selectedPreset by remember(coverType, presets) { mutableStateOf(presets.firstOrNull() ?: "") }
    
    val view = LocalView.current

    val primaryGreen = com.musically.studio.ui.theme.MaveBrand
    val darkBackground = com.musically.studio.ui.theme.MaveBackgroundVariant
    val containerColor = com.musically.studio.ui.theme.MaveDarkSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Generate ${if (coverType == "image") "Image" else "Video"} Cover",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        containerColor = darkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ModeSwitcherToggle(
                coverType = coverType,
                onCoverTypeChange = { coverType = it },
                primaryGreen = primaryGreen,
                containerColor = containerColor
            )

            PreviewCanvasCard(
                coverType = coverType,
                generatedCoverUrl = generatedCoverUrl,
                isGenerating = isGenerating,
                primaryGreen = primaryGreen
            )

            StylePresetPills(
                presets = presets,
                selectedPreset = selectedPreset,
                onPresetSelected = { selectedPreset = it },
                primaryGreen = primaryGreen
            )

            CustomPromptInput(
                customPrompt = customPrompt,
                onCustomPromptChange = { customPrompt = it },
                containerColor = containerColor,
                primaryGreen = primaryGreen
            )

            GenerateCoverButton(
                onClick = {
                    val finalPrompt = customPrompt.ifEmpty { selectedPreset }
                    if (finalPrompt.isBlank()) {
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                        return@GenerateCoverButton
                    }
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    isGenerating = true
                    viewModel.sendTextCommand("Generate $coverType cover: $finalPrompt")
                    val apiType = if (coverType == "image") "cover_art" else "video_motion"
                    viewModel.generateCoverMedia(trackId, finalPrompt, apiType) { resultUrl ->
                        isGenerating = false
                        if (resultUrl != null) {
                            generatedCoverUrl = resultUrl
                            onCoverGenerated(resultUrl)
                        } else {
                            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                        }
                    }
                },
                isGenerating = isGenerating,
                coverType = coverType,
                primaryGreen = primaryGreen
            )

            TextButton(
                onClick = { coverType = if (coverType == "image") "video" else "image" }
            ) {
                Text(
                    text = "Switch to ${if (coverType == "image") "Video" else "Image"} Cover generation",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
