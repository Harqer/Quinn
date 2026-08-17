
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



public interface SaveUserEpisodeProgressMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SaveUserEpisodeProgressMutation.Data,
      SaveUserEpisodeProgressMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val episodeId: String,
  
    val progressMs: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userEpisodeProgress_upsert: UserEpisodeProgressKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SaveUserEpisodeProgress"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SaveUserEpisodeProgressMutation.ref(
  
    episodeId: String,progressMs: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    SaveUserEpisodeProgressMutation.Data,
    SaveUserEpisodeProgressMutation.Variables
  > =
  ref(
    
      SaveUserEpisodeProgressMutation.Variables(
        episodeId=episodeId,progressMs=progressMs,
  
      )
    
  )

public suspend fun SaveUserEpisodeProgressMutation.execute(

  
    
      episodeId: String,progressMs: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    SaveUserEpisodeProgressMutation.Data,
    SaveUserEpisodeProgressMutation.Variables
  > =
  ref(
    
      episodeId=episodeId,progressMs=progressMs,
  
    
  ).execute()


