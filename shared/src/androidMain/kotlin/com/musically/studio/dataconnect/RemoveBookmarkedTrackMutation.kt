
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



public interface RemoveBookmarkedTrackMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      RemoveBookmarkedTrackMutation.Data,
      RemoveBookmarkedTrackMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bookmarkedTrack_delete: BookmarkedTrackKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RemoveBookmarkedTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RemoveBookmarkedTrackMutation.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RemoveBookmarkedTrackMutation.Data,
    RemoveBookmarkedTrackMutation.Variables
  > =
  ref(
    
      RemoveBookmarkedTrackMutation.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun RemoveBookmarkedTrackMutation.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RemoveBookmarkedTrackMutation.Data,
    RemoveBookmarkedTrackMutation.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


