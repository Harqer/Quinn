
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


public interface GetCoverArtForTrackQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetCoverArtForTrackQuery.Data,
      GetCoverArtForTrackQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val coverArts: List<CoverArtsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class CoverArtsItem(
  
    val id: String,
  
    val imageUrl: String,
  
    val promptUsed: String?,
  
    val stylePreset: String?,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetCoverArtForTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCoverArtForTrackQuery.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetCoverArtForTrackQuery.Data,
    GetCoverArtForTrackQuery.Variables
  > =
  ref(
    
      GetCoverArtForTrackQuery.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun GetCoverArtForTrackQuery.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetCoverArtForTrackQuery.Data,
    GetCoverArtForTrackQuery.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


  public fun GetCoverArtForTrackQuery.flow(
    
      trackId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetCoverArtForTrackQuery.Data> =
    ref(
        
          trackId=trackId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

