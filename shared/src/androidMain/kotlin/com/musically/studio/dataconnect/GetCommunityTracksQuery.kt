
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


public interface GetCommunityTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetCommunityTracksQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tracks: List<TracksItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TracksItem(
  
    val id: String,
  
    val name: String,
  
    val artistName: String,
  
    val albumName: String,
  
    val imageUrl: String?,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val owner: Owner,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Owner(
  
    val uid: String,
  
    val displayName: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetCommunityTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCommunityTracksQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetCommunityTracksQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetCommunityTracksQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetCommunityTracksQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetCommunityTracksQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetCommunityTracksQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

