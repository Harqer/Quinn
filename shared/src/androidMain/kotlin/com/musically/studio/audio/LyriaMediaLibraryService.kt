package com.musically.studio.audio

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaConstants
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * MediaLibraryService providing Android Auto and Android Automotive OS head units
 * browse and playback access to Lyria AI music, streams, playlists, and podcasts.
 */
class LyriaMediaLibraryService : MediaLibraryService() {

    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val trustedCallerPackages = setOf(
        "com.google.android.projection.gearhead", // Android Auto Host
        "com.google.android.googlequicksearchbox", // Google Assistant
        "com.android.systemui"                    // Automotive System UI
    )

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        initializeSession()
    }

    private fun initializePlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                android.util.Log.e("LyriaMediaService", "Playback Error: ${error.message}", error)
            }
        })
    }

    private fun initializeSession() {
        val callback = LyriaLibrarySessionCallback()
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback)
            .setId("LyriaMediaLibrarySession")
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }

    private inner class LyriaLibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return super.onConnect(session, controller)
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootExtras = Bundle().apply {
                putInt(
                    MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
                    MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
                )
                putInt(
                    MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
                    MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM
                )
                putBoolean("android.media.extra.RECENT", true)
            }

            val libraryParams = LibraryParams.Builder()
                .setExtras(rootExtras)
                .build()

            val rootItem = MediaItem.Builder()
                .setMediaId("root")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Lyria AI Music")
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .build()
                )
                .build()

            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, libraryParams))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children = when (parentId) {
                "root" -> buildRootCategories()
                "radio_root" -> buildRadioStreams()
                "playlists_root" -> buildUserPlaylists()
                "podcasts_root" -> buildAiPodcasts()
                "recent_tracks_root" -> buildRecentTracks()
                else -> emptyList()
            }

            val fromIndex = (page * pageSize).coerceAtMost(children.size)
            val toIndex = ((page + 1) * pageSize).coerceAtMost(children.size)
            val paginatedChildren = if (fromIndex <= toIndex) children.subList(fromIndex, toIndex) else emptyList()

            return Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(paginatedChildren), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val item = findMediaItemById(mediaId)
            return if (item != null) {
                Futures.immediateFuture(LibraryResult.ofItem(item, null))
            } else {
                Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE))
            }
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val resolvedItems = mediaItems.map { item ->
                val searchQuery = item.requestMetadata.searchQuery
                if (!searchQuery.isNullOrEmpty()) {
                    resolveVoiceSearchQuery(searchQuery)
                } else if (item.localConfiguration == null) {
                    resolveMediaItemPlaybackUri(item)
                } else {
                    item
                }
            }.toMutableList()

            return Futures.immediateFuture(resolvedItems)
        }
    }

    private fun buildRootCategories(): List<MediaItem> {
        return listOf(
            createFolderItem("radio_root", "AI Radio Streams", "Continuous AI audio streams", MediaMetadata.FOLDER_TYPE_TITLES),
            createFolderItem("playlists_root", "Playlists", "Your AI playlists & mixes", MediaMetadata.FOLDER_TYPE_PLAYLISTS),
            createFolderItem("podcasts_root", "AI Podcasts", "AI generated tech & music deep dives", MediaMetadata.FOLDER_TYPE_ALBUMS),
            createFolderItem("recent_tracks_root", "Recent Tracks", "Recently generated audio tracks", MediaMetadata.FOLDER_TYPE_TITLES)
        )
    }

    private fun buildRadioStreams(): List<MediaItem> {
        return listOf(
            createPlayableItem("radio_continuous", "Continuous Vibe", "Infinite AI Vibe Radio", "https://stream.lyria.ai/vibe.m3u8", MediaMetadata.MEDIA_TYPE_RADIO_STATION),
            createPlayableItem("radio_infinite", "Infinite Lyria Radio", "Adaptive AI Radio Stream", "https://stream.lyria.ai/infinite.m3u8", MediaMetadata.MEDIA_TYPE_RADIO_STATION)
        )
    }

    private fun buildUserPlaylists(): List<MediaItem> {
        return listOf(
            createFolderItem("playlist_favorites", "Favorites", "Your saved tracks", MediaMetadata.FOLDER_TYPE_TITLES),
            createFolderItem("playlist_ai_jams", "AI Generated Jams", "Top generated vibes", MediaMetadata.FOLDER_TYPE_TITLES)
        )
    }

    private fun buildAiPodcasts(): List<MediaItem> {
        return listOf(
            createPlayableItem("podcast_tech_vibe", "Tech Vibe Daily", "Daily AI Music Trends", "https://audio.lyria.ai/podcasts/tech_vibe_ep1.mp3", MediaMetadata.MEDIA_TYPE_PODCAST)
        )
    }

    private fun buildRecentTracks(): List<MediaItem> {
        return listOf(
            createPlayableItem("track_recent_1", "Cyber Synth Ambient", "Lyria AI Engine v2", "https://audio.lyria.ai/tracks/cybersynth.mp3", MediaMetadata.MEDIA_TYPE_MUSIC)
        )
    }

    private fun createFolderItem(id: String, title: String, subtitle: String, folderType: Int): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .setFolderType(folderType)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .build()
            )
            .build()
    }

    private fun createPlayableItem(id: String, title: String, artist: String, streamUrl: String, mediaType: Int): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(streamUrl))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setSubtitle(artist)
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setMediaType(mediaType)
                    .build()
            )
            .build()
    }

    private fun findMediaItemById(mediaId: String): MediaItem? {
        val allItems = buildRadioStreams() + buildAiPodcasts() + buildRecentTracks()
        return allItems.find { it.mediaId == mediaId }
    }

    private fun resolveMediaItemPlaybackUri(item: MediaItem): MediaItem {
        return findMediaItemById(item.mediaId) ?: item
    }

    private fun resolveVoiceSearchQuery(query: String): MediaItem {
        val normalized = query.lowercase()
        return when {
            normalized.contains("radio") || normalized.contains("infinite") -> buildRadioStreams()[1]
            normalized.contains("podcast") -> buildAiPodcasts()[0]
            else -> buildRadioStreams()[0]
        }
    }
}
