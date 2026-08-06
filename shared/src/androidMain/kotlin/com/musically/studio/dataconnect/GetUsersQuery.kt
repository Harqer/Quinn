
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetUsersQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetUsersQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val users: List<UsersItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class UsersItem(
  
    val uid: String,
  
    val username: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUsers"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUsersQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUsersQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUsersQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUsersQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUsersQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUsersQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

