
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


public interface SearchTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      SearchTracksQuery.Data,
      SearchTracksQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val query: String,
  
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
  

  public companion object {
    public val operationName: String = "SearchTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SearchTracksQuery.ref(
  
    query: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    SearchTracksQuery.Data,
    SearchTracksQuery.Variables
  > =
  ref(
    
      SearchTracksQuery.Variables(
        query=query,
  
      )
    
  )

public suspend fun SearchTracksQuery.execute(

  
    
      query: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    SearchTracksQuery.Data,
    SearchTracksQuery.Variables
  > =
  ref(
    
      query=query,
  
    
  ).execute()


  public fun SearchTracksQuery.flow(
    
      query: String,

  
    
    ): kotlinx.coroutines.flow.Flow<SearchTracksQuery.Data> =
    ref(
        
          query=query,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

