
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


public interface ListInstrumentsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListInstrumentsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val instruments: List<InstrumentsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class InstrumentsItem(
  
    val name: String,
  
    val iconUrl: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListInstruments"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListInstrumentsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListInstrumentsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListInstrumentsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListInstrumentsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListInstrumentsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListInstrumentsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

