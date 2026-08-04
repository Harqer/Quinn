
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


public interface ListAiPresetsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListAiPresetsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val aIPresets: List<AIPresetsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class AIPresetsItem(
  
    val id: String,
  
    val name: String,
  
    val promptFragment: String,
  
    val imageUrl: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListAIPresets"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListAiPresetsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListAiPresetsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListAiPresetsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListAiPresetsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListAiPresetsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListAiPresetsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

