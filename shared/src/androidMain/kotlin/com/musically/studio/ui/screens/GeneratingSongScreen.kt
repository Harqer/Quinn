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
    var stepIndex by remember { mutableIntStateOf(0) }
    
    val steps = listOf(
        "Analyzing visual vibe...",
        "Identifying subjects and mood...",
        "Extracting aesthetic keywords...",
        "Composing contextual lyrics...",
        "Generating instrumental track..."
    )

    LaunchedEffect(Unit) {
        // Mock chain of thought progression
        for (i in steps.indices) {
            stepIndex = i
            delay(1500)
        }
        
        // Trigger the actual generation via viewModel
        viewModel.generateMusicPrompts(imageBase64)
        
        // Wait a bit more to simulate completion before navigating
        delay(1000)
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
                targetState = stepIndex,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(500)) + slideInVertically { height -> height })
                        .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutVertically { height -> -height })
                },
                label = "ChainOfThoughtStep"
            ) { index ->
                Text(
                    text = steps[index],
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
