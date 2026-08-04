
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


public interface GetAlbumTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetAlbumTracksQuery.Data,
      GetAlbumTracksQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val albumId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tracks: List<TracksItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TracksItem(
  
    val id: String,
  
    val title: String,
  
    val album: Album?,
  
    val coverUrl: String?,
  
    val audioUrl: String,
  
    val prompt: String?,
  
    val visibility: String,
  
    val isCommunity: Boolean,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Album(
  
    val id: String,
  
    val title: String,
  
    val primaryArtist: PrimaryArtist,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class PrimaryArtist(
  
    val id: String,
  
    val name: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetAlbumTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAlbumTracksQuery.ref(
  
    albumId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetAlbumTracksQuery.Data,
    GetAlbumTracksQuery.Variables
  > =
  ref(
    
      GetAlbumTracksQuery.Variables(
        albumId=albumId,
  
      )
    
  )

public suspend fun GetAlbumTracksQuery.execute(

  
    
      albumId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetAlbumTracksQuery.Data,
    GetAlbumTracksQuery.Variables
  > =
  ref(
    
      albumId=albumId,
  
    
  ).execute()


  public fun GetAlbumTracksQuery.flow(
    
      albumId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetAlbumTracksQuery.Data> =
    ref(
        
          albumId=albumId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

