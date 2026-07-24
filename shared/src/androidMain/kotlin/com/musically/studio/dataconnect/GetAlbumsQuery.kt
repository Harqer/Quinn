
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


public interface GetAlbumsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetAlbumsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val albums: List<AlbumsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class AlbumsItem(
  
    val id: String,
  
    val name: String,
  
    val artistName: String,
  
    val imageUrl: String?,
  
    val releaseYear: Int?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetAlbums"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAlbumsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetAlbumsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetAlbumsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetAlbumsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetAlbumsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetAlbumsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

