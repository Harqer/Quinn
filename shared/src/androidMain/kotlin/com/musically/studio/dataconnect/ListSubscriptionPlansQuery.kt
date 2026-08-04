
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


public interface ListSubscriptionPlansQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      ListSubscriptionPlansQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val subscriptionPlans: List<SubscriptionPlansItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SubscriptionPlansItem(
  
    val id: String,
  
    val name: String,
  
    val tier: @kotlinx.serialization.Serializable(with = SubscriptionTier.EnumValueSerializer::class) EnumValue<SubscriptionTier>,
  
    val priceMonthly: Double,
  
    val priceAnnually: Double,
  
    val description: String,
  
    val features: List<FeaturesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FeaturesItem(
  
    val feature: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListSubscriptionPlans"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListSubscriptionPlansQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListSubscriptionPlansQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListSubscriptionPlansQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListSubscriptionPlansQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListSubscriptionPlansQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListSubscriptionPlansQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

