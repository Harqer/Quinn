package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.*
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun ChatInputArea(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit,
    onCameraCapture: () -> Unit,
    onVoiceRecord: () -> Unit,
    onGenerateCoverArt: () -> Unit,
    onGenerateVideo: () -> Unit
) {
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
            AttachImageButton(onClick = onAttachImage)
            CameraCaptureButton(onClick = onCameraCapture)
            
            TextField(
                value = inputValue,
                onValueChange = onValueChange,
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
                SendButton(onClick = onSend)
            } else {
                VoiceRecordButton(onClick = onVoiceRecord)
                GenerateCoverArtButton(onClick = onGenerateCoverArt)
                GenerateVideoButton(onClick = onGenerateVideo)
            }
        }
    }
}
