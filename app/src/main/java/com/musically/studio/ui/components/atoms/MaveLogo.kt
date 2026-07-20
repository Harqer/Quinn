package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.musically.studio.R

@Composable
fun MaveLogo(
    modifier: Modifier = Modifier,
    size: Int = 180
) {
    Image(
        painter = painterResource(id = R.drawable.mave_brand),
        contentDescription = "Mave Logo",
        modifier = modifier.size(size.dp)
    )
}
