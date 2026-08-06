package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import com.musically.studio.ui.components.atoms.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputArea(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit,
    onVoiceRecord: () -> Unit,
    onGenerateCoverArt: () -> Unit,
    onGenerateVideo: () -> Unit
) {
    var showAttachmentMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.ime)
            .padding(8.dp)
    ) {
        AnimatedVisibility(
            visible = showAttachmentMenu,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentOption(icon = Icons.Default.PhotoLibrary, label = "Attach Image", onClick = {
                        onAttachImage()
                        showAttachmentMenu = false
                    })
                    AttachmentOption(icon = Icons.Default.Image, label = "Cover Art", onClick = {
                        onGenerateCoverArt()
                        showAttachmentMenu = false
                    })
                    AttachmentOption(icon = Icons.Default.Animation, label = "Animate", onClick = {
                        onGenerateVideo()
                        showAttachmentMenu = false
                    })
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            val infiniteTransition = rememberInfiniteTransition(label = "RainbowBorderTransition")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "RainbowRotation"
            )
            val rainbowColors = listOf(
                Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .drawBehind {
                        rotate(rotation) {
                            val maxDimension = maxOf(size.width, size.height) * 1.5f
                            drawRect(
                                brush = Brush.sweepGradient(
                                    colors = rainbowColors,
                                    center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                                ),
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    (size.width - maxDimension) / 2,
                                    (size.height - maxDimension) / 2
                                ),
                                size = androidx.compose.ui.geometry.Size(maxDimension, maxDimension)
                            )
                        }
                    }
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                    AnimatedContent(
                        targetState = showAttachmentMenu,
                        label = "AttachmentMenuIcon"
                    ) { isMenuShowing ->
                        Icon(
                            imageVector = if (isMenuShowing) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Attach Media",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                TextField(
                    value = inputValue,
                    onValueChange = onValueChange,
                    placeholder = { Text("Ask Mave anything...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                AnimatedContent(
                    targetState = inputValue.isNotBlank(),
                    label = "SendVoiceTransition"
                ) { hasText ->
                    if (hasText) {
                        SendButton(onClick = onSend)
                    } else {
                        VoiceInputButton(onClick = onVoiceRecord)
                    }
                }
                }
            }
        }
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
