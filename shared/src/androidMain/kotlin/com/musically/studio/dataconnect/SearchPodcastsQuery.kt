
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


public interface SearchPodcastsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      SearchPodcastsQuery.Data,
      SearchPodcastsQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val query: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val shows: List<ShowsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ShowsItem(
  
    val id: String,
  
    val title: String,
  
    val publisher: String,
  
    val coverUrl: String?,
  
    val description: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "SearchPodcasts"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SearchPodcastsQuery.ref(
  
    query: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    SearchPodcastsQuery.Data,
    SearchPodcastsQuery.Variables
  > =
  ref(
    
      SearchPodcastsQuery.Variables(
        query=query,
  
      )
    
  )

public suspend fun SearchPodcastsQuery.execute(

  
    
      query: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    SearchPodcastsQuery.Data,
    SearchPodcastsQuery.Variables
  > =
  ref(
    
      query=query,
  
    
  ).execute()


  public fun SearchPodcastsQuery.flow(
    
      query: String,

  
    
    ): kotlinx.coroutines.flow.Flow<SearchPodcastsQuery.Data> =
    ref(
        
          query=query,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

