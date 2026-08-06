package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun UserAvatarButton(
    photoUrl: String?,
    displayName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 36.dp
) {
    val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta, Color.Red)
    val rainbowBrush = Brush.sweepGradient(rainbowColors)

    Box(
        modifier = modifier
            .size(size)
            .border(2.dp, rainbowBrush, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
            .debouncedClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(com.musically.studio.ui.theme.MaveBrand),
                contentAlignment = Alignment.Center
            ) {
                val initial = displayName?.firstOrNull()?.toString()?.uppercase() ?: "M"
                Text(
                    text = initial,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
