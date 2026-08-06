
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


public interface ListHomeSectionsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListHomeSectionsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val homeSections: List<HomeSectionsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class HomeSectionsItem(
  
    val id: String,
  
    val title: String,
  
    val orderIndex: Int,
  
    val route: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListHomeSections"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListHomeSectionsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListHomeSectionsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListHomeSectionsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListHomeSectionsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListHomeSectionsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListHomeSectionsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

