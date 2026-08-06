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
fun TriviaResultsOrganism(
    wasCorrect: Boolean,
    actualSong: String,
    onNextRound: () -> Unit,
    onEndGame: () -> Unit,
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
            text = if (wasCorrect) "Correct!" else "Incorrect!",
            color = if (wasCorrect) Color.Green else Color.Red,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "The song was:",
            color = Color.LightGray,
            fontSize = 18.sp
        )
        Text(
            text = actualSong,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MaveButton(
                text = "Next Round",
                onClick = onNextRound,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MaveButton(
                text = "End Game",
                onClick = onEndGame,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
