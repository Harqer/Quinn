package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun ModeSwitcherToggle(
    coverType: String,
    onCoverTypeChange: (String) -> Unit,
    primaryGreen: Color,
    containerColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(containerColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(if (coverType == "image") primaryGreen else Color.Transparent)
                .debouncedClickable { onCoverTypeChange("image") }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = if (coverType == "image") Color.Black else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Image Cover",
                    color = if (coverType == "image") Color.Black else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(if (coverType == "video") primaryGreen else Color.Transparent)
                .debouncedClickable { onCoverTypeChange("video") }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = if (coverType == "video") Color.Black else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Video Cover",
                    color = if (coverType == "video") Color.Black else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
