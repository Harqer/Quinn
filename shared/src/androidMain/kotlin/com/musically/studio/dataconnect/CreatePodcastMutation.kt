
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
  
    val title: String,
  
    val publisher: String,
  
    val coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val description: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var title: String
        public var publisher: String
        public var coverUrl: String?
        public var description: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          title: String,publisher: String,
          block_: Builder.() -> Unit
        ): Variables {
          var title= title
            var publisher= publisher
            var coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var description: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var publisher: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { publisher = value_ }
              
            override var coverUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { coverUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var description: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              title=title,publisher=publisher,coverUrl=coverUrl,description=description,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val show_insert: ShowKey,
  
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
  
    title: String,publisher: String,

  
    block_: CreatePodcastMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreatePodcastMutation.Data,
    CreatePodcastMutation.Variables
  > =
  ref(
    
      CreatePodcastMutation.Variables.build(
        title=title,publisher=publisher,
  
    block_
      )
    
  )

public suspend fun CreatePodcastMutation.execute(

  
    
      title: String,publisher: String,

  
    block_: CreatePodcastMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreatePodcastMutation.Data,
    CreatePodcastMutation.Variables
  > =
  ref(
    
      title=title,publisher=publisher,
  
    block_
    
  ).execute()


