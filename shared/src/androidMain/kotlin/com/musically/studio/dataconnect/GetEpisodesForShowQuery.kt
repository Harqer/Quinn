
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


public interface GetEpisodesForShowQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetEpisodesForShowQuery.Data,
      GetEpisodesForShowQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val showId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val episodes: List<EpisodesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class EpisodesItem(
  
    val id: String,
  
    val title: String,
  
    val description: String?,
  
    val publishDate: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val durationMs: Int,
  
    val audioUrl: String,
  
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
    public val operationName: String = "GetEpisodesForShow"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetEpisodesForShowQuery.ref(
  
    showId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetEpisodesForShowQuery.Data,
    GetEpisodesForShowQuery.Variables
  > =
  ref(
    
      GetEpisodesForShowQuery.Variables(
        showId=showId,
  
      )
    
  )

public suspend fun GetEpisodesForShowQuery.execute(

  
    
      showId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetEpisodesForShowQuery.Data,
    GetEpisodesForShowQuery.Variables
  > =
  ref(
    
      showId=showId,
  
    
  ).execute()


  public fun GetEpisodesForShowQuery.flow(
    
      showId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetEpisodesForShowQuery.Data> =
    ref(
        
          showId=showId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

