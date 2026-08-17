
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



public interface CreateMusicVideoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateMusicVideoMutation.Data,
      CreateMusicVideoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
    val videoUrl: String,
  
    val promptUsed: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val motionPreset: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var trackId: String
        public var videoUrl: String
        public var promptUsed: String?
        public var motionPreset: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          trackId: String,videoUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var trackId= trackId
            var videoUrl= videoUrl
            var promptUsed: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var motionPreset: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var trackId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { trackId = value_ }
              
            override var videoUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { videoUrl = value_ }
              
            override var promptUsed: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { promptUsed = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var motionPreset: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { motionPreset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              trackId=trackId,videoUrl=videoUrl,promptUsed=promptUsed,motionPreset=motionPreset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val musicVideo_insert: MusicVideoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateMusicVideo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateMusicVideoMutation.ref(
  
    trackId: String,videoUrl: String,

  
    block_: CreateMusicVideoMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateMusicVideoMutation.Data,
    CreateMusicVideoMutation.Variables
  > =
  ref(
    
      CreateMusicVideoMutation.Variables.build(
        trackId=trackId,videoUrl=videoUrl,
  
    block_
      )
    
  )

public suspend fun CreateMusicVideoMutation.execute(

  
    
      trackId: String,videoUrl: String,

  
    block_: CreateMusicVideoMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateMusicVideoMutation.Data,
    CreateMusicVideoMutation.Variables
  > =
  ref(
    
      trackId=trackId,videoUrl=videoUrl,
  
    block_
    
  ).execute()


