
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


public interface GetUserSettingsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetUserSettingsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userSettings: UserSettings?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class UserSettings(
  
    val userId: String,
  
    val isPremium: Boolean,
  
    val theme: String,
  
    val parentalControlsEnabled: Boolean,
  
    val stripeCustomerId: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserSettings"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserSettingsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserSettingsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUserSettingsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserSettingsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUserSettingsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUserSettingsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

