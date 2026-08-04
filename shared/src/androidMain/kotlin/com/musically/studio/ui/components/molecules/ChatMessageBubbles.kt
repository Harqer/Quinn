package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.styleable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.musically.studio.shared.R
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun UserMessageBubble(
    text: String,
    onCopy: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        Box(
            modifier = Modifier
                .styleable(style = MaveStyles.userMessageBubbleStyle)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                }
                Text(text, fontSize = 16.sp)
            }
        }
        Text("DELIVERED", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
    }
}

@Composable
fun AiMessageBubble(
    text: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta, Color.Red)
        val rainbowBrush = Brush.sweepGradient(rainbowColors)

        Box(
            modifier = Modifier
                .padding(end = 8.dp, top = 4.dp)
                .size(36.dp)
                .border(2.dp, rainbowBrush, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mave_brand_dark),
                contentDescription = "Mave AI",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        Column(
            modifier = Modifier
                .styleable(style = MaveStyles.aiMessageBubbleStyle)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text, fontSize = 16.sp, modifier = Modifier.weight(1f, fill = false))
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                }
            }
            content()
        }
    }
}
