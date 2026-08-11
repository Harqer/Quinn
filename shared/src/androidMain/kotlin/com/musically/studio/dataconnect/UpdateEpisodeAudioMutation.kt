
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



public interface UpdateEpisodeAudioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpdateEpisodeAudioMutation.Data,
      UpdateEpisodeAudioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val audioUrl: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val episode_update: EpisodeKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateEpisodeAudio"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateEpisodeAudioMutation.ref(
  
    id: String,audioUrl: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateEpisodeAudioMutation.Data,
    UpdateEpisodeAudioMutation.Variables
  > =
  ref(
    
      UpdateEpisodeAudioMutation.Variables(
        id=id,audioUrl=audioUrl,
  
      )
    
  )

public suspend fun UpdateEpisodeAudioMutation.execute(

  
    
      id: String,audioUrl: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateEpisodeAudioMutation.Data,
    UpdateEpisodeAudioMutation.Variables
  > =
  ref(
    
      id=id,audioUrl=audioUrl,
  
    
  ).execute()


