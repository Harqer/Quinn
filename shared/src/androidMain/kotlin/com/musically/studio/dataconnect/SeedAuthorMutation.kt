
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



public interface SeedAuthorMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedAuthorMutation.Data,
      SeedAuthorMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val name: String,
  
    val bio: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var name: String
        public var bio: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,name: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var name= name
            var bio: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var bio: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { bio = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,name=name,bio=bio,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val author_upsert: AuthorKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedAuthor"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedAuthorMutation.ref(
  
    id: String,name: String,

  
    block_: SeedAuthorMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SeedAuthorMutation.Data,
    SeedAuthorMutation.Variables
  > =
  ref(
    
      SeedAuthorMutation.Variables.build(
        id=id,name=name,
  
    block_
      )
    
  )

public suspend fun SeedAuthorMutation.execute(

  
    
      id: String,name: String,

  
    block_: SeedAuthorMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SeedAuthorMutation.Data,
    SeedAuthorMutation.Variables
  > =
  ref(
    
      id=id,name=name,
  
    block_
    
  ).execute()


