package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.graphics.Color
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
        viewModel.geminiLiveManager.sendVideoFrame(imageBase64)
        viewModel.geminiLiveManager.sendText("Look at the picture I just sent and give me a chain of thought describing the visual vibe, the aesthetic, and the musical mood it inspires. Think out loud! Then use the generate_cover_art tool to generate the cover and apply the music preset.")
        
        // Wait a bit more to simulate completion before navigating
        delay(15000) // Gemini should ideally call a function and trigger navigation, but for now we timeout
        onComplete()
    }

    val decodedBitmap = remember(imageBase64) {
        try {
            val bytes = android.util.Base64.decode(imageBase64, android.util.Base64.NO_WRAP)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.musically.studio.ui.theme.MaveBackground),
        contentAlignment = Alignment.Center
    ) {
        if (decodedBitmap != null) {
            androidx.compose.ui.viewinterop.AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val root = android.widget.RelativeLayout(ctx).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val contentContainer = android.widget.FrameLayout(ctx).apply {
                        id = android.view.View.generateViewId()
                        layoutParams = android.widget.RelativeLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val imageView = android.widget.ImageView(ctx).apply {
                        layoutParams = android.widget.FrameLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    }
                    contentContainer.addView(imageView)
                    root.addView(contentContainer)
                    
                    val liquidGlassView = com.qmdeve.liquidglass.widget.LiquidGlassView(ctx).apply {
                        layoutParams = android.widget.RelativeLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    root.addView(liquidGlassView)
                    
                    liquidGlassView.bind(contentContainer)
                    
                    imageView.setImageBitmap(decodedBitmap)
                    
                    root
                }
            )
            // Add a semi-transparent overlay to ensure text is readable
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)))
        }

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
                    color = Color.White
                )
            }
        }
    }
}
