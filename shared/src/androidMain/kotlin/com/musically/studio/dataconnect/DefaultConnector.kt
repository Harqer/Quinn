
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

  
    public val addChatMessage: AddChatMessageMutation
  
    public val addTrackToPlaylist: AddTrackToPlaylistMutation
  
    public val bookmarkTrack: BookmarkTrackMutation
  
    public val createCameraCapture: CreateCameraCaptureMutation
  
    public val createPlaylist: CreatePlaylistMutation
  
    public val createPodcast: CreatePodcastMutation
  
    public val createTrack: CreateTrackMutation
  
    public val createVideoDigestion: CreateVideoDigestionMutation
  
    public val getAlbumTracks: GetAlbumTracksQuery
  
    public val getAlbums: GetAlbumsQuery
  
    public val getAudiobooks: GetAudiobooksQuery
  
    public val getBookmarkedTracks: GetBookmarkedTracksQuery
  
    public val getCategories: GetCategoriesQuery
  
    public val getCategoryTracks: GetCategoryTracksQuery
  
    public val getCommunityTracks: GetCommunityTracksQuery
  
    public val getLikedTracks: GetLikedTracksQuery
  
    public val getPaymentHistory: GetPaymentHistoryQuery
  
    public val getPlaylistTracks: GetPlaylistTracksQuery
  
    public val getPlaylists: GetPlaylistsQuery
  
    public val getPodcasts: GetPodcastsQuery
  
    public val getTrack: GetTrackQuery
  
    public val getUserCameraCaptures: GetUserCameraCapturesQuery
  
    public val getUserSettings: GetUserSettingsQuery
  
    public val getUserTracks: GetUserTracksQuery
  
    public val getUserVideoDigestions: GetUserVideoDigestionsQuery
  
    public val getUsers: GetUsersQuery
  
    public val likeTrack: LikeTrackMutation
  
    public val listAiPresets: ListAiPresetsQuery
  
    public val listChatMessages: ListChatMessagesQuery
  
    public val listFaqItems: ListFaqItemsQuery
  
    public val listFeatureHighlights: ListFeatureHighlightsQuery
  
    public val listHomeSections: ListHomeSectionsQuery
  
    public val listSubscriptionPlans: ListSubscriptionPlansQuery
  
    public val recordPayment: RecordPaymentMutation
  
    public val removeBookmarkedTrack: RemoveBookmarkedTrackMutation
  
    public val removeLikedTrack: RemoveLikedTrackMutation
  
    public val searchTracks: SearchTracksQuery
  
    public val seedTrack: SeedTrackMutation
  
    public val seedUser: SeedUserMutation
  
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
  
    override val addChatMessage by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddChatMessageMutationImpl(this)
    }
  
    override val addTrackToPlaylist by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddTrackToPlaylistMutationImpl(this)
    }
  
    override val bookmarkTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      BookmarkTrackMutationImpl(this)
    }
  
    override val createCameraCapture by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateCameraCaptureMutationImpl(this)
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
  
    override val createVideoDigestion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateVideoDigestionMutationImpl(this)
    }
  
    override val getAlbumTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAlbumTracksQueryImpl(this)
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
  
    override val getCategoryTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCategoryTracksQueryImpl(this)
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
  
    override val getPlaylistTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPlaylistTracksQueryImpl(this)
    }
  
    override val getPlaylists by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPlaylistsQueryImpl(this)
    }
  
    override val getPodcasts by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPodcastsQueryImpl(this)
    }
  
    override val getTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetTrackQueryImpl(this)
    }
  
    override val getUserCameraCaptures by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserCameraCapturesQueryImpl(this)
    }
  
    override val getUserSettings by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserSettingsQueryImpl(this)
    }
  
    override val getUserTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserTracksQueryImpl(this)
    }
  
    override val getUserVideoDigestions by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserVideoDigestionsQueryImpl(this)
    }
  
    override val getUsers by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUsersQueryImpl(this)
    }
  
    override val likeTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      LikeTrackMutationImpl(this)
    }
  
    override val listAiPresets by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListAiPresetsQueryImpl(this)
    }
  
    override val listChatMessages by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListChatMessagesQueryImpl(this)
    }
  
    override val listFaqItems by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListFaqItemsQueryImpl(this)
    }
  
    override val listFeatureHighlights by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListFeatureHighlightsQueryImpl(this)
    }
  
    override val listHomeSections by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListHomeSectionsQueryImpl(this)
    }
  
    override val listSubscriptionPlans by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListSubscriptionPlansQueryImpl(this)
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
  
    override val searchTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SearchTracksQueryImpl(this)
    }
  
    override val seedTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SeedTrackMutationImpl(this)
    }
  
    override val seedUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SeedUserMutationImpl(this)
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
      addChatMessage,
        addTrackToPlaylist,
        bookmarkTrack,
        createCameraCapture,
        createPlaylist,
        createPodcast,
        createTrack,
        createVideoDigestion,
        likeTrack,
        recordPayment,
        removeBookmarkedTrack,
        removeLikedTrack,
        seedTrack,
        seedUser,
        updateUserPreferences,
        upsertUser,
        upsertUserSettings,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<DefaultConnector, *, *>> =
    listOf(
      getAlbumTracks,
        getAlbums,
        getAudiobooks,
        getBookmarkedTracks,
        getCategories,
        getCategoryTracks,
        getCommunityTracks,
        getLikedTracks,
        getPaymentHistory,
        getPlaylistTracks,
        getPlaylists,
        getPodcasts,
        getTrack,
        getUserCameraCaptures,
        getUserSettings,
        getUserTracks,
        getUserVideoDigestions,
        getUsers,
        listAiPresets,
        listChatMessages,
        listFaqItems,
        listFeatureHighlights,
        listHomeSections,
        listSubscriptionPlans,
        searchTracks,
        
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



private class AddChatMessageMutationImpl(
  connector: DefaultConnector
):
  AddChatMessageMutation,
  DefaultConnectorGeneratedMutationImpl<
      AddChatMessageMutation.Data,
      AddChatMessageMutation.Variables
  >(
    connector,
    AddChatMessageMutation.Companion.operationName,
    AddChatMessageMutation.Companion.dataDeserializer,
    AddChatMessageMutation.Companion.variablesSerializer,
  )


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


private class CreateCameraCaptureMutationImpl(
  connector: DefaultConnector
):
  CreateCameraCaptureMutation,
  DefaultConnectorGeneratedMutationImpl<
      CreateCameraCaptureMutation.Data,
      CreateCameraCaptureMutation.Variables
  >(
    connector,
    CreateCameraCaptureMutation.Companion.operationName,
    CreateCameraCaptureMutation.Companion.dataDeserializer,
    CreateCameraCaptureMutation.Companion.variablesSerializer,
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


private class CreateVideoDigestionMutationImpl(
  connector: DefaultConnector
):
  CreateVideoDigestionMutation,
  DefaultConnectorGeneratedMutationImpl<
      CreateVideoDigestionMutation.Data,
      CreateVideoDigestionMutation.Variables
  >(
    connector,
    CreateVideoDigestionMutation.Companion.operationName,
    CreateVideoDigestionMutation.Companion.dataDeserializer,
    CreateVideoDigestionMutation.Companion.variablesSerializer,
  )


private class GetAlbumTracksQueryImpl(
  connector: DefaultConnector
):
  GetAlbumTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetAlbumTracksQuery.Data,
      GetAlbumTracksQuery.Variables
  >(
    connector,
    GetAlbumTracksQuery.Companion.operationName,
    GetAlbumTracksQuery.Companion.dataDeserializer,
    GetAlbumTracksQuery.Companion.variablesSerializer,
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


private class GetCategoryTracksQueryImpl(
  connector: DefaultConnector
):
  GetCategoryTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetCategoryTracksQuery.Data,
      GetCategoryTracksQuery.Variables
  >(
    connector,
    GetCategoryTracksQuery.Companion.operationName,
    GetCategoryTracksQuery.Companion.dataDeserializer,
    GetCategoryTracksQuery.Companion.variablesSerializer,
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


private class GetPlaylistTracksQueryImpl(
  connector: DefaultConnector
):
  GetPlaylistTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetPlaylistTracksQuery.Data,
      GetPlaylistTracksQuery.Variables
  >(
    connector,
    GetPlaylistTracksQuery.Companion.operationName,
    GetPlaylistTracksQuery.Companion.dataDeserializer,
    GetPlaylistTracksQuery.Companion.variablesSerializer,
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


private class GetTrackQueryImpl(
  connector: DefaultConnector
):
  GetTrackQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetTrackQuery.Data,
      GetTrackQuery.Variables
  >(
    connector,
    GetTrackQuery.Companion.operationName,
    GetTrackQuery.Companion.dataDeserializer,
    GetTrackQuery.Companion.variablesSerializer,
  )


private class GetUserCameraCapturesQueryImpl(
  connector: DefaultConnector
):
  GetUserCameraCapturesQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetUserCameraCapturesQuery.Data,
      Unit
  >(
    connector,
    GetUserCameraCapturesQuery.Companion.operationName,
    GetUserCameraCapturesQuery.Companion.dataDeserializer,
    GetUserCameraCapturesQuery.Companion.variablesSerializer,
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


private class GetUserVideoDigestionsQueryImpl(
  connector: DefaultConnector
):
  GetUserVideoDigestionsQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetUserVideoDigestionsQuery.Data,
      Unit
  >(
    connector,
    GetUserVideoDigestionsQuery.Companion.operationName,
    GetUserVideoDigestionsQuery.Companion.dataDeserializer,
    GetUserVideoDigestionsQuery.Companion.variablesSerializer,
  )


private class GetUsersQueryImpl(
  connector: DefaultConnector
):
  GetUsersQuery,
  DefaultConnectorGeneratedQueryImpl<
      GetUsersQuery.Data,
      Unit
  >(
    connector,
    GetUsersQuery.Companion.operationName,
    GetUsersQuery.Companion.dataDeserializer,
    GetUsersQuery.Companion.variablesSerializer,
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


private class ListAiPresetsQueryImpl(
  connector: DefaultConnector
):
  ListAiPresetsQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListAiPresetsQuery.Data,
      Unit
  >(
    connector,
    ListAiPresetsQuery.Companion.operationName,
    ListAiPresetsQuery.Companion.dataDeserializer,
    ListAiPresetsQuery.Companion.variablesSerializer,
  )


private class ListChatMessagesQueryImpl(
  connector: DefaultConnector
):
  ListChatMessagesQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListChatMessagesQuery.Data,
      ListChatMessagesQuery.Variables
  >(
    connector,
    ListChatMessagesQuery.Companion.operationName,
    ListChatMessagesQuery.Companion.dataDeserializer,
    ListChatMessagesQuery.Companion.variablesSerializer,
  )


private class ListFaqItemsQueryImpl(
  connector: DefaultConnector
):
  ListFaqItemsQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListFaqItemsQuery.Data,
      Unit
  >(
    connector,
    ListFaqItemsQuery.Companion.operationName,
    ListFaqItemsQuery.Companion.dataDeserializer,
    ListFaqItemsQuery.Companion.variablesSerializer,
  )


private class ListFeatureHighlightsQueryImpl(
  connector: DefaultConnector
):
  ListFeatureHighlightsQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListFeatureHighlightsQuery.Data,
      Unit
  >(
    connector,
    ListFeatureHighlightsQuery.Companion.operationName,
    ListFeatureHighlightsQuery.Companion.dataDeserializer,
    ListFeatureHighlightsQuery.Companion.variablesSerializer,
  )


private class ListHomeSectionsQueryImpl(
  connector: DefaultConnector
):
  ListHomeSectionsQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListHomeSectionsQuery.Data,
      Unit
  >(
    connector,
    ListHomeSectionsQuery.Companion.operationName,
    ListHomeSectionsQuery.Companion.dataDeserializer,
    ListHomeSectionsQuery.Companion.variablesSerializer,
  )


private class ListSubscriptionPlansQueryImpl(
  connector: DefaultConnector
):
  ListSubscriptionPlansQuery,
  DefaultConnectorGeneratedQueryImpl<
      ListSubscriptionPlansQuery.Data,
      Unit
  >(
    connector,
    ListSubscriptionPlansQuery.Companion.operationName,
    ListSubscriptionPlansQuery.Companion.dataDeserializer,
    ListSubscriptionPlansQuery.Companion.variablesSerializer,
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


private class SearchTracksQueryImpl(
  connector: DefaultConnector
):
  SearchTracksQuery,
  DefaultConnectorGeneratedQueryImpl<
      SearchTracksQuery.Data,
      SearchTracksQuery.Variables
  >(
    connector,
    SearchTracksQuery.Companion.operationName,
    SearchTracksQuery.Companion.dataDeserializer,
    SearchTracksQuery.Companion.variablesSerializer,
  )


private class SeedTrackMutationImpl(
  connector: DefaultConnector
):
  SeedTrackMutation,
  DefaultConnectorGeneratedMutationImpl<
      SeedTrackMutation.Data,
      SeedTrackMutation.Variables
  >(
    connector,
    SeedTrackMutation.Companion.operationName,
    SeedTrackMutation.Companion.dataDeserializer,
    SeedTrackMutation.Companion.variablesSerializer,
  )


private class SeedUserMutationImpl(
  connector: DefaultConnector
):
  SeedUserMutation,
  DefaultConnectorGeneratedMutationImpl<
      SeedUserMutation.Data,
      SeedUserMutation.Variables
  >(
    connector,
    SeedUserMutation.Companion.operationName,
    SeedUserMutation.Companion.dataDeserializer,
    SeedUserMutation.Companion.variablesSerializer,
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


