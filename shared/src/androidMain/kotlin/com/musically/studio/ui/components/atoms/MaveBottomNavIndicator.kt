package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)

@Composable
fun MaveBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    shape: Shape = BottomNavIndicatorShape,
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .background(color = color, shape = shape)
    )
}
