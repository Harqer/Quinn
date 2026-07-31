
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


public interface GetPodcastsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetPodcastsQuery.Data,
      Unit
    >
{
  

  
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
    public val operationName: String = "GetPodcasts"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetPodcastsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetPodcastsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetPodcastsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetPodcastsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetPodcastsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetPodcastsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

