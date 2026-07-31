
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


public interface GetPlaylistsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetPlaylistsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val playlists: List<PlaylistsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class PlaylistsItem(
  
    val id: String,
  
    val name: String,
  
    val description: String?,
  
    val coverUrl: String?,
  
    val isPublic: Boolean,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetPlaylists"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetPlaylistsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetPlaylistsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetPlaylistsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetPlaylistsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetPlaylistsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetPlaylistsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

