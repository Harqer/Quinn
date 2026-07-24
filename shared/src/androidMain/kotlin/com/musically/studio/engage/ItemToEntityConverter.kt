package com.musically.studio.engage

import android.net.Uri
import com.google.android.engage.audio.datamodel.MusicTrackEntity
import com.google.android.engage.audio.datamodel.PlaylistEntity
import com.google.android.engage.common.datamodel.Image
import com.musically.studio.network.MavePlaylist
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.MavePodcast
import com.musically.studio.network.MaveAudiobook
import com.google.android.engage.audio.datamodel.PodcastSeriesEntity
import com.google.android.engage.books.datamodel.AudiobookEntity

object ItemToEntityConverter {

    fun convertTrackToEntity(track: MaveTrack): MusicTrackEntity {
        val imageUrl = track.album.images.firstOrNull()?.url
        val builder = MusicTrackEntity.Builder()
            .setEntityId(track.id)
            .setName(track.name)
            .setPlayBackUri(Uri.parse("musically://track/${track.id}"))
            .setDurationMillis(track.durationMs)
            .addArtists(track.artists.map { it.name })
            
        if (!imageUrl.isNullOrBlank()) {
            val image = Image.Builder().setImageUri(Uri.parse(imageUrl)).build()
            builder.addPosterImage(image)
        }
        
        return builder.build()
    }

    fun convertPlaylistToEntity(playlist: MavePlaylist): PlaylistEntity {
        val imageUrl = playlist.coverUrl
        val builder = PlaylistEntity.Builder()
            .setEntityId(playlist.id)
            .setName(playlist.name)
            .setPlayBackUri(Uri.parse("musically://playlist/${playlist.id}"))
            .setSongsCount(playlist.tracks.size)
            .setDurationMillis(playlist.durationMs)
            .apply {
                playlist.description?.let { setDescription(it) }
            }
            
        if (!imageUrl.isNullOrBlank()) {
            val image = Image.Builder().setImageUri(Uri.parse(imageUrl)).build()
            builder.addPosterImage(image)
        }

        return builder.build()
    }

    fun convertPodcastToEntity(podcast: MavePodcast): PodcastSeriesEntity {
        val imageUrl = podcast.imageUrl
        val builder = PodcastSeriesEntity.Builder()
            .setEntityId(podcast.id)
            .setName(podcast.name)
            .setPlayBackUri(Uri.parse("musically://podcast/${podcast.id}"))
            .setInfoPageUri(Uri.parse("musically://podcast/${podcast.id}/info"))
            .apply {
                podcast.description?.let { setDescription(it) }
            }
            
        if (!imageUrl.isNullOrBlank()) {
            val image = Image.Builder().setImageUri(Uri.parse(imageUrl)).build()
            builder.addPosterImage(image)
        }

        return builder.build()
    }

    fun convertAudiobookToEntity(audiobook: MaveAudiobook): AudiobookEntity {
        val imageUrl = audiobook.imageUrl
        val builder = AudiobookEntity.Builder()
            .setEntityId(audiobook.id)
            .setName(audiobook.title)
            .addAuthor(audiobook.author)
            .setActionLinkUri(Uri.parse("musically://audiobook/${audiobook.id}"))

        if (!imageUrl.isNullOrBlank()) {
            val image = Image.Builder().setImageUri(Uri.parse(imageUrl)).build()
            builder.addPosterImage(image)
        }

        return builder.build()
    }
}
