
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


public interface GetUserCameraCapturesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetUserCameraCapturesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val cameraCaptures: List<CameraCapturesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class CameraCapturesItem(
  
    val id: String,
  
    val imageUrl: String?,
  
    val videoUrl: String?,
  
    val environmentData: String?,
  
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
    public val operationName: String = "GetUserCameraCaptures"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserCameraCapturesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserCameraCapturesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUserCameraCapturesQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserCameraCapturesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUserCameraCapturesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUserCameraCapturesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

