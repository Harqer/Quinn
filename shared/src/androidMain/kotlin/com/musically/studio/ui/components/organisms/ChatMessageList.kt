package com.musically.studio.ui.components.organisms

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.screens.MaveChatMessage
import com.musically.studio.ui.components.molecules.AiMessageBubble
import com.musically.studio.ui.components.molecules.ChatMultiTrackList
import com.musically.studio.ui.components.molecules.ChatSingleTrackCard
import com.musically.studio.ui.components.molecules.UserMessageBubble

@Composable
fun ChatMessageList(
    messages: List<MaveChatMessage>,
    paddingValues: PaddingValues,
    context: Context,
    clipboardManager: ClipboardManager,
    onTrackClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        if (messages.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your personal audio curator. Ready to discover?", fontSize = 14.sp)
                }
            }
        }
        
        items(messages) { msg ->
            if (msg.sender == "ai") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    AiMessageBubble(
                        text = msg.text,
                        onCopy = {
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", msg.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        if (msg.tracks?.size == 1) {
                            ChatSingleTrackCard(
                                track = msg.tracks[0],
                                coverArtUrl = msg.coverArtUrl,
                                onClick = onTrackClick
                            )
                        } else if ((msg.tracks?.size ?: 0) > 1) {
                            msg.tracks?.let { ChatMultiTrackList(tracks = it) }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    UserMessageBubble(
                        text = msg.text,
                        onCopy = {
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", msg.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
