/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for EpisodeListItemFooter.kt
 */

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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.musically.studio.shared.R
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun EpisodeListItemFooter(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(
            painterResource(id = R.drawable.ic_play_circle),
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp),
                ) {
                    onQueueEpisode(
                        PlayerEpisode(
                            podcastInfo = podcast,
                            episodeInfo = episode,
                        )
                    )
                }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button },
        )

        val duration = episode.duration
        Text(
            text = when {
                duration != null -> {
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(episode.published),
                        duration.toMinutes().toInt(),
                    )
                }
                else -> MediumDateFormatter.format(episode.published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
        )

        IconButton(
            onClick = {
                onQueueEpisode(
                    PlayerEpisode(
                        podcastInfo = podcast,
                        episodeInfo = episode,
                    ),
                )
            },
        ) {
            Icon(
                painterResource(id = R.drawable.ic_playlist_add),
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        var showOptionsSheet by remember { mutableStateOf(false) }

        IconButton(
            onClick = { showOptionsSheet = true },
        ) {
            Icon(
                painterResource(id = R.drawable.ic_more_vert),
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showOptionsSheet) {
            EpisodeOptionsBottomSheet(
                episode = episode,
                podcast = podcast,
                onQueueEpisode = onQueueEpisode,
                onDismiss = { showOptionsSheet = false },
            )
        }
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
