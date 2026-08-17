
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



public interface CreateCoverArtMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateCoverArtMutation.Data,
      CreateCoverArtMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
    val imageUrl: String,
  
    val promptUsed: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val stylePreset: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var trackId: String
        public var imageUrl: String
        public var promptUsed: String?
        public var stylePreset: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          trackId: String,imageUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var trackId= trackId
            var imageUrl= imageUrl
            var promptUsed: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var stylePreset: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var trackId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { trackId = value_ }
              
            override var imageUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = value_ }
              
            override var promptUsed: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { promptUsed = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var stylePreset: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { stylePreset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              trackId=trackId,imageUrl=imageUrl,promptUsed=promptUsed,stylePreset=stylePreset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val coverArt_insert: CoverArtKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateCoverArt"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateCoverArtMutation.ref(
  
    trackId: String,imageUrl: String,

  
    block_: CreateCoverArtMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateCoverArtMutation.Data,
    CreateCoverArtMutation.Variables
  > =
  ref(
    
      CreateCoverArtMutation.Variables.build(
        trackId=trackId,imageUrl=imageUrl,
  
    block_
      )
    
  )

public suspend fun CreateCoverArtMutation.execute(

  
    
      trackId: String,imageUrl: String,

  
    block_: CreateCoverArtMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateCoverArtMutation.Data,
    CreateCoverArtMutation.Variables
  > =
  ref(
    
      trackId=trackId,imageUrl=imageUrl,
  
    block_
    
  ).execute()


