
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface ListChatMessagesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListChatMessagesQuery.Data,
      ListChatMessagesQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val chatMessages: List<ChatMessagesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ChatMessagesItem(
  
    val id: String,
  
    val sender: String,
  
    val text: String,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListChatMessages"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ListChatMessagesQuery.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    ListChatMessagesQuery.Data,
    ListChatMessagesQuery.Variables
  > =
  ref(
    
      ListChatMessagesQuery.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun ListChatMessagesQuery.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListChatMessagesQuery.Data,
    ListChatMessagesQuery.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


  public fun ListChatMessagesQuery.flow(
    
      userId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<ListChatMessagesQuery.Data> =
    ref(
        
          userId=userId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

