package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.theme.LocalMaveColorScheme
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.components.atoms.animated_images

@Composable
fun AttachImageButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }
}

@Composable
fun CameraCaptureButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = LocalMaveColorScheme.current.onSurface)
    }
}

@Composable
fun SendButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    
    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.styleable(styleState = styleState, style = MaveStyles.sendButtonStyle)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = "Send", tint = Color.Black
        )
    }
}

@Composable
fun VoiceInputButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = LocalMaveColorScheme.current.onSurface)
    }
}

@Composable
fun VoiceRecordButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Mic, contentDescription = "Mic")
    }
}

@Composable
fun GenerateCoverArtButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Palette, contentDescription = "Generate Cover Art", modifier = Modifier.size(20.dp))
    }
}

@Composable
fun GenerateVideoButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(animated_images, contentDescription = "Generate Video", modifier = Modifier.size(20.dp))
    }
}
