package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSegmentedNavRow(
    onNavigateToMusic: () -> Unit,
    onNavigateToPodcast: () -> Unit,
    onNavigateToAudiobooks: () -> Unit,
    onNavigateToTrivia: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Music", "Podcasts", "Audiobooks", "Trivia")
    var selectedIndex by remember { mutableIntStateOf(0) }
    
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    when (index) {
                        0 -> onNavigateToMusic()
                        1 -> onNavigateToPodcast()
                        2 -> onNavigateToAudiobooks()
                        3 -> onNavigateToTrivia()
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
