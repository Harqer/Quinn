
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



public interface CreateAudiobookMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateAudiobookMutation.Data,
      CreateAudiobookMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val title: String,
  
    val authorId: String,
  
    val narrator: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val storyContext: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var title: String
        public var authorId: String
        public var narrator: String?
        public var coverUrl: String?
        public var storyContext: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          title: String,authorId: String,
          block_: Builder.() -> Unit
        ): Variables {
          var title= title
            var authorId= authorId
            var narrator: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var storyContext: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var authorId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { authorId = value_ }
              
            override var narrator: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { narrator = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var coverUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { coverUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var storyContext: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { storyContext = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              title=title,authorId=authorId,narrator=narrator,coverUrl=coverUrl,storyContext=storyContext,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val audiobook_insert: AudiobookKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateAudiobook"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateAudiobookMutation.ref(
  
    title: String,authorId: String,

  
    block_: CreateAudiobookMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateAudiobookMutation.Data,
    CreateAudiobookMutation.Variables
  > =
  ref(
    
      CreateAudiobookMutation.Variables.build(
        title=title,authorId=authorId,
  
    block_
      )
    
  )

public suspend fun CreateAudiobookMutation.execute(

  
    
      title: String,authorId: String,

  
    block_: CreateAudiobookMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateAudiobookMutation.Data,
    CreateAudiobookMutation.Variables
  > =
  ref(
    
      title=title,authorId=authorId,
  
    block_
    
  ).execute()


