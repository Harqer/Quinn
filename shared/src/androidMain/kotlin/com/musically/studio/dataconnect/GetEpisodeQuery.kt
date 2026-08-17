
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


public interface GetEpisodeQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetEpisodeQuery.Data,
      GetEpisodeQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val episode: Episode?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Episode(
  
    val id: String,
  
    val title: String,
  
    val description: String?,
  
    val publishDate: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val durationMs: Int,
  
    val audioUrl: String,
  
    val coverUrl: String?,
  
    val show: Show,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Show(
  
    val id: String,
  
    val title: String,
  
    val publisher: String,
  
    val coverUrl: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetEpisode"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetEpisodeQuery.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetEpisodeQuery.Data,
    GetEpisodeQuery.Variables
  > =
  ref(
    
      GetEpisodeQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetEpisodeQuery.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetEpisodeQuery.Data,
    GetEpisodeQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetEpisodeQuery.flow(
    
      id: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetEpisodeQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

