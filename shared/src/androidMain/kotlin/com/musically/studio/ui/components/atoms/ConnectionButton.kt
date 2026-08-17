/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for ConnectionButton.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun ConnectionButton(
    platform: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isConnected) {
        if (platform == "Spotify") MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    }
    val textColor = if (isConnected) {
        if (platform == "Spotify") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(bgColor)
            .debouncedClickable { if (!isConnected) onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (platform == "Spotify") {
                Icon(
                    painter = painterResource(id = com.musically.studio.shared.R.drawable.ic_spotify),
                    contentDescription = "Spotify",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = painterResource(id = com.musically.studio.shared.R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isConnected) "$platform Connected" else "Connect $platform",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (!isConnected) {
            Icon(
                imageVector = OpenInNewIcon,
                contentDescription = "Open in new window",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

val OpenInNewIcon: ImageVector
    get() = ImageVector.Builder(
        name = "OpenInNew",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color.White)
        ) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(80f)
            horizontalLineTo(200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-280f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(280f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            horizontalLineTo(200f)
            close()
            moveToRelative(188f, -212f)
            lineToRelative(-56f, -56f)
            lineToRelative(372f, -372f)
            horizontalLineTo(560f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(280f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-144f)
            lineTo(388f, 628f)
            close()
        }
    }.build()
