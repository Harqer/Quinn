
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


public interface GetTrackQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetTrackQuery.Data,
      GetTrackQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val track: Track?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Track(
  
    val id: String,
  
    val title: String,
  
    val album: Album?,
  
    val durationMs: Int,
  
    val audioUrl: String,
  
    val coverUrl: String?,
  
    val playCount: Long,
  
    val prompt: String?,
  
    val isCommunity: Boolean,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Album(
  
    val id: String,
  
    val title: String,
  
    val primaryArtist: PrimaryArtist,
  
    val coverUrl: String?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class PrimaryArtist(
  
    val id: String,
  
    val name: String,
  
    val imageUrl: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetTrackQuery.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetTrackQuery.Data,
    GetTrackQuery.Variables
  > =
  ref(
    
      GetTrackQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetTrackQuery.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetTrackQuery.Data,
    GetTrackQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetTrackQuery.flow(
    
      id: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetTrackQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

