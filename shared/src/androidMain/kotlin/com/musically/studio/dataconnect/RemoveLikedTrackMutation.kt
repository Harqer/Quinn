
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



public interface RemoveLikedTrackMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      RemoveLikedTrackMutation.Data,
      RemoveLikedTrackMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val likedTrack_delete: LikedTrackKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RemoveLikedTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RemoveLikedTrackMutation.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RemoveLikedTrackMutation.Data,
    RemoveLikedTrackMutation.Variables
  > =
  ref(
    
      RemoveLikedTrackMutation.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun RemoveLikedTrackMutation.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RemoveLikedTrackMutation.Data,
    RemoveLikedTrackMutation.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


