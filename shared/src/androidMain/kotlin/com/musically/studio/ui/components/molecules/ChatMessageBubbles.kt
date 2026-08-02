package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.selection.SelectionContainer
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
                SelectionContainer {
                    Text(text, fontSize = 16.sp)
                }
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
    Column(
        modifier = modifier
            .styleable(style = MaveStyles.aiMessageBubbleStyle)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SelectionContainer(modifier = Modifier.weight(1f, fill = false)) {
                Text(text, fontSize = 16.sp)
            }
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
            }
        }
        content()
    }
}
