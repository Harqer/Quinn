/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for LiveSessionBottomBar.kt
 */

package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

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
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.getValue
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*

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
    val micPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (!isLiveSessionActive) viewModel.startLiveSession()
            viewModel.recordVoice(context)
        }
    }

    val onMicClick: () -> Unit = {
        val hasMicPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasMicPermission) {
            if (!isLiveSessionActive) viewModel.startLiveSession()
            viewModel.recordVoice(context)
        } else {
            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    shape = MaterialTheme.shapes.extraLarge,
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
                    onClick = onMicClick,
                    modifier = Modifier.size(52.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
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
