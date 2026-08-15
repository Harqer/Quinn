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

package com.musically.studio.ui.jetcaster.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.musically.studio.shared.R
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode

/**
 * Organism component displaying a single podcast/track episode in lists.
 * Delegates header and footer layouts to atomic sub-components.
 */
@Composable
fun EpisodeListItem(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onClick: (EpisodeInfo) -> Unit,
    removeFromQueue: (EpisodeInfo) -> Unit = {},
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = false,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 40.dp),
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                onClick = { onClick(episode) },
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    // Top Header
                    EpisodeListItemHeader(
                        episode = episode,
                        podcast = podcast,
                        showPodcastImage = showPodcastImage,
                        showSummary = showSummary,
                        modifier = Modifier.padding(bottom = 8.dp),
                        imageModifier = imageModifier,
                    )

                    // Bottom Footer
                    EpisodeListItemFooter(
                        episode = episode,
                        podcast = podcast,
                        onQueueEpisode = onQueueEpisode,
                    )
                }
            }
        }
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                removeFromQueue(episode)
            }
            SwipeToDismissBoxValue.StartToEnd -> {}
            SwipeToDismissBoxValue.Settled -> {}
        }
    }
}
