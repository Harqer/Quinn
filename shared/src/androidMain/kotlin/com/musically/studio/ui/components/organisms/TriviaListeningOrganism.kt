package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.components.atoms.MaveButton

@Composable
fun TriviaListeningOrganism(
    notesToPlay: Int,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Listen Closely!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Playing $notesToPlay notes...",
            color = Color.LightGray,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (isPlaying) {
            // A simple animated equalizer or just text
            Text(
                text = "Playing Audio...",
                color = Color.Green,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            MaveButton(
                text = "Play Audio",
                onClick = onPlayAudio,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}
