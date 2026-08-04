
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


public interface ListFaqItemsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListFaqItemsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val faqItems: List<FaqItemsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FaqItemsItem(
  
    val id: String,
  
    val question: String,
  
    val answer: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListFaqItems"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListFaqItemsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListFaqItemsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListFaqItemsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListFaqItemsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListFaqItemsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListFaqItemsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

