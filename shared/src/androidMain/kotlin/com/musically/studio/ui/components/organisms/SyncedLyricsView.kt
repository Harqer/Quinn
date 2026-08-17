/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for SyncedLyricsView.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveLyricLine
import kotlinx.coroutines.launch

@Composable
fun SyncedLyricsView(
    lyrics: List<MaveLyricLine>,
    currentProgressMs: Long,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find the currently active lyric index
    val activeIndex = remember(currentProgressMs, lyrics) {
        lyrics.indexOfLast { line ->
            currentProgressMs >= line.startMs
        }.takeIf { it >= 0 } ?: 0
    }

    // Scroll to active index
    LaunchedEffect(activeIndex) {
        if (lyrics.isNotEmpty() && activeIndex >= 0 && activeIndex < lyrics.size) {
            coroutineScope.launch {
                // Scroll the active item to the center approximately (offset by a few items)
                listState.animateScrollToItem(maxOf(0, activeIndex - 2))
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (lyrics.isEmpty()) {
            item {
                Text(
                    text = "Lyrics not available.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            itemsIndexed(lyrics) { index, line ->
                val isActive = index == activeIndex
                val isPast = index < activeIndex
                
                val targetColor = when {
                    isActive -> Color.White
                    isPast -> Color.White.copy(alpha = 0.6f)
                    else -> Color.Black.copy(alpha = 0.4f)
                }

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 300),
                    label = "lyric_color"
                )

                Text(
                    text = line.text,
                    style = MaterialTheme.typography.titleLarge,
                    color = animatedColor,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
