
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


public interface ListFeatureHighlightsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListFeatureHighlightsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val featureHighlights: List<FeatureHighlightsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FeatureHighlightsItem(
  
    val id: String,
  
    val iconName: String,
  
    val title: String,
  
    val description: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListFeatureHighlights"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListFeatureHighlightsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListFeatureHighlightsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListFeatureHighlightsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListFeatureHighlightsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListFeatureHighlightsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListFeatureHighlightsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

