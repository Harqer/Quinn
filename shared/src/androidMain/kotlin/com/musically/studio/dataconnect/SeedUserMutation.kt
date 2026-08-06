
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



public interface SeedUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      SeedUserMutation.Data,
      SeedUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val uid: String,
  
    val username: String,
  
    val email: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_upsert: UserKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SeedUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SeedUserMutation.ref(
  
    uid: String,username: String,email: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    SeedUserMutation.Data,
    SeedUserMutation.Variables
  > =
  ref(
    
      SeedUserMutation.Variables(
        uid=uid,username=username,email=email,
  
      )
    
  )

public suspend fun SeedUserMutation.execute(

  
    
      uid: String,username: String,email: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    SeedUserMutation.Data,
    SeedUserMutation.Variables
  > =
  ref(
    
      uid=uid,username=username,email=email,
  
    
  ).execute()


