
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



public interface UpsertUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val displayName: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val username: String,
  
    val email: String,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var displayName: String?
        public var username: String
        public var email: String
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          username: String,email: String,
          block_: Builder.() -> Unit
        ): Variables {
          var displayName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var username= username
            var email= email
            

          return object : Builder {
            override var displayName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { displayName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var username: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { username = value_ }
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              displayName=displayName,username=username,email=email,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_upsert: UserKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserMutation.ref(
  
    username: String,email: String,

  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      UpsertUserMutation.Variables.build(
        username=username,email=email,
  
    block_
      )
    
  )

public suspend fun UpsertUserMutation.execute(

  
    
      username: String,email: String,

  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      username=username,email=email,
  
    block_
    
  ).execute()


