
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


public interface GetCategoriesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetCategoriesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val categories: List<CategoriesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class CategoriesItem(
  
    val id: String,
  
    val name: String,
  
    val type: @kotlinx.serialization.Serializable(with = CategoryType.EnumValueSerializer::class) EnumValue<CategoryType>,
  
    val colorHex: String?,
  
    val imageUrl: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetCategories"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCategoriesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetCategoriesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetCategoriesQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetCategoriesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetCategoriesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetCategoriesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

