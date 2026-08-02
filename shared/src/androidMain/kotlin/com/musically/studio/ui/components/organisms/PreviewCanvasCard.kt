package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PreviewCanvasCard(
    coverType: String,
    generatedCoverUrl: String?,
    isGenerating: Boolean,
    primaryGreen: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(com.musically.studio.ui.theme.MaveBackgroundVariant3)
            .border(1.dp, primaryGreen.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (generatedCoverUrl != null) {
            AsyncImage(
                model = generatedCoverUrl,
                contentDescription = "Generated Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = if (coverType == "image") Icons.Default.AutoAwesome else Icons.Default.MovieFilter,
                    contentDescription = null,
                    tint = primaryGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = "Describe a mood to generate your custom AI ${coverType} cover",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (isGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = primaryGreen)
                    Text(
                        text = "Generating AI ${if (coverType == "image") "Visual Artwork" else "Motion Video"}...",
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
