
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



public interface UpdateAudiobookContextMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpdateAudiobookContextMutation.Data,
      UpdateAudiobookContextMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val storyContext: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val audiobook_update: AudiobookKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateAudiobookContext"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateAudiobookContextMutation.ref(
  
    id: String,storyContext: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateAudiobookContextMutation.Data,
    UpdateAudiobookContextMutation.Variables
  > =
  ref(
    
      UpdateAudiobookContextMutation.Variables(
        id=id,storyContext=storyContext,
  
      )
    
  )

public suspend fun UpdateAudiobookContextMutation.execute(

  
    
      id: String,storyContext: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateAudiobookContextMutation.Data,
    UpdateAudiobookContextMutation.Variables
  > =
  ref(
    
      id=id,storyContext=storyContext,
  
    
  ).execute()


