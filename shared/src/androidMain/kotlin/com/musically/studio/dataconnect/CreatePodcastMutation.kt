
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



public interface CreatePodcastMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreatePodcastMutation.Data,
      CreatePodcastMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val name: String,
  
    val publisher: String,
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val description: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var name: String
        public var publisher: String
        public var imageUrl: String?
        public var description: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          name: String,publisher: String,
          block_: Builder.() -> Unit
        ): Variables {
          var name= name
            var publisher= publisher
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var description: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var publisher: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { publisher = value_ }
              
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var description: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              name=name,publisher=publisher,imageUrl=imageUrl,description=description,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val podcast_insert: PodcastKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreatePodcast"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreatePodcastMutation.ref(
  
    name: String,publisher: String,

  
    block_: CreatePodcastMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreatePodcastMutation.Data,
    CreatePodcastMutation.Variables
  > =
  ref(
    
      CreatePodcastMutation.Variables.build(
        name=name,publisher=publisher,
  
    block_
      )
    
  )

public suspend fun CreatePodcastMutation.execute(

  
    
      name: String,publisher: String,

  
    block_: CreatePodcastMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreatePodcastMutation.Data,
    CreatePodcastMutation.Variables
  > =
  ref(
    
      name=name,publisher=publisher,
  
    block_
    
  ).execute()


