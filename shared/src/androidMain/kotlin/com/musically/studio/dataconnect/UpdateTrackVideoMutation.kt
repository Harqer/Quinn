
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



public interface UpdateTrackVideoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpdateTrackVideoMutation.Data,
      UpdateTrackVideoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val videoUrl: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val track_update: TrackKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateTrackVideo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateTrackVideoMutation.ref(
  
    id: String,videoUrl: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateTrackVideoMutation.Data,
    UpdateTrackVideoMutation.Variables
  > =
  ref(
    
      UpdateTrackVideoMutation.Variables(
        id=id,videoUrl=videoUrl,
  
      )
    
  )

public suspend fun UpdateTrackVideoMutation.execute(

  
    
      id: String,videoUrl: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateTrackVideoMutation.Data,
    UpdateTrackVideoMutation.Variables
  > =
  ref(
    
      id=id,videoUrl=videoUrl,
  
    
  ).execute()


