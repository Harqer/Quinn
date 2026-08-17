/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for TrackItems.kt
 */

package com.musically.studio.ui.components
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.shared.R
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.ChatMessage

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.rememberUpdatedStyleState
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun TrackItem(
    track: MaveTrack, 
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAlbumClick: () -> Unit = {},
    onRemixClick: (() -> Unit)? = null,
    style: Style = Style
) {
    val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
            .styleable(styleState, MaveStyles.listRowItemStyle, style)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .debouncedClickable { onAlbumClick() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artists.joinToString { it.name }.ifEmpty { "Independent Creator" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (onRemixClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onRemixClick,
                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                        contentDescription = "Remix with AI",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onLike: () -> Unit = {},
    onBookmark: () -> Unit = {},
    style: Style = Style
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val contentColor = if (message.isUser) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
    val defaultStyle = if (message.isUser) MaveStyles.userMessageBubbleStyle else MaveStyles.aiMessageBubbleStyle

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
        val styleState = rememberUpdatedStyleState(interactionSource)
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .styleable(styleState, defaultStyle, style)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                
                if (!message.isUser && message.trackId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = contentColor.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onLike) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = contentColor, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onBookmark) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = contentColor, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
