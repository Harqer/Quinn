
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



public interface RecordPaymentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      RecordPaymentMutation.Data,
      RecordPaymentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userUid: String,
  
    val amount: Double,
  
    val currency: String,
  
    val status: String,
  
    val stripeInvoiceId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var userUid: String
        public var amount: Double
        public var currency: String
        public var status: String
        public var stripeInvoiceId: String?
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          userUid: String,amount: Double,currency: String,status: String,
          block_: Builder.() -> Unit
        ): Variables {
          var userUid= userUid
            var amount= amount
            var currency= currency
            var status= status
            var stripeInvoiceId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var userUid: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { userUid = value_ }
              
            override var amount: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { amount = value_ }
              
            override var currency: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { currency = value_ }
              
            override var status: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { status = value_ }
              
            override var stripeInvoiceId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { stripeInvoiceId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              userUid=userUid,amount=amount,currency=currency,status=status,stripeInvoiceId=stripeInvoiceId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val paymentHistory_insert: PaymentHistoryKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RecordPayment"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RecordPaymentMutation.ref(
  
    userUid: String,amount: Double,currency: String,status: String,

  
    block_: RecordPaymentMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RecordPaymentMutation.Data,
    RecordPaymentMutation.Variables
  > =
  ref(
    
      RecordPaymentMutation.Variables.build(
        userUid=userUid,amount=amount,currency=currency,status=status,
  
    block_
      )
    
  )

public suspend fun RecordPaymentMutation.execute(

  
    
      userUid: String,amount: Double,currency: String,status: String,

  
    block_: RecordPaymentMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RecordPaymentMutation.Data,
    RecordPaymentMutation.Variables
  > =
  ref(
    
      userUid=userUid,amount=amount,currency=currency,status=status,
  
    block_
    
  ).execute()


