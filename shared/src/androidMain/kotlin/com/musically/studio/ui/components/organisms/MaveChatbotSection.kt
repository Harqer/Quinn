/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for MaveChatbotSection.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.molecules.ChatBubble
import com.musically.studio.ui.components.organisms.ChatInputArea
import com.musically.studio.ui.theme.MaveBackground
import com.musically.studio.ui.MainViewModel

@Composable
fun MaveChatbotSection(
    viewModel: MainViewModel,
    chatInputValue: String,
    onChatInputValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit,
    onVoiceRecord: () -> Unit,
    onGenerateCoverArt: () -> Unit,
    onGenerateVideo: () -> Unit,
    onGeneratePodcast: () -> Unit,
    onGenerateAudiobook: () -> Unit
) {
    val messages = viewModel.messages
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    
    Column {
        if (messages.isNotEmpty()) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .background(MaveBackground.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.hashCode() }) { message ->
                    ChatBubble(message = message, viewModel = viewModel)
                }
            }
        }
        ChatInputArea(
            inputValue = chatInputValue,
            onValueChange = onChatInputValueChange,
            onSend = onSend,
            onAttachImage = onAttachImage,
            onVoiceRecord = onVoiceRecord,
            onGenerateCoverArt = onGenerateCoverArt,
            onGenerateVideo = onGenerateVideo,
            onGeneratePodcast = onGeneratePodcast,
            onGenerateAudiobook = onGenerateAudiobook
        )
    }
}
