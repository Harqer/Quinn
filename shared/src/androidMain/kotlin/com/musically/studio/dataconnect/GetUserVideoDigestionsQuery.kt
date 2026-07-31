
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


public interface GetUserVideoDigestionsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetUserVideoDigestionsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val videoDigestions: List<VideoDigestionsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class VideoDigestionsItem(
  
    val id: String,
  
    val videoUrl: String,
  
    val extractedAudioUrl: String?,
  
    val atmosphereSummary: String?,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val generatedTrack: GeneratedTrack?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class GeneratedTrack(
  
    val id: String,
  
    val title: String,
  
    val coverUrl: String?,
  
    val audioUrl: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserVideoDigestions"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserVideoDigestionsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserVideoDigestionsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUserVideoDigestionsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserVideoDigestionsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUserVideoDigestionsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUserVideoDigestionsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

