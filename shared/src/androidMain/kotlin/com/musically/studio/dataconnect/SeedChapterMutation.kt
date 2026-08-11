
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



public interface SeedChapterMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedChapterMutation.Data,
      SeedChapterMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val audiobookId: String,
  
    val title: String,
  
    val chapterNumber: Int,
  
    val audioUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var audiobookId: String
        public var title: String
        public var chapterNumber: Int
        public var audioUrl: String?
        public var durationMs: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          audiobookId: String,title: String,chapterNumber: Int,
          block_: Builder.() -> Unit
        ): Variables {
          var audiobookId= audiobookId
            var title= title
            var chapterNumber= chapterNumber
            var audioUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var durationMs: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var audiobookId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audiobookId = value_ }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var chapterNumber: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { chapterNumber = value_ }
              
            override var audioUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audioUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var durationMs: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { durationMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              audiobookId=audiobookId,title=title,chapterNumber=chapterNumber,audioUrl=audioUrl,durationMs=durationMs,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val chapter_insert: ChapterKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedChapter"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedChapterMutation.ref(
  
    audiobookId: String,title: String,chapterNumber: Int,

  
    block_: SeedChapterMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SeedChapterMutation.Data,
    SeedChapterMutation.Variables
  > =
  ref(
    
      SeedChapterMutation.Variables.build(
        audiobookId=audiobookId,title=title,chapterNumber=chapterNumber,
  
    block_
      )
    
  )

public suspend fun SeedChapterMutation.execute(

  
    
      audiobookId: String,title: String,chapterNumber: Int,

  
    block_: SeedChapterMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SeedChapterMutation.Data,
    SeedChapterMutation.Variables
  > =
  ref(
    
      audiobookId=audiobookId,title=title,chapterNumber=chapterNumber,
  
    block_
    
  ).execute()


