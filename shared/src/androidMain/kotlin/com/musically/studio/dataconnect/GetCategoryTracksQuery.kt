
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


public interface GetCategoryTracksQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      DefaultConnector,
      GetCategoryTracksQuery.Data,
      GetCategoryTracksQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val categoryId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val musicCategories: List<MusicCategoriesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class MusicCategoriesItem(
  
    val track: Track,
  
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
    public val operationName: String = "GetCategoryTracks"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCategoryTracksQuery.ref(
  
    categoryId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetCategoryTracksQuery.Data,
    GetCategoryTracksQuery.Variables
  > =
  ref(
    
      GetCategoryTracksQuery.Variables(
        categoryId=categoryId,
  
      )
    
  )

public suspend fun GetCategoryTracksQuery.execute(

  
    
      categoryId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetCategoryTracksQuery.Data,
    GetCategoryTracksQuery.Variables
  > =
  ref(
    
      categoryId=categoryId,
  
    
  ).execute()


  public fun GetCategoryTracksQuery.flow(
    
      categoryId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetCategoryTracksQuery.Data> =
    ref(
        
          categoryId=categoryId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

