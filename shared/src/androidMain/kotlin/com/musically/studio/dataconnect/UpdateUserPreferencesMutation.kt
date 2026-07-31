
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



public interface UpdateUserPreferencesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      UpdateUserPreferencesMutation.Data,
      UpdateUserPreferencesMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val favoriteArtists: com.google.firebase.dataconnect.OptionalVariable<List<String>?>,
  
    val favoriteGenres: com.google.firebase.dataconnect.OptionalVariable<List<String>?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var favoriteArtists: List<String>?
        public var favoriteGenres: List<String>?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var favoriteArtists: com.google.firebase.dataconnect.OptionalVariable<List<String>?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var favoriteGenres: com.google.firebase.dataconnect.OptionalVariable<List<String>?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var favoriteArtists: List<String>?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { favoriteArtists = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var favoriteGenres: List<String>?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { favoriteGenres = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              favoriteArtists=favoriteArtists,favoriteGenres=favoriteGenres,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_update: UserKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateUserPreferences"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateUserPreferencesMutation.ref(
  
    

  
    block_: UpdateUserPreferencesMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateUserPreferencesMutation.Data,
    UpdateUserPreferencesMutation.Variables
  > =
  ref(
    
      UpdateUserPreferencesMutation.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun UpdateUserPreferencesMutation.execute(

  
    
      

  
    block_: UpdateUserPreferencesMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateUserPreferencesMutation.Data,
    UpdateUserPreferencesMutation.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


