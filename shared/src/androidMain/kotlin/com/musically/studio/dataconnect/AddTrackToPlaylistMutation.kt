
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



public interface AddTrackToPlaylistMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      AddTrackToPlaylistMutation.Data,
      AddTrackToPlaylistMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val playlistId: String,
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val playlistEntry_insert: PlaylistEntryKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AddTrackToPlaylist"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddTrackToPlaylistMutation.ref(
  
    playlistId: String,trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AddTrackToPlaylistMutation.Data,
    AddTrackToPlaylistMutation.Variables
  > =
  ref(
    
      AddTrackToPlaylistMutation.Variables(
        playlistId=playlistId,trackId=trackId,
  
      )
    
  )

public suspend fun AddTrackToPlaylistMutation.execute(

  
    
      playlistId: String,trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AddTrackToPlaylistMutation.Data,
    AddTrackToPlaylistMutation.Variables
  > =
  ref(
    
      playlistId=playlistId,trackId=trackId,
  
    
  ).execute()


