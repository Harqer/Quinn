package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.musically.studio.ui.MainViewModel

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

    val primaryGreen = Color(0xFF1DB954)
    val darkBackground = Color(0xFF121414)
    val containerColor = Color(0xFF1E2020)

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
                .imePadding()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingValues.calculateBottomPadding() + 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Mode Switcher Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(containerColor)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (coverType == "image") primaryGreen else Color.Transparent)
                        .clickable { coverType = "image" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = if (coverType == "image") Color.Black else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Image Cover",
                            color = if (coverType == "image") Color.Black else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (coverType == "video") primaryGreen else Color.Transparent)
                        .clickable { coverType = "video" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = if (coverType == "video") Color.Black else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Video Cover",
                            color = if (coverType == "video") Color.Black else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Preview Canvas Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A1C1C))
                    .border(1.dp, primaryGreen.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (generatedCoverUrl != null) {
                    AsyncImage(
                        model = generatedCoverUrl,
                        contentDescription = "Generated Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = if (coverType == "image") Icons.Default.AutoAwesome else Icons.Default.MovieFilter,
                            contentDescription = null,
                            tint = primaryGreen.copy(alpha = 0.7f),
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Describe a mood to generate your custom AI ${coverType} cover",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = primaryGreen)
                            Text(
                                text = "Generating AI ${if (coverType == "image") "Visual Artwork" else "Motion Video"}...",
                                color = primaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Style Preset Pills
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "STYLE PRESETS",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presets) { preset ->
                        val isSelected = selectedPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) primaryGreen else Color(0xFF282A2B))
                                .clickable { selectedPreset = preset }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Custom Prompt Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CUSTOM PROMPT (OPTIONAL)",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = customPrompt,
                    onValueChange = { customPrompt = it },
                    placeholder = {
                        Text(
                            text = "Describe visual style (e.g. glowing neon grid)...",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        focusedBorderColor = primaryGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // CTA Button
            Button(
                onClick = {
                    isGenerating = true
                    viewModel.sendTextCommand("Generate $coverType cover: ${customPrompt.ifEmpty { selectedPreset }}")
                    
                    val apiType = if (coverType == "image") "cover_art" else "video_motion"
                    viewModel.generateCoverMedia(customPrompt.ifEmpty { selectedPreset }, apiType) { resultUrl ->
                        isGenerating = false
                        if (resultUrl != null) {
                            generatedCoverUrl = resultUrl
                            onCoverGenerated(resultUrl)
                        }
                    }
                },
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Text(
                        text = "Generate ${if (coverType == "image") "Image" else "Video"} Cover",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

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
