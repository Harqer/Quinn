
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


public interface GetPaymentHistoryQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetPaymentHistoryQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val paymentHistories: List<PaymentHistoriesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class PaymentHistoriesItem(
  
    val id: String,
  
    val amount: Double,
  
    val currency: String,
  
    val status: String,
  
    val stripeInvoiceId: String?,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetPaymentHistory"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetPaymentHistoryQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetPaymentHistoryQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetPaymentHistoryQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetPaymentHistoryQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetPaymentHistoryQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetPaymentHistoryQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

