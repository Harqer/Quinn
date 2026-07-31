
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



public interface CreateVideoDigestionMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateVideoDigestionMutation.Data,
      CreateVideoDigestionMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val videoUrl: String,
  
    val extractedAudioUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val atmosphereSummary: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val generatedTrackId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var videoUrl: String
        public var extractedAudioUrl: String?
        public var atmosphereSummary: String?
        public var generatedTrackId: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          videoUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var videoUrl= videoUrl
            var extractedAudioUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var atmosphereSummary: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var generatedTrackId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var videoUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { videoUrl = value_ }
              
            override var extractedAudioUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { extractedAudioUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var atmosphereSummary: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { atmosphereSummary = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var generatedTrackId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { generatedTrackId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              videoUrl=videoUrl,extractedAudioUrl=extractedAudioUrl,atmosphereSummary=atmosphereSummary,generatedTrackId=generatedTrackId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val videoDigestion_insert: VideoDigestionKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateVideoDigestion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateVideoDigestionMutation.ref(
  
    videoUrl: String,

  
    block_: CreateVideoDigestionMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateVideoDigestionMutation.Data,
    CreateVideoDigestionMutation.Variables
  > =
  ref(
    
      CreateVideoDigestionMutation.Variables.build(
        videoUrl=videoUrl,
  
    block_
      )
    
  )

public suspend fun CreateVideoDigestionMutation.execute(

  
    
      videoUrl: String,

  
    block_: CreateVideoDigestionMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateVideoDigestionMutation.Data,
    CreateVideoDigestionMutation.Variables
  > =
  ref(
    
      videoUrl=videoUrl,
  
    block_
    
  ).execute()


