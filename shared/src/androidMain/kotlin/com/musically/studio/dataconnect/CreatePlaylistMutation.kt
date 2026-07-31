
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



public interface CreatePlaylistMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      CreatePlaylistMutation.Data,
      CreatePlaylistMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val name: String,
  
    val description: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var name: String
        public var description: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          name: String,
          block_: Builder.() -> Unit
        ): Variables {
          var name= name
            var description: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var description: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              name=name,description=description,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val playlist_insert: PlaylistKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreatePlaylist"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreatePlaylistMutation.ref(
  
    name: String,

  
    block_: CreatePlaylistMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreatePlaylistMutation.Data,
    CreatePlaylistMutation.Variables
  > =
  ref(
    
      CreatePlaylistMutation.Variables.build(
        name=name,
  
    block_
      )
    
  )

public suspend fun CreatePlaylistMutation.execute(

  
    
      name: String,

  
    block_: CreatePlaylistMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreatePlaylistMutation.Data,
    CreatePlaylistMutation.Variables
  > =
  ref(
    
      name=name,
  
    block_
    
  ).execute()


