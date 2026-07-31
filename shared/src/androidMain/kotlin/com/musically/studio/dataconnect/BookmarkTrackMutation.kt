
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



public interface BookmarkTrackMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      DefaultConnector,
      BookmarkTrackMutation.Data,
      BookmarkTrackMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val trackId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bookmarkedTrack_insert: BookmarkedTrackKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "BookmarkTrack"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun BookmarkTrackMutation.ref(
  
    trackId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    BookmarkTrackMutation.Data,
    BookmarkTrackMutation.Variables
  > =
  ref(
    
      BookmarkTrackMutation.Variables(
        trackId=trackId,
  
      )
    
  )

public suspend fun BookmarkTrackMutation.execute(

  
    
      trackId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    BookmarkTrackMutation.Data,
    BookmarkTrackMutation.Variables
  > =
  ref(
    
      trackId=trackId,
  
    
  ).execute()


