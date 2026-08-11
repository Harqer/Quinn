/*
 * Copyright 2020-2025 The Android Open Source Project
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

package com.musically.studio.ui.jetcaster.ui.home

import androidx.activity.compose.BackHandler
import com.musically.studio.shared.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.musically.studio.ui.jetcaster.core.model.EpisodeInfo
import com.musically.studio.ui.jetcaster.core.model.FilterableCategoriesModel
import com.musically.studio.ui.jetcaster.core.model.LibraryInfo
import com.musically.studio.ui.jetcaster.core.model.PodcastCategoryFilterResult
import com.musically.studio.ui.jetcaster.core.model.PodcastInfo
import com.musically.studio.ui.jetcaster.core.designsystem.component.PodcastImage
import com.musically.studio.ui.jetcaster.ui.home.discover.discoverItems
import com.musically.studio.ui.jetcaster.ui.home.library.libraryItems
import com.musically.studio.ui.jetcaster.ui.podcast.PodcastDetailsScreen
import com.musically.studio.ui.jetcaster.ui.podcast.PodcastDetailsViewModel
import com.musically.studio.ui.jetcaster.ui.theme.JetcasterTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import com.musically.studio.ui.jetcaster.util.ToggleFollowPodcastIconButton
import com.musically.studio.ui.jetcaster.util.fullWidthItem
import com.musically.studio.ui.jetcaster.util.isCompact
import com.musically.studio.ui.jetcaster.util.quantityStringResource
import com.musically.studio.ui.jetcaster.util.radialGradientScrim
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isMainPaneHidden(): Boolean =
    scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden

/**
 * Copied from `calculatePaneScaffoldDirective()` in [PaneScaffoldDirective], with modifications to
 * only show 1 pane horizontally if either width or height size class is compact.
 */
fun calculateScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating,
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val verticalSpacerSize: Dp
    if (windowAdaptiveInfo.windowSizeClass.isCompact) {
        // Window width or height is compact. Limit to 1 pane horizontally.
        maxHorizontalPartitions = 1
        verticalSpacerSize = 0.dp
    } else {
        if (windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            maxHorizontalPartitions = 2
            verticalSpacerSize = 24.dp
        } else {
            maxHorizontalPartitions = 1
            verticalSpacerSize = 0.dp
        }
    }
    val maxVerticalPartitions: Int
    val horizontalSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        horizontalSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        horizontalSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        verticalSpacerSize,
        maxVerticalPartitions,
        horizontalSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(windowAdaptiveInfo.windowPosture, verticalHingePolicy),
    )
}

/**
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 */
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> = when (hingePolicy) {
    HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
    HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
    HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
    else -> emptyList()
}

@Composable
fun MainScreen(windowSizeClass: WindowSizeClass, navigateToPlayer: (EpisodeInfo) -> Unit, viewModel: HomeViewModel) {
    val homeScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState = homeScreenUiState
    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToPlayer = navigateToPlayer,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
    }
}

@Composable
private fun HomeScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.an_error_has_occurred),
                modifier = Modifier.padding(16.dp),
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun HomeScreenReady(
    uiState: HomeScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculateScaffoldDirective(currentWindowAdaptiveInfo()),
    )
    val scope = rememberCoroutineScope()

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    Surface {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                HomeScreen(
                    isHomeAppBarExpanded = windowSizeClass.isCompact,
                    isLoading = uiState.isLoading,
                    featuredPodcasts = uiState.featuredPodcasts,
                    homeCategories = uiState.homeCategories,
                    selectedHomeCategory = uiState.selectedHomeCategory,
                    filterableCategoriesModel = uiState.filterableCategoriesModel,
                    podcastCategoryFilterResult = uiState.podcastCategoryFilterResult,
                    library = uiState.library,
                    onHomeAction = viewModel::onHomeAction,
                    navigateToPodcastDetails = {
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.uri)
                        }
                    },
                    navigateToPlayer = navigateToPlayer,
                    modifier = Modifier.fillMaxSize(),
                    onSearch = { viewModel.searchPodcasts(it) }
                )
            },
            supportingPane = {
                val podcastUri = navigator.currentDestination?.contentKey
                if (!podcastUri.isNullOrEmpty()) {
                    val podcastDetailsViewModel: PodcastDetailsViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel()
                    
                    LaunchedEffect(podcastUri) {
                        podcastDetailsViewModel.initialize(podcastUri)
                    }
                    
                    PodcastDetailsScreen(
                        viewModel = podcastDetailsViewModel,
                        navigateToPlayer = navigateToPlayer,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                scope.launch {
                                    navigator.navigateBack()
                                }
                            }
                        },
                        showBackButton = navigator.isMainPaneHidden(),
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(isExpanded: Boolean, onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    var queryText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryText,
                    onQueryChange = { queryText = it },
                    onSearch = { 
                        expanded = false
                        onSearch(it)
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    enabled = true,
                    placeholder = {
                        Text(stringResource(id = R.string.search_for_a_podcast))
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_account_circle),
                            contentDescription = stringResource(R.string.cd_account),
                        )
                    },
                    interactionSource = null,
                    modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier,
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {}
    }
}

@Composable
private fun HomeScreenBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        )
        content()
    }
}

@Composable
private fun HomeScreen(
    isHomeAppBarExpanded: Boolean,
    isLoading: Boolean,
    featuredPodcasts: List<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    // Effect that changes the home category selection when there are no subscribed podcasts
    LaunchedEffect(key1 = featuredPodcasts) {
        if (featuredPodcasts.isEmpty()) {
            onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Discover))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Scaffold(
            topBar = {
                Column {
                    HomeAppBar(
                        isExpanded = isHomeAppBarExpanded,
                        onSearch = onSearch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent,
        ) { contentPadding ->
            // Main Content
            val snackBarText = stringResource(id = R.string.episode_added_to_your_queue)
            val showHomeCategoryTabs = featuredPodcasts.isNotEmpty() && homeCategories.isNotEmpty()
            HomeContent(
                featuredPodcasts = featuredPodcasts,
                selectedHomeCategory = selectedHomeCategory,
                filterableCategoriesModel = filterableCategoriesModel,
                podcastCategoryFilterResult = podcastCategoryFilterResult,
                library = library,
                modifier = Modifier.padding(contentPadding),
                onHomeAction = { action ->
                    if (action is HomeAction.QueueEpisode) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
                        }
                    }
                    onHomeAction(action)
                },
                navigateToPodcastDetails = navigateToPodcastDetails,
                navigateToPlayer = navigateToPlayer,
            )

            if (showHomeCategoryTabs) {
                PillToolbar(
                    selectedHomeCategory,
                    onHomeAction,
                    Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
fun PillToolbar(selectedHomeCategory: HomeCategory, onHomeAction: (HomeAction) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
            val libraryContainerColor =
                if (selectedHomeCategory.name == HomeCategory.Library.name) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                }

            val libraryContentColor =
                if (selectedHomeCategory.name == HomeCategory.Library.name) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

            Button(
                onClick = { onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Library)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = libraryContainerColor,
                    contentColor = libraryContentColor,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Row {
                    Icon(
                        painterResource(id = R.drawable.ic_library_music),
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = stringResource(
                            R.string.library_toolbar_content_description,
                        ),
                    )
                    Text(stringResource(R.string.library_toolbar))
                }
            }

            val discoverContainerColor =
                if (selectedHomeCategory.name == HomeCategory.Library.name) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.secondary
                }

            val discoverContentColor =
                if (selectedHomeCategory.name == HomeCategory.Library.name) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                }

            Button(
                onClick = { onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Discover)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = discoverContainerColor,
                    contentColor = discoverContentColor,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary,
                    disabledContentColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
            ) {
                Row {
                    Icon(
                        painterResource(R.drawable.genres),
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = stringResource(
                            R.string.discover_toolbar_content_description,
                        ),
                    )
                    Text(stringResource(R.string.discover_toolbar))
                }
            }
    }
}

@Composable
private fun HomeContent(
    featuredPodcasts: List<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    val pagerState = rememberPagerState { featuredPodcasts.size }
    LaunchedEffect(pagerState, featuredPodcasts) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val podcast = featuredPodcasts.getOrNull(it)
                onHomeAction(HomeAction.LibraryPodcastSelected(podcast))
            }
    }

    HomeContentGrid(
        featuredPodcasts = featuredPodcasts,
        selectedHomeCategory = selectedHomeCategory,
        filterableCategoriesModel = filterableCategoriesModel,
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        library = library,
        modifier = modifier,
        onHomeAction = onHomeAction,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
    )
}

@Composable
private fun HomeContentGrid(
    featuredPodcasts: List<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                if (featuredPodcasts.isNotEmpty()) {
                    fullWidthItem {
                        FollowedPodcastItem(
                            items = featuredPodcasts,
                            onPodcastUnfollowed = {
                                onHomeAction(HomeAction.PodcastUnfollowed(it))
                            },
                            navigateToPodcastDetails = navigateToPodcastDetails,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }

                libraryItems(
                    library = library,
                    navigateToPlayer = navigateToPlayer,
                    onQueueEpisode = { onHomeAction(HomeAction.QueueEpisode(it)) },
                    removeFromQueue = { onHomeAction(HomeAction.RemoveEpisode(it)) },
                )
            }

            HomeCategory.Discover -> {
                discoverItems(
                    filterableCategoriesModel = filterableCategoriesModel,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    navigateToPodcastDetails = navigateToPodcastDetails,
                    navigateToPlayer = navigateToPlayer,
                    onCategorySelected = { onHomeAction(HomeAction.CategorySelected(it)) },
                    onTogglePodcastFollowed = {
                        onHomeAction(HomeAction.TogglePodcastFollowed(it))
                    },
                    onQueueEpisode = { onHomeAction(HomeAction.QueueEpisode(it)) },
                    removeFromQueue = { onHomeAction(HomeAction.RemoveEpisode(it)) },
                )
            }
        }
    }
}

@Composable
private fun FollowedPodcastItem(
    items: List<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FollowedPodcasts(
            items = items,
            onPodcastUnfollowed = onPodcastUnfollowed,
            navigateToPodcastDetails = navigateToPodcastDetails,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowedPodcasts(
    items: List<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier.background(Color.Transparent),
    ) {
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items.size) { page ->
                val podcast = items[page]
                val lastDateText = if (podcast.lastEpisodeDate != null) lastUpdated(podcast.lastEpisodeDate) else null
                FollowedPodcastCarouselItem(
                    podcastImageUrl = podcast.imageUrl,
                    podcastTitle = podcast.title,
                    onUnfollowedClick = { onPodcastUnfollowed(podcast) },
                    lastEpisodeDateText = lastDateText,
                    modifier = Modifier
                        .width(205.dp)
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.large)
                        .clickable {
                            navigateToPodcastDetails(podcast)
                        },
                )
            }
        }
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    podcastTitle: String,
    podcastImageUrl: String,
    modifier: Modifier = Modifier,
    lastEpisodeDateText: String? = null,
    onUnfollowedClick: () -> Unit,
) {
    val gradient = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))

    Box(
        modifier
            .height(230.dp),
    ) {
        PodcastImage(
            podcastImageUrl = podcastImageUrl,
            contentDescription = podcastTitle,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium),
        )
        ToggleFollowPodcastIconButton(
            onClick = onUnfollowedClick,
            isFollowed = true, /* All podcasts are followed in this feed */
            modifier = Modifier.align(Alignment.TopStart),
        )
        Box(modifier = Modifier.matchParentSize().background(gradient))
        if (lastEpisodeDateText != null) {
            Text(
                text = lastEpisodeDateText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
            )
        }
    }
}

@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)

        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)

        else -> stringResource(R.string.updated_today)
    }
}

