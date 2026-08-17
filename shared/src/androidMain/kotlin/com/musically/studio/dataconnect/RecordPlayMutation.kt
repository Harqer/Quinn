
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



public interface RecordPlayMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      RecordPlayMutation.Data,
      RecordPlayMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val playHistory_insert: PlayHistoryKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RecordPlay"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RecordPlayMutation.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RecordPlayMutation.Data,
    RecordPlayMutation.Variables
  > =
  ref(
    
      RecordPlayMutation.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun RecordPlayMutation.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RecordPlayMutation.Data,
    RecordPlayMutation.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


