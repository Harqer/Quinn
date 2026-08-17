
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


public interface SearchAudiobooksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      SearchAudiobooksQuery.Data,
      SearchAudiobooksQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val query: String,
  
  ) {
    
    
  }
  

  
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
    public val operationName: String = "SearchAudiobooks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SearchAudiobooksQuery.ref(
  
    query: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    SearchAudiobooksQuery.Data,
    SearchAudiobooksQuery.Variables
  > =
  ref(
    
      SearchAudiobooksQuery.Variables(
        query=query,
  
      )
    
  )

public suspend fun SearchAudiobooksQuery.execute(

  
    
      query: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    SearchAudiobooksQuery.Data,
    SearchAudiobooksQuery.Variables
  > =
  ref(
    
      query=query,
  
    
  ).execute()


  public fun SearchAudiobooksQuery.flow(
    
      query: String,

  
    
    ): kotlinx.coroutines.flow.Flow<SearchAudiobooksQuery.Data> =
    ref(
        
          query=query,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

