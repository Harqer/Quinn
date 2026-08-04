
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



public interface LikeTrackMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      LikeTrackMutation.Data,
      LikeTrackMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val likedTrack_upsert: LikedTrackKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "LikeTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun LikeTrackMutation.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    LikeTrackMutation.Data,
    LikeTrackMutation.Variables
  > =
  ref(
    
      LikeTrackMutation.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun LikeTrackMutation.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    LikeTrackMutation.Data,
    LikeTrackMutation.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


