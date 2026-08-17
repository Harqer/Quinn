/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for TriviaGuessingOrganism.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
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
fun TriviaGuessingOrganism(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
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
            text = "Name That Tune!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Say the name of the song and the artist.",
            color = Color.LightGray,
            fontSize = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (isRecording) {
            Text(
                text = "Listening...",
                color = Color.Red,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            MaveButton(
                text = "Stop & Submit",
                onClick = onStopRecording,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        } else {
            MaveButton(
                text = "Hold to Speak",
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}
