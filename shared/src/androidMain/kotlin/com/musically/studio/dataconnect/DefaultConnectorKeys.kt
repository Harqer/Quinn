
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.musically.studio.dataconnect


  @kotlinx.serialization.Serializable
  public data class AiPresetKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class AudiobookKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class AuthorKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class BookmarkedTrackKey(
  
    val userUid: String,
  
    val trackId: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class CameraCaptureKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class ChapterKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class ChatMessageKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class CoverArtKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class EpisodeKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class InstrumentKey(
  
    val name: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class JamSessionHistoryKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class LikedTrackKey(
  
    val userUid: String,
  
    val trackId: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class MusicVideoKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class PaymentHistoryKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class PlayHistoryKey(
  
    val userUid: String,
  
    val trackId: String,
  
    val playedAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class PlaylistEntryKey(
  
    val playlistId: String,
  
    val trackId: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class PlaylistKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class ShowKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class TrackKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class UserEpisodeProgressKey(
  
    val userUid: String,
  
    val episodeId: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class UserKey(
  
    val uid: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class UserSettingsKey(
  
    val userId: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class VideoDigestionKey(
  
    val id: String,
  
  ) {
    
    
  }

