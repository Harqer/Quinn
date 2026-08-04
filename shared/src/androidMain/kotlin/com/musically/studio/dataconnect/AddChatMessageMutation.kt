
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



public interface AddChatMessageMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      AddChatMessageMutation.Data,
      AddChatMessageMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
    val sender: String,
  
    val text: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val chatMessage_insert: ChatMessageKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AddChatMessage"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddChatMessageMutation.ref(
  
    userId: String,sender: String,text: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AddChatMessageMutation.Data,
    AddChatMessageMutation.Variables
  > =
  ref(
    
      AddChatMessageMutation.Variables(
        userId=userId,sender=sender,text=text,
  
      )
    
  )

public suspend fun AddChatMessageMutation.execute(

  
    
      userId: String,sender: String,text: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AddChatMessageMutation.Data,
    AddChatMessageMutation.Variables
  > =
  ref(
    
      userId=userId,sender=sender,text=text,
  
    
  ).execute()


