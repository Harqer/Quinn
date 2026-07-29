
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

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface DefaultConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<DefaultConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val addTrackToPlaylist: AddTrackToPlaylistMutation
  
    public val bookmarkTrack: BookmarkTrackMutation
  
    public val createPlaylist: CreatePlaylistMutation
  
    public val createPodcast: CreatePodcastMutation
  
    public val createTrack: CreateTrackMutation
  
    public val getAlbums: GetAlbumsQuery
  
    public val getAudiobooks: GetAudiobooksQuery
  
    public val getBookmarkedTracks: GetBookmarkedTracksQuery
  
    public val getCategories: GetCategoriesQuery
  
    public val getCommunityTracks: GetCommunityTracksQuery
  
    public val getLikedTracks: GetLikedTracksQuery
  
    public val getPaymentHistory: GetPaymentHistoryQuery
  
    public val getPlaylists: GetPlaylistsQuery
  
    public val getPodcasts: GetPodcastsQuery
  
    public val getUserSettings: GetUserSettingsQuery
  
    public val getUserTracks: GetUserTracksQuery
  
    public val likeTrack: LikeTrackMutation
  
    public val recordPayment: RecordPaymentMutation
  
    public val removeBookmarkedTrack: RemoveBookmarkedTrackMutation
  
    public val removeLikedTrack: RemoveLikedTrackMutation
  
    public val updateUserPreferences: UpdateUserPreferencesMutation
  
    public val upsertUser: UpsertUserMutation
  
    public val upsertUserSettings: UpsertUserSettingsMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "default",
      location = "us-central1",
      serviceId = "musically-studio",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):DefaultConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        DefaultConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, DefaultConnectorImpl>()

    
  }
}

public val DefaultConnector.Companion.instance:DefaultConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config
  ))

public fun DefaultConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):DefaultConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun DefaultConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):DefaultConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class DefaultConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : DefaultConnector {
  
    override val addTrackToPlaylist by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddTrackToPlaylistMutationImpl(this)
    }
  
    override val bookmarkTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      BookmarkTrackMutationImpl(this)
    }
  
    override val createPlaylist by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreatePlaylistMutationImpl(this)
    }
  
    override val createPodcast by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreatePodcastMutationImpl(this)
    }
  
    override val createTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateTrackMutationImpl(this)
    }
  
    override val getAlbums by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAlbumsQueryImpl(this)
    }
  
    override val getAudiobooks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAudiobooksQueryImpl(this)
    }
  
    override val getBookmarkedTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetBookmarkedTracksQueryImpl(this)
    }
  
    override val getCategories by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCategoriesQueryImpl(this)
    }
  
    override val getCommunityTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCommunityTracksQueryImpl(this)
    }
  
    override val getLikedTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetLikedTracksQueryImpl(this)
    }
  
    override val getPaymentHistory by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPaymentHistoryQueryImpl(this)
    }
  
    override val getPlaylists by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPlaylistsQueryImpl(this)
    }
  
    override val getPodcasts by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPodcastsQueryImpl(this)
    }
  
    override val getUserSettings by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserSettingsQueryImpl(this)
    }
  
    override val getUserTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserTracksQueryImpl(this)
    }
  
    override val likeTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      LikeTrackMutationImpl(this)
    }
  
    override val recordPayment by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RecordPaymentMutationImpl(this)
    }
  
    override val removeBookmarkedTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RemoveBookmarkedTrackMutationImpl(this)
    }
  
    override val removeLikedTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RemoveLikedTrackMutationImpl(this)
    }
  
    override val updateUserPreferences by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateUserPreferencesMutationImpl(this)
    }
  
    override val upsertUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserMutationImpl(this)
    }
  
    override val upsertUserSettings by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserSettingsMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<DefaultConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<DefaultConnector, *, *>> =
    listOf(
      addTrackToPlaylist,
        bookmarkTrack,
        createPlaylist,
        createPodcast,
        createTrack,
        likeTrack,
        recordPayment,
        removeBookmarkedTrack,
        removeLikedTrack,
        updateUserPreferences,
        upsertUser,
        upsertUserSettings,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<DefaultConnector, *, *>> =
    listOf(
      getAlbums,
        getAudiobooks,
        getBookmarkedTracks,
        getCategories,
        getCommunityTracks,
        getLikedTracks,
        getPaymentHistory,
        getPlaylists,
        getPodcasts,
        getUserSettings,
        getUserTracks,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    DefaultConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is DefaultConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "DefaultConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "DefaultConnectorImpl(dataConnect=$dataConnect)"
}



private open class DefaultConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: DefaultConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<DefaultConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: DefaultConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    DefaultConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    DefaultConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    DefaultConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is DefaultConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "DefaultConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "DefaultConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class DefaultConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: DefaultConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<DefaultConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: DefaultConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    DefaultConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    DefaultConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    DefaultConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is DefaultConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "DefaultConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "DefaultConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class AddTrackToPlaylistMutationImpl(
  connector: DefaultConnector
):
  AddTrackToPlaylistMutation,
  DefaultConnectorGeneratedMutationImpl<
      AddTrackToPlaylistMutation.Data,
      AddTrackToPlaylistMutation.Variables
  >(
    connector,
    AddTrackToPlaylistMutation.Companion.operationName,
    AddTrackToPlaylistMutation.Companion.dataDeserializer,
    AddTrackToPlaylistMutation.Companion.variablesSerializer,
  )


private class BookmarkTrackMutationImpl(
  connector: DefaultConnector
):
  BookmarkTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      BookmarkTrackMutation.Data,
      BookmarkTrackMutation.Variables
  >(
    connector,
    BookmarkTrackMutation.Companion.operationName,
    BookmarkTrackMutation.Companion.dataDeserializer,
    BookmarkTrackMutation.Companion.variablesSerializer,
  )


private class CreatePlaylistMutationImpl(
  connector: DefaultConnector
):
  CreatePlaylistMutation,
  DefaultConnectorGeneratedMutationImpl<
      CreatePlaylistMutation.Data,
      CreatePlaylistMutation.Variables
  >(
    connector,
    CreatePlaylistMutation.Companion.operationName,
    CreatePlaylistMutation.Companion.dataDeserializer,
    CreatePlaylistMutation.Companion.variablesSerializer,
  )


private class CreatePodcastMutationImpl(
  connector: DefaultConnector
):
  CreatePodcastMutation,
  DefaultConnectorGeneratedMutationImpl<
      CreatePodcastMutation.Data,
      CreatePodcastMutation.Variables
  >(
    connector,
    CreatePodcastMutation.Companion.operationName,
    CreatePodcastMutation.Companion.dataDeserializer,
    CreatePodcastMutation.Companion.variablesSerializer,
  )


private class CreateTrackMutationImpl(
  connector: DefaultConnector
):
  CreateTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      CreateTrackMutation.Data,
      CreateTrackMutation.Variables
  >(
    connector,
    CreateTrackMutation.Companion.operationName,
    CreateTrackMutation.Companion.dataDeserializer,
    CreateTrackMutation.Companion.variablesSerializer,
  )


private class GetAlbumsQueryImpl(
  connector: DefaultConnector
):
  GetAlbumsQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetAlbumsQuery.Data,
      Unit
  >(
    connector,
    GetAlbumsQuery.Companion.operationName,
    GetAlbumsQuery.Companion.dataDeserializer,
    GetAlbumsQuery.Companion.variablesSerializer,
  )


private class GetAudiobooksQueryImpl(
  connector: DefaultConnector
):
  GetAudiobooksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetAudiobooksQuery.Data,
      Unit
  >(
    connector,
    GetAudiobooksQuery.Companion.operationName,
    GetAudiobooksQuery.Companion.dataDeserializer,
    GetAudiobooksQuery.Companion.variablesSerializer,
  )


private class GetBookmarkedTracksQueryImpl(
  connector: DefaultConnector
):
  GetBookmarkedTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetBookmarkedTracksQuery.Data,
      Unit
  >(
    connector,
    GetBookmarkedTracksQuery.Companion.operationName,
    GetBookmarkedTracksQuery.Companion.dataDeserializer,
    GetBookmarkedTracksQuery.Companion.variablesSerializer,
  )


private class GetCategoriesQueryImpl(
  connector: DefaultConnector
):
  GetCategoriesQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetCategoriesQuery.Data,
      Unit
  >(
    connector,
    GetCategoriesQuery.Companion.operationName,
    GetCategoriesQuery.Companion.dataDeserializer,
    GetCategoriesQuery.Companion.variablesSerializer,
  )


private class GetCommunityTracksQueryImpl(
  connector: DefaultConnector
):
  GetCommunityTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetCommunityTracksQuery.Data,
      Unit
  >(
    connector,
    GetCommunityTracksQuery.Companion.operationName,
    GetCommunityTracksQuery.Companion.dataDeserializer,
    GetCommunityTracksQuery.Companion.variablesSerializer,
  )


private class GetLikedTracksQueryImpl(
  connector: DefaultConnector
):
  GetLikedTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetLikedTracksQuery.Data,
      Unit
  >(
    connector,
    GetLikedTracksQuery.Companion.operationName,
    GetLikedTracksQuery.Companion.dataDeserializer,
    GetLikedTracksQuery.Companion.variablesSerializer,
  )


private class GetPaymentHistoryQueryImpl(
  connector: DefaultConnector
):
  GetPaymentHistoryQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetPaymentHistoryQuery.Data,
      Unit
  >(
    connector,
    GetPaymentHistoryQuery.Companion.operationName,
    GetPaymentHistoryQuery.Companion.dataDeserializer,
    GetPaymentHistoryQuery.Companion.variablesSerializer,
  )


private class GetPlaylistsQueryImpl(
  connector: DefaultConnector
):
  GetPlaylistsQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetPlaylistsQuery.Data,
      Unit
  >(
    connector,
    GetPlaylistsQuery.Companion.operationName,
    GetPlaylistsQuery.Companion.dataDeserializer,
    GetPlaylistsQuery.Companion.variablesSerializer,
  )


private class GetPodcastsQueryImpl(
  connector: DefaultConnector
):
  GetPodcastsQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetPodcastsQuery.Data,
      Unit
  >(
    connector,
    GetPodcastsQuery.Companion.operationName,
    GetPodcastsQuery.Companion.dataDeserializer,
    GetPodcastsQuery.Companion.variablesSerializer,
  )


private class GetUserSettingsQueryImpl(
  connector: DefaultConnector
):
  GetUserSettingsQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetUserSettingsQuery.Data,
      Unit
  >(
    connector,
    GetUserSettingsQuery.Companion.operationName,
    GetUserSettingsQuery.Companion.dataDeserializer,
    GetUserSettingsQuery.Companion.variablesSerializer,
  )


private class GetUserTracksQueryImpl(
  connector: DefaultConnector
):
  GetUserTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetUserTracksQuery.Data,
      Unit
  >(
    connector,
    GetUserTracksQuery.Companion.operationName,
    GetUserTracksQuery.Companion.dataDeserializer,
    GetUserTracksQuery.Companion.variablesSerializer,
  )


private class LikeTrackMutationImpl(
  connector: DefaultConnector
):
  LikeTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      LikeTrackMutation.Data,
      LikeTrackMutation.Variables
  >(
    connector,
    LikeTrackMutation.Companion.operationName,
    LikeTrackMutation.Companion.dataDeserializer,
    LikeTrackMutation.Companion.variablesSerializer,
  )


private class RecordPaymentMutationImpl(
  connector: DefaultConnector
):
  RecordPaymentMutation,
  DefaultConnectorGeneratedMutationImpl<
      RecordPaymentMutation.Data,
      RecordPaymentMutation.Variables
  >(
    connector,
    RecordPaymentMutation.Companion.operationName,
    RecordPaymentMutation.Companion.dataDeserializer,
    RecordPaymentMutation.Companion.variablesSerializer,
  )


private class RemoveBookmarkedTrackMutationImpl(
  connector: DefaultConnector
):
  RemoveBookmarkedTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      RemoveBookmarkedTrackMutation.Data,
      RemoveBookmarkedTrackMutation.Variables
  >(
    connector,
    RemoveBookmarkedTrackMutation.Companion.operationName,
    RemoveBookmarkedTrackMutation.Companion.dataDeserializer,
    RemoveBookmarkedTrackMutation.Companion.variablesSerializer,
  )


private class RemoveLikedTrackMutationImpl(
  connector: DefaultConnector
):
  RemoveLikedTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      RemoveLikedTrackMutation.Data,
      RemoveLikedTrackMutation.Variables
  >(
    connector,
    RemoveLikedTrackMutation.Companion.operationName,
    RemoveLikedTrackMutation.Companion.dataDeserializer,
    RemoveLikedTrackMutation.Companion.variablesSerializer,
  )


private class UpdateUserPreferencesMutationImpl(
  connector: DefaultConnector
):
  UpdateUserPreferencesMutation,
  DefaultConnectorGeneratedMutationImpl<
      UpdateUserPreferencesMutation.Data,
      UpdateUserPreferencesMutation.Variables
  >(
    connector,
    UpdateUserPreferencesMutation.Companion.operationName,
    UpdateUserPreferencesMutation.Companion.dataDeserializer,
    UpdateUserPreferencesMutation.Companion.variablesSerializer,
  )


private class UpsertUserMutationImpl(
  connector: DefaultConnector
):
  UpsertUserMutation,
  DefaultConnectorGeneratedMutationImpl<
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
  >(
    connector,
    UpsertUserMutation.Companion.operationName,
    UpsertUserMutation.Companion.dataDeserializer,
    UpsertUserMutation.Companion.variablesSerializer,
  )


private class UpsertUserSettingsMutationImpl(
  connector: DefaultConnector
):
  UpsertUserSettingsMutation,
  DefaultConnectorGeneratedMutationImpl<
      UpsertUserSettingsMutation.Data,
      UpsertUserSettingsMutation.Variables
  >(
    connector,
    UpsertUserSettingsMutation.Companion.operationName,
    UpsertUserSettingsMutation.Companion.dataDeserializer,
    UpsertUserSettingsMutation.Companion.variablesSerializer,
  )


