package com.musically.studio.audio

import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import androidx.core.net.toUri
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.LibraryResult
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaConstants
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.Futures
import com.google.common.collect.ImmutableList
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.musically.studio.network.GeminiLiveManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class PlaybackService : MediaLibraryService() {

    @Inject
    lateinit var geminiLiveManager: GeminiLiveManager

    private var mediaSession: MediaLibrarySession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Pipe to feed the ExoPlayer DataSource from the WebSocket SharedFlow
    private val pipedOutputStream = PipedOutputStream()
    private val pipedInputStream = PipedInputStream(pipedOutputStream, 1024 * 64)

    override fun onCreate() {
        super.onCreate()
        
        val defaultDataSourceFactory = androidx.media3.datasource.DefaultDataSource.Factory(this)
        val customDataSourceFactory = DataSource.Factory {
            object : DataSource {
                private var dataSource: DataSource? = null
                override fun addTransferListener(transferListener: androidx.media3.datasource.TransferListener) {}
                override fun open(dataSpec: androidx.media3.datasource.DataSpec): Long {
                    dataSource = if (dataSpec.uri.scheme == "mave") {
                        FlowDataSource(pipedInputStream)
                    } else {
                        defaultDataSourceFactory.createDataSource()
                    }
                    return dataSource!!.open(dataSpec)
                }
                override fun getUri(): android.net.Uri? = dataSource?.uri
                override fun close() {
                    dataSource?.close()
                }
                override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
                    return dataSource?.read(buffer, offset, length) ?: -1
                }
            }
        }
        val mediaSourceFactory = androidx.media3.exoplayer.source.DefaultMediaSourceFactory(this)
            .setDataSourceFactory(customDataSourceFactory)
            
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        
        val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
            .setUsage(androidx.media3.common.C.USAGE_ASSISTANT)
            .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()
        
        player.setAudioAttributes(audioAttributes, true)
        
        createNotificationChannel()
        
        mediaSession = MediaLibrarySession.Builder(this, player, CustomMediaLibrarySessionCallback())
            .setId("LyriaMediaLibrarySession")
            .build()
            
        // Collect audio bytes and write them to the pipe from both sources
        scope.launch {
            geminiLiveManager.audioOutput.collect { bytes ->
                try {
                    pipedOutputStream.write(bytes)
                    pipedOutputStream.flush()
                } catch (e: Exception) {
                    Timber.e(e, "Error writing bytes to ExoPlayer pipe")
                }
            }
        }
        
        val mediaItem = MediaItem.Builder()
            .setUri("mave://stream".toUri())
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle("Mave Studio")
                    .setArtist("AI Generated Vibe")
                    .build()
            )
            .build()
        
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    geminiLiveManager.sendText("User resumed audio playback")
                } else {
                    geminiLiveManager.sendText("User paused audio playback")
                }
            }
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    // Media3 automatically handles foreground service and rich media notifications
    // based on the MediaSession state.

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "playback_channel",
            "Mave Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        scope.cancel()
        pipedOutputStream.close()
        pipedInputStream.close()
        super.onDestroy()
    }
    
    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
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
                putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE, MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM)
                putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE, MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM)
                putBoolean("android.media.extra.RECENT", true)
            }
            val libraryParams = LibraryParams.Builder().setExtras(rootExtras).build()
            val rootItem = MediaItem.Builder().setMediaId("root").setMediaMetadata(
                MediaMetadata.Builder().setTitle("Lyria AI Music").setIsBrowsable(true).setIsPlayable(false).setFolderType(MediaMetadata.FOLDER_TYPE_MIXED).setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED).build()
            ).build()
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

        override fun onGetItem(session: MediaLibrarySession, browser: MediaSession.ControllerInfo, mediaId: String): ListenableFuture<LibraryResult<MediaItem>> {
            val item = findMediaItemById(mediaId)
            return if (item != null) Futures.immediateFuture(LibraryResult.ofItem(item, null))
            else Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE))
        }

        override fun onAddMediaItems(mediaSession: MediaSession, controller: MediaSession.ControllerInfo, mediaItems: MutableList<MediaItem>): ListenableFuture<MutableList<MediaItem>> {
            val resolvedItems = mediaItems.map { resolveMediaItemPlaybackUri(it) }.toMutableList()
            return Futures.immediateFuture(resolvedItems)
        }
    }

    private fun buildRootCategories(): List<MediaItem> = listOf(
        createFolderItem("radio_root", "AI Radio Streams", "Continuous AI audio streams", MediaMetadata.FOLDER_TYPE_TITLES),
        createFolderItem("playlists_root", "Playlists", "Your AI playlists", MediaMetadata.FOLDER_TYPE_PLAYLISTS),
        createFolderItem("podcasts_root", "AI Podcasts", "Tech deep dives", MediaMetadata.FOLDER_TYPE_ALBUMS),
        createFolderItem("recent_tracks_root", "Recent Tracks", "Recently generated", MediaMetadata.FOLDER_TYPE_TITLES)
    )

    private fun fetchDynamicStreams(category: String): List<MediaItem> {
        val result = mutableListOf<MediaItem>()
        return try {
            val task = com.google.firebase.functions.FirebaseFunctions.getInstance()
                .getHttpsCallable("getDynamicStreams")
                .call(mapOf("category" to category))
            val response = kotlinx.coroutines.runBlocking { task.await() }
            val dataList = response.data as? List<Map<String, String>> ?: emptyList()
            if (dataList.isEmpty()) {
                result.add(createPlayableItem("error_$category", "No Streams Found", "Generate some tracks first!", "", MediaMetadata.MEDIA_TYPE_MUSIC))
            } else {
                for (item in dataList) {
                    val id = item["id"] ?: continue
                    val title = item["title"] ?: "Unknown"
                    val artist = item["artist"] ?: "Unknown"
                    val url = item["url"] ?: ""
                    result.add(createPlayableItem(id, title, artist, url, MediaMetadata.MEDIA_TYPE_MUSIC))
                }
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch dynamic streams for $category")
            listOf(createPlayableItem("error_$category", "Stream Fetch Failed", "Network Error - Please Try Again", "", MediaMetadata.MEDIA_TYPE_MUSIC))
        }
    }

    private fun buildRadioStreams(): List<MediaItem> = fetchDynamicStreams("radio")
    private fun buildUserPlaylists(): List<MediaItem> = listOf(createFolderItem("playlist_favorites", "Favorites", "Your saved tracks", MediaMetadata.FOLDER_TYPE_TITLES))
    private fun buildAiPodcasts(): List<MediaItem> = fetchDynamicStreams("podcasts")
    private fun buildRecentTracks(): List<MediaItem> = fetchDynamicStreams("recent")

    private fun createFolderItem(id: String, title: String, subtitle: String, folderType: Int): MediaItem = MediaItem.Builder().setMediaId(id).setMediaMetadata(
        MediaMetadata.Builder().setTitle(title).setSubtitle(subtitle).setIsBrowsable(true).setIsPlayable(false).setFolderType(folderType).setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED).build()
    ).build()

    private fun createPlayableItem(id: String, title: String, artist: String, streamUrl: String, mediaType: Int): MediaItem = MediaItem.Builder().setMediaId(id).setUri(Uri.parse(streamUrl)).setMediaMetadata(
        MediaMetadata.Builder().setTitle(title).setArtist(artist).setSubtitle(artist).setIsBrowsable(false).setIsPlayable(true).setMediaType(mediaType).build()
    ).build()

    private fun findMediaItemById(mediaId: String): MediaItem? = (buildRadioStreams() + buildAiPodcasts() + buildRecentTracks()).find { it.mediaId == mediaId }
    private fun resolveMediaItemPlaybackUri(item: MediaItem): MediaItem = findMediaItemById(item.mediaId) ?: item
}

@OptIn(UnstableApi::class)
class FlowDataSource(private val inputStream: java.io.InputStream) : BaseDataSource(/* isNetwork = */ false) {
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        opened = true
        transferInitializing(dataSpec)
        transferStarted(dataSpec)
        return C.LENGTH_UNSET.toLong()
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        return try {
            if (inputStream.available() <= 0 && opened) {
                Thread.sleep(20)
            }
            val bytesRead = inputStream.read(buffer, offset, length)
            if (bytesRead == -1) {
                C.RESULT_END_OF_INPUT
            } else {
                bytesTransferred(bytesRead)
                bytesRead
            }
        } catch (e: Exception) {
            C.RESULT_END_OF_INPUT
        }
    }

    override fun getUri(): Uri? = "mave://stream".toUri()

    override fun close() {
        if (opened) {
            opened = false
            transferEnded()
        }
    }
}
