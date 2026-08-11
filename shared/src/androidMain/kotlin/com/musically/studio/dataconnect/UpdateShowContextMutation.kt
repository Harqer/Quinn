
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



public interface UpdateShowContextMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpdateShowContextMutation.Data,
      UpdateShowContextMutation.Variables
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
  
    val show_update: ShowKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateShowContext"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateShowContextMutation.ref(
  
    id: String,storyContext: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateShowContextMutation.Data,
    UpdateShowContextMutation.Variables
  > =
  ref(
    
      UpdateShowContextMutation.Variables(
        id=id,storyContext=storyContext,
  
      )
    
  )

public suspend fun UpdateShowContextMutation.execute(

  
    
      id: String,storyContext: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateShowContextMutation.Data,
    UpdateShowContextMutation.Variables
  > =
  ref(
    
      id=id,storyContext=storyContext,
  
    
  ).execute()


