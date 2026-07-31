
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


public interface GetBookmarkedTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetBookmarkedTracksQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bookmarkedTracks: List<BookmarkedTracksItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class BookmarkedTracksItem(
  
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
    public val operationName: String = "GetBookmarkedTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetBookmarkedTracksQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetBookmarkedTracksQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetBookmarkedTracksQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetBookmarkedTracksQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetBookmarkedTracksQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetBookmarkedTracksQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

