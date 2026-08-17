/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for TriviaBiddingOrganism.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.network.JamSession
import com.musically.studio.ui.components.atoms.MaveButton

@Composable
fun TriviaBiddingOrganism(
    session: JamSession,
    currentBid: Int,
    isLocalUserTurn: Boolean,
    onBid: (Int) -> Unit,
    onPass: () -> Unit,
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
            text = "Bid Phase",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Current lowest bid: $currentBid notes",
            color = Color.LightGray,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (isLocalUserTurn) {
            Text(
                text = "Your Turn!",
                color = Color.Yellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MaveButton(
                    text = "I can name that tune in ${currentBid - 1} notes",
                    onClick = { onBid(currentBid - 1) },
                    enabled = currentBid > 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                MaveButton(
                    text = "Pass",
                    onClick = onPass,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text(
                text = "Waiting for other players to bid...",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}
