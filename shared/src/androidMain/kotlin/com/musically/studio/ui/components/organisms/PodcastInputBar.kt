package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastInputBar(
    promptText: String,
    onPromptTextChange: (String) -> Unit,
    isRecording: Boolean,
    onRecordToggle: () -> Unit,
    onSend: () -> Unit,
    surfaceColor: Color,
    primaryElectricViolet: Color,
    secondaryNeonCyan: Color,
    onSurfaceColor: Color
) {
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(surfaceColor.copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = promptText,
                onValueChange = onPromptTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Describe the podcast you want to create...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = onSurfaceColor,
                    unfocusedTextColor = onSurfaceColor
                )
            )
            
            IconButton(
                onClick = {
                    if (isRecording) {
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                    onRecordToggle()
                },
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(listOf(primaryElectricViolet, secondaryNeonCyan)),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color.White
                )
            }
            
            if (promptText.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onSend()
                    },
                    modifier = Modifier
                        .background(surfaceColor, RoundedCornerShape(50))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = secondaryNeonCyan)
                }
            }
        }
    }
}
