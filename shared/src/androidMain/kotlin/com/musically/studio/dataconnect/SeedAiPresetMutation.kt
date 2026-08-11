
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



public interface SeedAiPresetMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedAiPresetMutation.Data,
      SeedAiPresetMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val name: String,
  
    val promptFragment: String,
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var name: String
        public var promptFragment: String
        public var imageUrl: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          name: String,promptFragment: String,
          block_: Builder.() -> Unit
        ): Variables {
          var name= name
            var promptFragment= promptFragment
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var promptFragment: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { promptFragment = value_ }
              
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              name=name,promptFragment=promptFragment,imageUrl=imageUrl,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val aIPreset_insert: AiPresetKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedAIPreset"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedAiPresetMutation.ref(
  
    name: String,promptFragment: String,

  
    block_: SeedAiPresetMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SeedAiPresetMutation.Data,
    SeedAiPresetMutation.Variables
  > =
  ref(
    
      SeedAiPresetMutation.Variables.build(
        name=name,promptFragment=promptFragment,
  
    block_
      )
    
  )

public suspend fun SeedAiPresetMutation.execute(

  
    
      name: String,promptFragment: String,

  
    block_: SeedAiPresetMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SeedAiPresetMutation.Data,
    SeedAiPresetMutation.Variables
  > =
  ref(
    
      name=name,promptFragment=promptFragment,
  
    block_
    
  ).execute()


