
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

  
    public val createTrack: CreateTrackMutation
  
    public val getAlbums: GetAlbumsQuery
  
    public val getAudiobooks: GetAudiobooksQuery
  
    public val getCategories: GetCategoriesQuery
  
    public val getCommunityTracks: GetCommunityTracksQuery
  
    public val getPlaylists: GetPlaylistsQuery
  
    public val getPodcasts: GetPodcastsQuery
  
    public val getUserTracks: GetUserTracksQuery
  
    public val upsertUser: UpsertUserMutation
  

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
  
    override val createTrack by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateTrackMutationImpl(this)
    }
  
    override val getAlbums by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAlbumsQueryImpl(this)
    }
  
    override val getAudiobooks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAudiobooksQueryImpl(this)
    }
  
    override val getCategories by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCategoriesQueryImpl(this)
    }
  
    override val getCommunityTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCommunityTracksQueryImpl(this)
    }
  
    override val getPlaylists by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPlaylistsQueryImpl(this)
    }
  
    override val getPodcasts by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPodcastsQueryImpl(this)
    }
  
    override val getUserTracks by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserTracksQueryImpl(this)
    }
  
    override val upsertUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<DefaultConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<DefaultConnector, *, *>> =
    listOf(
      createTrack,
        upsertUser,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<DefaultConnector, *, *>> =
    listOf(
      getAlbums,
        getAudiobooks,
        getCategories,
        getCommunityTracks,
        getPlaylists,
        getPodcasts,
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


