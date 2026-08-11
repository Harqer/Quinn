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

package com.example.jetcaster.ui

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

/**
 * NavController extensions that links to the screens of the Jetcaster app.
 */
public object JetcasterNavController {

    public fun NavController.navigateToYourPodcast() {
        navigate("your_podcasts")
    }

    public fun NavController.navigateToLatestEpisode() {
        navigate("latest_episodes")
    }

    public fun NavController.navigateToPodcastDetails(podcastUri: String) {
        navigate("podcast_details/$podcastUri")
    }

    public fun NavController.navigateToUpNext() {
        navigate("up_next")
    }

    public fun NavController.navigateToEpisode(episodeUri: String) {
        navigate("episode/$episodeUri")
    }
}
