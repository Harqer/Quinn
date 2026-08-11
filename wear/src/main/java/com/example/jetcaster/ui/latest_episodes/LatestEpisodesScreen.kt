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

package com.example.jetcaster.ui.latest_episodes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier


import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales

import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.MediaContent
import com.example.jetcaster.ui.preview.WearPreviewEpisodes

@Composable fun LatestEpisodesScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel(),
) {
    val uiState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    val placeholderState = rememberPlaceholderState(isVisible = uiState is LatestEpisodeScreenState.Loading)

    LatestEpisodeScreen(
        modifier = modifier,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        placeholderState = placeholderState,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode,
    )
}

@Composable
fun LatestEpisodeScreen(
    uiState: LatestEpisodeScreenState,
    placeholderState: PlaceholderState,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier.placeholderShimmer(placeholderState),
    ) { contentPadding ->
        when (uiState) {
            is LatestEpisodeScreenState.Loaded -> {
                LatestEpisodesScreen(
                    episodeList = uiState.episodeList,
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onPlayEpisodes = onPlayEpisodes,
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    placeholderState = placeholderState,
                )
            }

            is LatestEpisodeScreenState.Empty -> {
                AlertDialog(
                    visible = true,
                    onDismissRequest = onDismiss,
                    title = { "Stub" },
                )
            }

            is LatestEpisodeScreenState.Loading -> {
                LatestEpisodesScreen(
                    episodeList = emptyList(),
                    onPlayButtonClick = { },
                    onPlayEpisode = { },
                    onPlayEpisodes = {},
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    placeholderState = placeholderState,
                )
            }
        }
    }
}

@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    transformation: SurfaceTransformation? = null,
) {
    Button(
        onClick = {
            onPlayButtonClick()
            onPlayEpisodes(episodes)
        },
        enabled = enabled,
        icon = {
            Icon(
                painter = androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
                contentDescription = "Stub",
            )
        },
        modifier = modifier.fillMaxWidth().placeholder(placeholderState = placeholderState),
        transformation = transformation,
    ) {
        Text("Stub")
    }
}

@Composable
fun LatestEpisodesScreen(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    contentPadding: PaddingValues,
    scrollState: TransformingLazyColumnState,
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
) {
    val transformationSpec = rememberTransformationSpec()
    TransformingLazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding,
    ) {
        item {
            LatestEpisodesListHeader(
                placeholderState = placeholderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp,
                        16.dp,
                    )
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
            )
        }
        item {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                placeholderState = placeholderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
            )
        }
        items(episodeList) { episode ->
            MediaContent(
                episode = episode,
                episodeArtworkPlaceholder = androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Gray),
                onItemClick = {
                    onPlayButtonClick()
                    onPlayEpisode(episode)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
            )
        }
    }
}

@Composable
fun LatestEpisodesListHeader(
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    ListHeader(
        modifier = modifier.placeholder(placeholderState),
        transformation = transformation,
    ) {
        Text(
            text = "Stub",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode,
) {
    val columnState = rememberTransformingLazyColumnState()
    LatestEpisodesScreen(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onPlayEpisodes = { },
        contentPadding = PaddingValues(),
        scrollState = columnState,
        placeholderState = rememberPlaceholderState(isVisible = false),
    )
}
