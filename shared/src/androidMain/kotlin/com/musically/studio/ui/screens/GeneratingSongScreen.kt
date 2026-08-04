package com.musically.studio.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.generateMusicPrompts
import kotlinx.coroutines.delay

@Composable
fun GeneratingSongScreen(
    imageBase64: String,
    viewModel: MainViewModel,
    onComplete: () -> Unit
) {
    val thinkingText by viewModel.thinkingText.collectAsState()

    LaunchedEffect(Unit) {
        // Send the image and prompt Gemini to generate a song based on it
        viewModel.sendFrame(imageBase64)
        viewModel.sendTextCommand("Look at the picture I just sent and give me a chain of thought describing the visual vibe, the aesthetic, and the musical mood it inspires. Think out loud! Then use the generate_cover_media tool to generate the cover and apply the music preset.")
        
        // Wait a bit more to simulate completion before navigating
        delay(15000) // Gemini should ideally call a function and trigger navigation, but for now we timeout
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.musically.studio.ui.theme.MaveBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = com.musically.studio.ui.theme.MaveBrand,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedContent(
                targetState = thinkingText,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(500)) + slideInVertically { height -> height })
                        .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutVertically { height -> -height })
                },
                label = "ChainOfThoughtStep"
            ) { text ->
                Text(
                    text = text.ifBlank { "Analyzing visual vibe..." },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
