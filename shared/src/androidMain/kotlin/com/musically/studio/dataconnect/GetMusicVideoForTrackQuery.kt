
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


public interface GetMusicVideoForTrackQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetMusicVideoForTrackQuery.Data,
      GetMusicVideoForTrackQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val musicVideos: List<MusicVideosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class MusicVideosItem(
  
    val id: String,
  
    val videoUrl: String,
  
    val promptUsed: String?,
  
    val motionPreset: String?,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMusicVideoForTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMusicVideoForTrackQuery.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetMusicVideoForTrackQuery.Data,
    GetMusicVideoForTrackQuery.Variables
  > =
  ref(
    
      GetMusicVideoForTrackQuery.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun GetMusicVideoForTrackQuery.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMusicVideoForTrackQuery.Data,
    GetMusicVideoForTrackQuery.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


  public fun GetMusicVideoForTrackQuery.flow(
    
      trackId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetMusicVideoForTrackQuery.Data> =
    ref(
        
          trackId=trackId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

