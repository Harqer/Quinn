package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun GlassmorphicControlsRow(
    isPlaying: Boolean,
    progressSec: Long,
    remainingSec: Long,
    progressRatio: Float,
    onPlayPauseClick: () -> Unit,
    onUndoClick: () -> Unit,
    onEqClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    fun formatTime(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .debouncedClickable { onPlayPauseClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), CircleShape)
                .debouncedClickable { onUndoClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = "Replay",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = formatTime(progressSec),
            color = Color.White,
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressRatio)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "-${formatTime(remainingSec)}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), CircleShape)
                .semantics(mergeDescendants = true) {}
                .clickable { onEqClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "EQ",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
