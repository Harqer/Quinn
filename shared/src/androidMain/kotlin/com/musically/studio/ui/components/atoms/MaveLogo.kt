package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.musically.studio.shared.R

@Composable
fun MaveLogo(
    modifier: Modifier = Modifier,
    size: Int = 180,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val logoDrawable = if (isDark) R.drawable.mave_brand_dark else R.drawable.mave_brand_light
    Image(
        painter = painterResource(id = logoDrawable),
        contentDescription = "Mave Logo",
        modifier = modifier.size(size.dp)
    )
}
