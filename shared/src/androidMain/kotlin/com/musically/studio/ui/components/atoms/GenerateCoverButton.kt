package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GenerateCoverButton(
    onClick: () -> Unit,
    isGenerating: Boolean,
    coverType: String,
    primaryGreen: Color
) {
    Button(
        onClick = onClick,
        enabled = !isGenerating,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryGreen,
            contentColor = Color.Black
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
            Text(
                text = "Generate ${if (coverType == "image") "Image" else "Video"} Cover",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
