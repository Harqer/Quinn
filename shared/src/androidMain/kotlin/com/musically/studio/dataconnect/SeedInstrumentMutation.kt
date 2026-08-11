
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



public interface SeedInstrumentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedInstrumentMutation.Data,
      SeedInstrumentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val name: String,
  
    val iconUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var name: String
        public var iconUrl: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          name: String,
          block_: Builder.() -> Unit
        ): Variables {
          var name= name
            var iconUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var iconUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { iconUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              name=name,iconUrl=iconUrl,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val instrument_upsert: InstrumentKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedInstrument"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedInstrumentMutation.ref(
  
    name: String,

  
    block_: SeedInstrumentMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SeedInstrumentMutation.Data,
    SeedInstrumentMutation.Variables
  > =
  ref(
    
      SeedInstrumentMutation.Variables.build(
        name=name,
  
    block_
      )
    
  )

public suspend fun SeedInstrumentMutation.execute(

  
    
      name: String,

  
    block_: SeedInstrumentMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SeedInstrumentMutation.Data,
    SeedInstrumentMutation.Variables
  > =
  ref(
    
      name=name,
  
    block_
    
  ).execute()


