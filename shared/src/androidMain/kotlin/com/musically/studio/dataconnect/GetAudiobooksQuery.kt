
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


public interface GetAudiobooksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetAudiobooksQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val audiobooks: List<AudiobooksItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class AudiobooksItem(
  
    val id: String,
  
    val title: String,
  
    val author: Author,
  
    val narrator: String?,
  
    val coverUrl: String?,
  
    val totalDurationMs: Int?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Author(
  
    val id: String,
  
    val name: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetAudiobooks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAudiobooksQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetAudiobooksQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetAudiobooksQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetAudiobooksQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetAudiobooksQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetAudiobooksQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

