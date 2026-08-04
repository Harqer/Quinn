
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



public interface CreateTrackMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreateTrackMutation.Data,
      CreateTrackMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val title: String,
  
    val albumId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val audioUrl: String,
  
    val coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val prompt: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val isCommunity: Boolean,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var title: String
        public var albumId: String?
        public var audioUrl: String
        public var coverUrl: String?
        public var durationMs: Int?
        public var prompt: String?
        public var isCommunity: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          title: String,audioUrl: String,isCommunity: Boolean,
          block_: Builder.() -> Unit
        ): Variables {
          var title= title
            var albumId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var audioUrl= audioUrl
            var coverUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var prompt: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var isCommunity= isCommunity
            

          return object : Builder {
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var albumId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { albumId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var audioUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audioUrl = value_ }
              
            override var coverUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { coverUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var durationMs: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { durationMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var prompt: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { prompt = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var isCommunity: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isCommunity = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              title=title,albumId=albumId,audioUrl=audioUrl,coverUrl=coverUrl,durationMs=durationMs,prompt=prompt,isCommunity=isCommunity,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val track_insert: TrackKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateTrackMutation.ref(
  
    title: String,audioUrl: String,isCommunity: Boolean,

  
    block_: CreateTrackMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateTrackMutation.Data,
    CreateTrackMutation.Variables
  > =
  ref(
    
      CreateTrackMutation.Variables.build(
        title=title,audioUrl=audioUrl,isCommunity=isCommunity,
  
    block_
      )
    
  )

public suspend fun CreateTrackMutation.execute(

  
    
      title: String,audioUrl: String,isCommunity: Boolean,

  
    block_: CreateTrackMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateTrackMutation.Data,
    CreateTrackMutation.Variables
  > =
  ref(
    
      title=title,audioUrl=audioUrl,isCommunity=isCommunity,
  
    block_
    
  ).execute()


