
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
  
    val name: String,
  
    val artistName: String,
  
    val albumName: String,
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val isCommunity: Boolean,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var name: String
        public var artistName: String
        public var albumName: String
        public var imageUrl: String?
        public var isCommunity: Boolean
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          name: String,artistName: String,albumName: String,isCommunity: Boolean,
          block_: Builder.() -> Unit
        ): Variables {
          var name= name
            var artistName= artistName
            var albumName= albumName
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var isCommunity= isCommunity
            

          return object : Builder {
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var artistName: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { artistName = value_ }
              
            override var albumName: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { albumName = value_ }
              
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var isCommunity: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isCommunity = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              name=name,artistName=artistName,albumName=albumName,imageUrl=imageUrl,isCommunity=isCommunity,
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
  
    name: String,artistName: String,albumName: String,isCommunity: Boolean,

  
    block_: CreateTrackMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateTrackMutation.Data,
    CreateTrackMutation.Variables
  > =
  ref(
    
      CreateTrackMutation.Variables.build(
        name=name,artistName=artistName,albumName=albumName,isCommunity=isCommunity,
  
    block_
      )
    
  )

public suspend fun CreateTrackMutation.execute(

  
    
      name: String,artistName: String,albumName: String,isCommunity: Boolean,

  
    block_: CreateTrackMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateTrackMutation.Data,
    CreateTrackMutation.Variables
  > =
  ref(
    
      name=name,artistName=artistName,albumName=albumName,isCommunity=isCommunity,
  
    block_
    
  ).execute()


