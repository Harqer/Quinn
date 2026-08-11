
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



public interface SeedEpisodeMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedEpisodeMutation.Data,
      SeedEpisodeMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val showId: String,
  
    val title: String,
  
    val description: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val audioUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val publishDate: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var showId: String
        public var title: String
        public var description: String?
        public var audioUrl: String?
        public var durationMs: Int?
        public var publishDate: com.google.firebase.Timestamp
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          showId: String,title: String,publishDate: com.google.firebase.Timestamp,
          block_: Builder.() -> Unit
        ): Variables {
          var showId= showId
            var title= title
            var description: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var audioUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var publishDate= publishDate
            

          return object : Builder {
            override var showId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { showId = value_ }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var description: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var audioUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audioUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var durationMs: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { durationMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var publishDate: com.google.firebase.Timestamp
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { publishDate = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              showId=showId,title=title,description=description,audioUrl=audioUrl,durationMs=durationMs,publishDate=publishDate,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val episode_insert: EpisodeKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedEpisode"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedEpisodeMutation.ref(
  
    showId: String,title: String,publishDate: com.google.firebase.Timestamp,

  
    block_: SeedEpisodeMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SeedEpisodeMutation.Data,
    SeedEpisodeMutation.Variables
  > =
  ref(
    
      SeedEpisodeMutation.Variables.build(
        showId=showId,title=title,publishDate=publishDate,
  
    block_
      )
    
  )

public suspend fun SeedEpisodeMutation.execute(

  
    
      showId: String,title: String,publishDate: com.google.firebase.Timestamp,

  
    block_: SeedEpisodeMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SeedEpisodeMutation.Data,
    SeedEpisodeMutation.Variables
  > =
  ref(
    
      showId=showId,title=title,publishDate=publishDate,
  
    block_
    
  ).execute()


