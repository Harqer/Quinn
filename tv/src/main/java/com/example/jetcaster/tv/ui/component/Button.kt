/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.example.jetcaster.tv.R

@Composable
internal fun PlayButton(onClick: () -> Unit, modifier: Modifier = Modifier, scale: ButtonScale = ButtonDefaults.scale()) = ButtonWithIcon(
    iconId = 0,
    label = "Stub",
    onClick = onClick,
    modifier = modifier,
    scale = scale,
)

@Composable
internal fun EnqueueButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}

@Composable
internal fun InfoButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}

@Composable
internal fun PreviousButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}

@Composable
internal fun NextButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}

@Composable
internal fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (icon, description) = if (isPlaying) {
        androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray) to "Stub"
    } else {
        androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray) to "Stub"
    }
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(icon, description, modifier = Modifier.size(48.dp))
    }
}

@Composable
internal fun RewindButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}

@Composable
internal fun SkipButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
            contentDescription = "Stub",
        )
    }
}
