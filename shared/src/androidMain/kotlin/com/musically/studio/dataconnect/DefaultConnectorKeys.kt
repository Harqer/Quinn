
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
  public data class TrackKey(
  
    val id: String,
  
  ) {
    
    
  }

  @kotlinx.serialization.Serializable
  public data class UserKey(
  
    val uid: String,
  
  ) {
    
    
  }

