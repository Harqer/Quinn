
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


public interface GetLikedTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetLikedTracksQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val likedTracks: List<LikedTracksItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class LikedTracksItem(
  
    val track: Track,
  
    val addedAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Track(
  
    val id: String,
  
    val title: String,
  
    val album: Album,
  
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
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetLikedTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetLikedTracksQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetLikedTracksQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetLikedTracksQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetLikedTracksQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetLikedTracksQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetLikedTracksQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

