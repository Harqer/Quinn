
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



public interface CreateJamSessionHistoryMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateJamSessionHistoryMutation.Data,
      CreateJamSessionHistoryMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val roomId: String,
  
    val gameMode: String,
  
    val participantCount: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val jamSessionHistory_insert: JamSessionHistoryKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateJamSessionHistory"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateJamSessionHistoryMutation.ref(
  
    roomId: String,gameMode: String,participantCount: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CreateJamSessionHistoryMutation.Data,
    CreateJamSessionHistoryMutation.Variables
  > =
  ref(
    
      CreateJamSessionHistoryMutation.Variables(
        roomId=roomId,gameMode=gameMode,participantCount=participantCount,
  
      )
    
  )

public suspend fun CreateJamSessionHistoryMutation.execute(

  
    
      roomId: String,gameMode: String,participantCount: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CreateJamSessionHistoryMutation.Data,
    CreateJamSessionHistoryMutation.Variables
  > =
  ref(
    
      roomId=roomId,gameMode=gameMode,participantCount=participantCount,
  
    
  ).execute()


