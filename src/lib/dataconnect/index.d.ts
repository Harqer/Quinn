import { ConnectorConfig, DataConnect, QueryRef, QueryPromise, ExecuteQueryOptions, MutationRef, MutationPromise } from 'firebase/data-connect';

export const connectorConfig: ConnectorConfig;

export type TimestampString = string;
export type UUIDString = string;
export type Int64String = string;
export type DateString = string;


export enum CategoryType {
  GENRE = "GENRE",
  MOOD = "MOOD",
  ACTIVITY = "ACTIVITY",
};

export enum SubscriptionTier {
  FREE = "FREE",
  PREMIUM = "PREMIUM",
  FAMILY = "FAMILY",
};



export interface AIPreset_Key {
  id: string;
  __typename?: 'AIPreset_Key';
}

export interface AddChatMessageData {
  chatMessage_insert: ChatMessage_Key;
}

export interface AddChatMessageVariables {
  userId: string;
  sender: string;
  text: string;
}

export interface AddTrackToPlaylistData {
  playlistEntry_insert: PlaylistEntry_Key;
}

export interface AddTrackToPlaylistVariables {
  playlistId: string;
  trackId: string;
}

export interface Album_Key {
  id: string;
  __typename?: 'Album_Key';
}

export interface Artist_Key {
  id: string;
  __typename?: 'Artist_Key';
}

export interface Audiobook_Key {
  id: string;
  __typename?: 'Audiobook_Key';
}

export interface AuthIdentity_Key {
  provider: string;
  providerSubject: string;
  __typename?: 'AuthIdentity_Key';
}

export interface Author_Key {
  id: string;
  __typename?: 'Author_Key';
}

export interface BookmarkTrackData {
  bookmarkedTrack_insert: BookmarkedTrack_Key;
}

export interface BookmarkTrackVariables {
  trackId: string;
}

export interface BookmarkedTrack_Key {
  userUid: string;
  trackId: string;
  __typename?: 'BookmarkedTrack_Key';
}

export interface CameraCapture_Key {
  id: string;
  __typename?: 'CameraCapture_Key';
}

export interface Category_Key {
  id: string;
  __typename?: 'Category_Key';
}

export interface Chapter_Key {
  id: string;
  __typename?: 'Chapter_Key';
}

export interface ChatMessage_Key {
  id: string;
  __typename?: 'ChatMessage_Key';
}

export interface CoverArt_Key {
  id: string;
  __typename?: 'CoverArt_Key';
}

export interface CreateAudiobookData {
  audiobook_insert: Audiobook_Key;
}

export interface CreateAudiobookVariables {
  title: string;
  authorId: string;
  narrator?: string | null;
  coverUrl?: string | null;
  storyContext?: string | null;
}

export interface CreateCameraCaptureData {
  cameraCapture_insert: CameraCapture_Key;
}

export interface CreateCameraCaptureVariables {
  imageUrl?: string | null;
  videoUrl?: string | null;
  environmentData?: string | null;
  generatedTrackId?: string | null;
}

export interface CreateCoverArtData {
  coverArt_insert: CoverArt_Key;
}

export interface CreateCoverArtVariables {
  trackId: string;
  imageUrl: string;
  promptUsed?: string | null;
  stylePreset?: string | null;
}

export interface CreateJamSessionHistoryData {
  jamSessionHistory_insert: JamSessionHistory_Key;
}

export interface CreateJamSessionHistoryVariables {
  roomId: string;
  gameMode: string;
  participantCount: number;
}

export interface CreateMusicVideoData {
  musicVideo_insert: MusicVideo_Key;
}

export interface CreateMusicVideoVariables {
  trackId: string;
  videoUrl: string;
  promptUsed?: string | null;
  motionPreset?: string | null;
}

export interface CreatePlaylistData {
  playlist_insert: Playlist_Key;
}

export interface CreatePlaylistVariables {
  name: string;
  description?: string | null;
}

export interface CreatePodcastData {
  show_insert: Show_Key;
}

export interface CreatePodcastVariables {
  title: string;
  publisher: string;
  coverUrl?: string | null;
  description?: string | null;
  storyContext?: string | null;
}

export interface CreateTrackData {
  track_insert: Track_Key;
}

export interface CreateTrackVariables {
  title: string;
  albumId?: string | null;
  audioUrl: string;
  coverUrl?: string | null;
  durationMs?: number | null;
  prompt?: string | null;
  isCommunity: boolean;
}

export interface CreateVideoDigestionData {
  videoDigestion_insert: VideoDigestion_Key;
}

export interface CreateVideoDigestionVariables {
  videoUrl: string;
  extractedAudioUrl?: string | null;
  atmosphereSummary?: string | null;
  generatedTrackId?: string | null;
}

export interface Episode_Key {
  id: string;
  __typename?: 'Episode_Key';
}

export interface FaqItem_Key {
  id: string;
  __typename?: 'FaqItem_Key';
}

export interface FeatureHighlight_Key {
  id: string;
  __typename?: 'FeatureHighlight_Key';
}

export interface GetAlbumTracksData {
  tracks: ({
    id: string;
    title: string;
    album?: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
      } & Artist_Key;
    } & Album_Key;
    coverUrl?: string | null;
    audioUrl: string;
    prompt?: string | null;
    visibility: string;
    isCommunity: boolean;
    createdAt: TimestampString;
  } & Track_Key)[];
}

export interface GetAlbumTracksVariables {
  albumId: string;
}

export interface GetAlbumsData {
  albums: ({
    id: string;
    title: string;
    primaryArtist: {
      id: string;
      name: string;
    } & Artist_Key;
    coverUrl?: string | null;
    releaseDate?: DateString | null;
  } & Album_Key)[];
}

export interface GetAudiobooksData {
  audiobooks: ({
    id: string;
    title: string;
    author: {
      id: string;
      name: string;
    } & Author_Key;
    narrator?: string | null;
    coverUrl?: string | null;
    totalDurationMs?: number | null;
  } & Audiobook_Key)[];
}

export interface GetBookmarkedTracksData {
  bookmarkedTracks: ({
    track: {
      id: string;
      title: string;
      album?: {
        id: string;
        title: string;
        primaryArtist: {
          id: string;
          name: string;
        } & Artist_Key;
      } & Album_Key;
      coverUrl?: string | null;
      audioUrl: string;
      prompt?: string | null;
      visibility: string;
      isCommunity: boolean;
      createdAt: TimestampString;
    } & Track_Key;
    addedAt: TimestampString;
  })[];
}

export interface GetCategoriesData {
  categories: ({
    id: string;
    name: string;
    type: CategoryType;
    colorHex?: string | null;
    imageUrl?: string | null;
  } & Category_Key)[];
}

export interface GetCategoryTracksData {
  musicCategories: ({
    track: {
      id: string;
      title: string;
      album?: {
        id: string;
        title: string;
        primaryArtist: {
          id: string;
          name: string;
        } & Artist_Key;
      } & Album_Key;
      coverUrl?: string | null;
      audioUrl: string;
      prompt?: string | null;
      visibility: string;
      isCommunity: boolean;
      createdAt: TimestampString;
    } & Track_Key;
  })[];
}

export interface GetCategoryTracksVariables {
  categoryId: string;
}

export interface GetCommunityTracksData {
  tracks: ({
    id: string;
    title: string;
    album?: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
      } & Artist_Key;
    } & Album_Key;
    coverUrl?: string | null;
    audioUrl: string;
    prompt?: string | null;
    createdAt: TimestampString;
    owner: {
      uid: string;
      displayName?: string | null;
    } & User_Key;
  } & Track_Key)[];
}

export interface GetCoverArtForTrackData {
  coverArts: ({
    id: string;
    imageUrl: string;
    promptUsed?: string | null;
    stylePreset?: string | null;
    createdAt: TimestampString;
  } & CoverArt_Key)[];
}

export interface GetCoverArtForTrackVariables {
  trackId: string;
}

export interface GetEpisodeData {
  episode?: {
    id: string;
    title: string;
    description?: string | null;
    publishDate?: TimestampString | null;
    durationMs: number;
    audioUrl: string;
    coverUrl?: string | null;
    show: {
      id: string;
      title: string;
      publisher: string;
      coverUrl?: string | null;
    } & Show_Key;
  } & Episode_Key;
}

export interface GetEpisodeVariables {
  id: string;
}

export interface GetEpisodesForShowData {
  episodes: ({
    id: string;
    title: string;
    description?: string | null;
    publishDate?: TimestampString | null;
    durationMs: number;
    audioUrl: string;
    coverUrl?: string | null;
    show: {
      id: string;
      title: string;
      publisher: string;
      coverUrl?: string | null;
    } & Show_Key;
  } & Episode_Key)[];
}

export interface GetEpisodesForShowVariables {
  showId: string;
}

export interface GetLikedTracksData {
  likedTracks: ({
    track: {
      id: string;
      title: string;
      album?: {
        id: string;
        title: string;
        primaryArtist: {
          id: string;
          name: string;
        } & Artist_Key;
      } & Album_Key;
      coverUrl?: string | null;
      audioUrl: string;
      prompt?: string | null;
      visibility: string;
      isCommunity: boolean;
      createdAt: TimestampString;
    } & Track_Key;
    addedAt: TimestampString;
  })[];
}

export interface GetMusicVideoForTrackData {
  musicVideos: ({
    id: string;
    videoUrl: string;
    promptUsed?: string | null;
    motionPreset?: string | null;
    createdAt: TimestampString;
  } & MusicVideo_Key)[];
}

export interface GetMusicVideoForTrackVariables {
  trackId: string;
}

export interface GetPaymentHistoryData {
  paymentHistories: ({
    id: string;
    amount: number;
    currency: string;
    status: string;
    stripeInvoiceId?: string | null;
    createdAt: TimestampString;
  } & PaymentHistory_Key)[];
}

export interface GetPlaylistTracksData {
  playlistEntries: ({
    track: {
      id: string;
      title: string;
      album?: {
        id: string;
        title: string;
        primaryArtist: {
          id: string;
          name: string;
        } & Artist_Key;
      } & Album_Key;
      coverUrl?: string | null;
      audioUrl: string;
      prompt?: string | null;
      visibility: string;
      isCommunity: boolean;
      createdAt: TimestampString;
    } & Track_Key;
  })[];
}

export interface GetPlaylistTracksVariables {
  playlistId: string;
}

export interface GetPlaylistsData {
  playlists: ({
    id: string;
    name: string;
    description?: string | null;
    coverUrl?: string | null;
    isPublic: boolean;
  } & Playlist_Key)[];
}

export interface GetPodcastsData {
  shows: ({
    id: string;
    title: string;
    publisher: string;
    coverUrl?: string | null;
    description?: string | null;
  } & Show_Key)[];
}

export interface GetTrackData {
  track?: {
    id: string;
    title: string;
    album?: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
        imageUrl?: string | null;
      } & Artist_Key;
      coverUrl?: string | null;
    } & Album_Key;
    durationMs: number;
    audioUrl: string;
    coverUrl?: string | null;
    playCount: Int64String;
    prompt?: string | null;
    lyrics?: string | null;
    isCommunity: boolean;
    createdAt: TimestampString;
  } & Track_Key;
}

export interface GetTrackVariables {
  id: string;
}

export interface GetUserCameraCapturesData {
  cameraCaptures: ({
    id: string;
    imageUrl?: string | null;
    videoUrl?: string | null;
    environmentData?: string | null;
    createdAt: TimestampString;
    generatedTrack?: {
      id: string;
      title: string;
      coverUrl?: string | null;
      audioUrl: string;
    } & Track_Key;
  } & CameraCapture_Key)[];
}

export interface GetUserSettingsData {
  userSettings?: {
    userId: string;
    isPremium: boolean;
    theme: string;
    parentalControlsEnabled: boolean;
    notificationsEnabled: boolean;
    appsDevicesEnabled: boolean;
    offlineMode: boolean;
    stripeCustomerId?: string | null;
    user: {
      displayName?: string | null;
      avatarUrl?: string | null;
    };
  } & UserSettings_Key;
}

export interface GetUserTracksData {
  tracks: ({
    id: string;
    title: string;
    album?: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
      } & Artist_Key;
    } & Album_Key;
    coverUrl?: string | null;
    audioUrl: string;
    prompt?: string | null;
    visibility: string;
    isCommunity: boolean;
    createdAt: TimestampString;
  } & Track_Key)[];
}

export interface GetUserVideoDigestionsData {
  videoDigestions: ({
    id: string;
    videoUrl: string;
    extractedAudioUrl?: string | null;
    atmosphereSummary?: string | null;
    createdAt: TimestampString;
    generatedTrack?: {
      id: string;
      title: string;
      coverUrl?: string | null;
      audioUrl: string;
    } & Track_Key;
  } & VideoDigestion_Key)[];
}

export interface GetUsersData {
  users: ({
    uid: string;
    username: string;
  } & User_Key)[];
}

export interface HomeSection_Key {
  id: string;
  __typename?: 'HomeSection_Key';
}

export interface Instrument_Key {
  name: string;
  __typename?: 'Instrument_Key';
}

export interface JamSessionHistory_Key {
  id: string;
  __typename?: 'JamSessionHistory_Key';
}

export interface LikeTrackData {
  likedTrack_upsert: LikedTrack_Key;
}

export interface LikeTrackVariables {
  trackId: string;
}

export interface LikedTrack_Key {
  userUid: string;
  trackId: string;
  __typename?: 'LikedTrack_Key';
}

export interface ListAiPresetsData {
  aIPresets: ({
    id: string;
    name: string;
    promptFragment: string;
    imageUrl?: string | null;
  } & AIPreset_Key)[];
}

export interface ListChatMessagesData {
  chatMessages: ({
    id: string;
    sender: string;
    text: string;
    createdAt: TimestampString;
  } & ChatMessage_Key)[];
}

export interface ListChatMessagesVariables {
  userId: string;
}

export interface ListFaqItemsData {
  faqItems: ({
    id: string;
    question: string;
    answer: string;
  } & FaqItem_Key)[];
}

export interface ListFeatureHighlightsData {
  featureHighlights: ({
    id: string;
    iconName: string;
    title: string;
    description: string;
  } & FeatureHighlight_Key)[];
}

export interface ListHomeSectionsData {
  homeSections: ({
    id: string;
    title: string;
    orderIndex: number;
    route: string;
  } & HomeSection_Key)[];
}

export interface ListInstrumentsData {
  instruments: ({
    name: string;
    iconUrl?: string | null;
  } & Instrument_Key)[];
}

export interface ListSubscriptionPlansData {
  subscriptionPlans: ({
    id: string;
    name: string;
    tier: SubscriptionTier;
    priceMonthly: number;
    priceAnnually: number;
    description: string;
    features: ({
      feature: string;
    })[];
  } & SubscriptionPlan_Key)[];
}

export interface MusicCategory_Key {
  trackId: string;
  categoryId: string;
  __typename?: 'MusicCategory_Key';
}

export interface MusicVideo_Key {
  id: string;
  __typename?: 'MusicVideo_Key';
}

export interface PaymentHistory_Key {
  id: string;
  __typename?: 'PaymentHistory_Key';
}

export interface PlayHistory_Key {
  userUid: string;
  trackId: string;
  playedAt: TimestampString;
  __typename?: 'PlayHistory_Key';
}

export interface PlaylistEntry_Key {
  playlistId: string;
  trackId: string;
  __typename?: 'PlaylistEntry_Key';
}

export interface Playlist_Key {
  id: string;
  __typename?: 'Playlist_Key';
}

export interface PodcastCategory_Key {
  showId: string;
  categoryId: string;
  __typename?: 'PodcastCategory_Key';
}

export interface RecordPaymentData {
  paymentHistory_insert: PaymentHistory_Key;
}

export interface RecordPaymentVariables {
  userUid: string;
  amount: number;
  currency: string;
  status: string;
  stripeInvoiceId?: string | null;
}

export interface RecordPlayData {
  playHistory_insert: PlayHistory_Key;
}

export interface RecordPlayVariables {
  trackId: string;
}

export interface RemoveBookmarkedTrackData {
  bookmarkedTrack_delete?: BookmarkedTrack_Key | null;
}

export interface RemoveBookmarkedTrackVariables {
  trackId: string;
}

export interface RemoveLikedTrackData {
  likedTrack_delete?: LikedTrack_Key | null;
}

export interface RemoveLikedTrackVariables {
  trackId: string;
}

export interface SaveUserEpisodeProgressData {
  userEpisodeProgress_upsert: UserEpisodeProgress_Key;
}

export interface SaveUserEpisodeProgressVariables {
  episodeId: string;
  progressMs: number;
}

export interface SearchAudiobooksData {
  audiobooks: ({
    id: string;
    title: string;
    author: {
      id: string;
      name: string;
    } & Author_Key;
    narrator?: string | null;
    coverUrl?: string | null;
    totalDurationMs?: number | null;
  } & Audiobook_Key)[];
}

export interface SearchAudiobooksVariables {
  query: string;
}

export interface SearchPodcastsData {
  shows: ({
    id: string;
    title: string;
    publisher: string;
    coverUrl?: string | null;
    description?: string | null;
  } & Show_Key)[];
}

export interface SearchPodcastsVariables {
  query: string;
}

export interface SearchTracksData {
  tracks: ({
    id: string;
    title: string;
    album?: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
      } & Artist_Key;
    } & Album_Key;
    coverUrl?: string | null;
    audioUrl: string;
    prompt?: string | null;
    visibility: string;
    isCommunity: boolean;
    createdAt: TimestampString;
  } & Track_Key)[];
}

export interface SearchTracksVariables {
  query: string;
}

export interface SeedAiPresetData {
  aIPreset_insert: AIPreset_Key;
}

export interface SeedAiPresetVariables {
  name: string;
  promptFragment: string;
  imageUrl?: string | null;
}

export interface SeedAuthorData {
  author_upsert: Author_Key;
}

export interface SeedAuthorVariables {
  id: string;
  name: string;
  bio?: string | null;
}

export interface SeedChapterData {
  chapter_insert: Chapter_Key;
}

export interface SeedChapterVariables {
  audiobookId: string;
  title: string;
  chapterNumber: number;
  audioUrl?: string | null;
  durationMs?: number | null;
}

export interface SeedEpisodeData {
  episode_insert: Episode_Key;
}

export interface SeedEpisodeVariables {
  showId: string;
  title: string;
  description?: string | null;
  audioUrl?: string | null;
  durationMs?: number | null;
  publishDate: TimestampString;
}

export interface SeedInstrumentData {
  instrument_upsert: Instrument_Key;
}

export interface SeedInstrumentVariables {
  name: string;
  iconUrl?: string | null;
}

export interface SeedTrackData {
  track_insert: Track_Key;
}

export interface SeedTrackVariables {
  title: string;
  audioUrl: string;
  coverUrl?: string | null;
  durationMs?: number | null;
  prompt?: string | null;
  isCommunity: boolean;
  ownerUid: string;
}

export interface SeedUserData {
  user_upsert: User_Key;
}

export interface SeedUserVariables {
  uid: string;
  username: string;
  email: string;
}

export interface Show_Key {
  id: string;
  __typename?: 'Show_Key';
}

export interface SubscriptionFeature_Key {
  planId: string;
  feature: string;
  __typename?: 'SubscriptionFeature_Key';
}

export interface SubscriptionPlan_Key {
  id: string;
  __typename?: 'SubscriptionPlan_Key';
}

export interface TrackArtist_Key {
  trackId: string;
  artistId: string;
  __typename?: 'TrackArtist_Key';
}

export interface Track_Key {
  id: string;
  __typename?: 'Track_Key';
}

export interface UpdateAudiobookContextData {
  audiobook_update?: Audiobook_Key | null;
}

export interface UpdateAudiobookContextVariables {
  id: string;
  storyContext: string;
}

export interface UpdateEpisodeAudioData {
  episode_update?: Episode_Key | null;
}

export interface UpdateEpisodeAudioVariables {
  id: string;
  audioUrl: string;
}

export interface UpdateShowContextData {
  show_update?: Show_Key | null;
}

export interface UpdateShowContextVariables {
  id: string;
  storyContext: string;
}

export interface UpdateTrackVideoData {
  track_update?: Track_Key | null;
}

export interface UpdateTrackVideoVariables {
  id: string;
  videoUrl: string;
}

export interface UpdateUserPreferencesData {
  user_update?: User_Key | null;
}

export interface UpdateUserPreferencesVariables {
  favoriteArtists?: string[] | null;
  favoriteGenres?: string[] | null;
}

export interface UpsertUserData {
  user_upsert: User_Key;
}

export interface UpsertUserSettingsData {
  userSettings_upsert: UserSettings_Key;
}

export interface UpsertUserSettingsVariables {
  theme?: string | null;
  parentalControlsEnabled?: boolean | null;
  notificationsEnabled?: boolean | null;
  appsDevicesEnabled?: boolean | null;
  offlineMode?: boolean | null;
  stripeCustomerId?: string | null;
}

export interface UpsertUserVariables {
  displayName?: string | null;
  username: string;
  email: string;
}

export interface UserEpisodeProgress_Key {
  userUid: string;
  episodeId: string;
  __typename?: 'UserEpisodeProgress_Key';
}

export interface UserSettings_Key {
  userId: string;
  __typename?: 'UserSettings_Key';
}

export interface User_Key {
  uid: string;
  __typename?: 'User_Key';
}

export interface VideoDigestion_Key {
  id: string;
  __typename?: 'VideoDigestion_Key';
}

interface CreateTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateTrackVariables): MutationRef<CreateTrackData, CreateTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateTrackVariables): MutationRef<CreateTrackData, CreateTrackVariables>;
  operationName: string;
}
export const createTrackRef: CreateTrackRef;

export function createTrack(vars: CreateTrackVariables): MutationPromise<CreateTrackData, CreateTrackVariables>;
export function createTrack(dc: DataConnect, vars: CreateTrackVariables): MutationPromise<CreateTrackData, CreateTrackVariables>;

interface UpsertUserRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
  operationName: string;
}
export const upsertUserRef: UpsertUserRef;

export function upsertUser(vars: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;
export function upsertUser(dc: DataConnect, vars: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

interface CreatePlaylistRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreatePlaylistVariables): MutationRef<CreatePlaylistData, CreatePlaylistVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreatePlaylistVariables): MutationRef<CreatePlaylistData, CreatePlaylistVariables>;
  operationName: string;
}
export const createPlaylistRef: CreatePlaylistRef;

export function createPlaylist(vars: CreatePlaylistVariables): MutationPromise<CreatePlaylistData, CreatePlaylistVariables>;
export function createPlaylist(dc: DataConnect, vars: CreatePlaylistVariables): MutationPromise<CreatePlaylistData, CreatePlaylistVariables>;

interface AddTrackToPlaylistRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: AddTrackToPlaylistVariables): MutationRef<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: AddTrackToPlaylistVariables): MutationRef<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;
  operationName: string;
}
export const addTrackToPlaylistRef: AddTrackToPlaylistRef;

export function addTrackToPlaylist(vars: AddTrackToPlaylistVariables): MutationPromise<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;
export function addTrackToPlaylist(dc: DataConnect, vars: AddTrackToPlaylistVariables): MutationPromise<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;

interface CreatePodcastRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreatePodcastVariables): MutationRef<CreatePodcastData, CreatePodcastVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreatePodcastVariables): MutationRef<CreatePodcastData, CreatePodcastVariables>;
  operationName: string;
}
export const createPodcastRef: CreatePodcastRef;

export function createPodcast(vars: CreatePodcastVariables): MutationPromise<CreatePodcastData, CreatePodcastVariables>;
export function createPodcast(dc: DataConnect, vars: CreatePodcastVariables): MutationPromise<CreatePodcastData, CreatePodcastVariables>;

interface UpsertUserSettingsRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars?: UpsertUserSettingsVariables): MutationRef<UpsertUserSettingsData, UpsertUserSettingsVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars?: UpsertUserSettingsVariables): MutationRef<UpsertUserSettingsData, UpsertUserSettingsVariables>;
  operationName: string;
}
export const upsertUserSettingsRef: UpsertUserSettingsRef;

export function upsertUserSettings(vars?: UpsertUserSettingsVariables): MutationPromise<UpsertUserSettingsData, UpsertUserSettingsVariables>;
export function upsertUserSettings(dc: DataConnect, vars?: UpsertUserSettingsVariables): MutationPromise<UpsertUserSettingsData, UpsertUserSettingsVariables>;

interface UpdateUserPreferencesRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars?: UpdateUserPreferencesVariables): MutationRef<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars?: UpdateUserPreferencesVariables): MutationRef<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;
  operationName: string;
}
export const updateUserPreferencesRef: UpdateUserPreferencesRef;

export function updateUserPreferences(vars?: UpdateUserPreferencesVariables): MutationPromise<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;
export function updateUserPreferences(dc: DataConnect, vars?: UpdateUserPreferencesVariables): MutationPromise<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;

interface RecordPaymentRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: RecordPaymentVariables): MutationRef<RecordPaymentData, RecordPaymentVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: RecordPaymentVariables): MutationRef<RecordPaymentData, RecordPaymentVariables>;
  operationName: string;
}
export const recordPaymentRef: RecordPaymentRef;

export function recordPayment(vars: RecordPaymentVariables): MutationPromise<RecordPaymentData, RecordPaymentVariables>;
export function recordPayment(dc: DataConnect, vars: RecordPaymentVariables): MutationPromise<RecordPaymentData, RecordPaymentVariables>;

interface LikeTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: LikeTrackVariables): MutationRef<LikeTrackData, LikeTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: LikeTrackVariables): MutationRef<LikeTrackData, LikeTrackVariables>;
  operationName: string;
}
export const likeTrackRef: LikeTrackRef;

export function likeTrack(vars: LikeTrackVariables): MutationPromise<LikeTrackData, LikeTrackVariables>;
export function likeTrack(dc: DataConnect, vars: LikeTrackVariables): MutationPromise<LikeTrackData, LikeTrackVariables>;

interface RemoveLikedTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: RemoveLikedTrackVariables): MutationRef<RemoveLikedTrackData, RemoveLikedTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: RemoveLikedTrackVariables): MutationRef<RemoveLikedTrackData, RemoveLikedTrackVariables>;
  operationName: string;
}
export const removeLikedTrackRef: RemoveLikedTrackRef;

export function removeLikedTrack(vars: RemoveLikedTrackVariables): MutationPromise<RemoveLikedTrackData, RemoveLikedTrackVariables>;
export function removeLikedTrack(dc: DataConnect, vars: RemoveLikedTrackVariables): MutationPromise<RemoveLikedTrackData, RemoveLikedTrackVariables>;

interface BookmarkTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: BookmarkTrackVariables): MutationRef<BookmarkTrackData, BookmarkTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: BookmarkTrackVariables): MutationRef<BookmarkTrackData, BookmarkTrackVariables>;
  operationName: string;
}
export const bookmarkTrackRef: BookmarkTrackRef;

export function bookmarkTrack(vars: BookmarkTrackVariables): MutationPromise<BookmarkTrackData, BookmarkTrackVariables>;
export function bookmarkTrack(dc: DataConnect, vars: BookmarkTrackVariables): MutationPromise<BookmarkTrackData, BookmarkTrackVariables>;

interface RecordPlayRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: RecordPlayVariables): MutationRef<RecordPlayData, RecordPlayVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: RecordPlayVariables): MutationRef<RecordPlayData, RecordPlayVariables>;
  operationName: string;
}
export const recordPlayRef: RecordPlayRef;

export function recordPlay(vars: RecordPlayVariables): MutationPromise<RecordPlayData, RecordPlayVariables>;
export function recordPlay(dc: DataConnect, vars: RecordPlayVariables): MutationPromise<RecordPlayData, RecordPlayVariables>;

interface RemoveBookmarkedTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: RemoveBookmarkedTrackVariables): MutationRef<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: RemoveBookmarkedTrackVariables): MutationRef<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;
  operationName: string;
}
export const removeBookmarkedTrackRef: RemoveBookmarkedTrackRef;

export function removeBookmarkedTrack(vars: RemoveBookmarkedTrackVariables): MutationPromise<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;
export function removeBookmarkedTrack(dc: DataConnect, vars: RemoveBookmarkedTrackVariables): MutationPromise<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;

interface CreateCameraCaptureRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars?: CreateCameraCaptureVariables): MutationRef<CreateCameraCaptureData, CreateCameraCaptureVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars?: CreateCameraCaptureVariables): MutationRef<CreateCameraCaptureData, CreateCameraCaptureVariables>;
  operationName: string;
}
export const createCameraCaptureRef: CreateCameraCaptureRef;

export function createCameraCapture(vars?: CreateCameraCaptureVariables): MutationPromise<CreateCameraCaptureData, CreateCameraCaptureVariables>;
export function createCameraCapture(dc: DataConnect, vars?: CreateCameraCaptureVariables): MutationPromise<CreateCameraCaptureData, CreateCameraCaptureVariables>;

interface CreateVideoDigestionRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateVideoDigestionVariables): MutationRef<CreateVideoDigestionData, CreateVideoDigestionVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateVideoDigestionVariables): MutationRef<CreateVideoDigestionData, CreateVideoDigestionVariables>;
  operationName: string;
}
export const createVideoDigestionRef: CreateVideoDigestionRef;

export function createVideoDigestion(vars: CreateVideoDigestionVariables): MutationPromise<CreateVideoDigestionData, CreateVideoDigestionVariables>;
export function createVideoDigestion(dc: DataConnect, vars: CreateVideoDigestionVariables): MutationPromise<CreateVideoDigestionData, CreateVideoDigestionVariables>;

interface AddChatMessageRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: AddChatMessageVariables): MutationRef<AddChatMessageData, AddChatMessageVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: AddChatMessageVariables): MutationRef<AddChatMessageData, AddChatMessageVariables>;
  operationName: string;
}
export const addChatMessageRef: AddChatMessageRef;

export function addChatMessage(vars: AddChatMessageVariables): MutationPromise<AddChatMessageData, AddChatMessageVariables>;
export function addChatMessage(dc: DataConnect, vars: AddChatMessageVariables): MutationPromise<AddChatMessageData, AddChatMessageVariables>;

interface SeedTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedTrackVariables): MutationRef<SeedTrackData, SeedTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedTrackVariables): MutationRef<SeedTrackData, SeedTrackVariables>;
  operationName: string;
}
export const seedTrackRef: SeedTrackRef;

export function seedTrack(vars: SeedTrackVariables): MutationPromise<SeedTrackData, SeedTrackVariables>;
export function seedTrack(dc: DataConnect, vars: SeedTrackVariables): MutationPromise<SeedTrackData, SeedTrackVariables>;

interface SeedUserRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedUserVariables): MutationRef<SeedUserData, SeedUserVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedUserVariables): MutationRef<SeedUserData, SeedUserVariables>;
  operationName: string;
}
export const seedUserRef: SeedUserRef;

export function seedUser(vars: SeedUserVariables): MutationPromise<SeedUserData, SeedUserVariables>;
export function seedUser(dc: DataConnect, vars: SeedUserVariables): MutationPromise<SeedUserData, SeedUserVariables>;

interface SeedAiPresetRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedAiPresetVariables): MutationRef<SeedAiPresetData, SeedAiPresetVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedAiPresetVariables): MutationRef<SeedAiPresetData, SeedAiPresetVariables>;
  operationName: string;
}
export const seedAiPresetRef: SeedAiPresetRef;

export function seedAiPreset(vars: SeedAiPresetVariables): MutationPromise<SeedAiPresetData, SeedAiPresetVariables>;
export function seedAiPreset(dc: DataConnect, vars: SeedAiPresetVariables): MutationPromise<SeedAiPresetData, SeedAiPresetVariables>;

interface UpdateShowContextRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateShowContextVariables): MutationRef<UpdateShowContextData, UpdateShowContextVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: UpdateShowContextVariables): MutationRef<UpdateShowContextData, UpdateShowContextVariables>;
  operationName: string;
}
export const updateShowContextRef: UpdateShowContextRef;

export function updateShowContext(vars: UpdateShowContextVariables): MutationPromise<UpdateShowContextData, UpdateShowContextVariables>;
export function updateShowContext(dc: DataConnect, vars: UpdateShowContextVariables): MutationPromise<UpdateShowContextData, UpdateShowContextVariables>;

interface UpdateAudiobookContextRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateAudiobookContextVariables): MutationRef<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: UpdateAudiobookContextVariables): MutationRef<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;
  operationName: string;
}
export const updateAudiobookContextRef: UpdateAudiobookContextRef;

export function updateAudiobookContext(vars: UpdateAudiobookContextVariables): MutationPromise<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;
export function updateAudiobookContext(dc: DataConnect, vars: UpdateAudiobookContextVariables): MutationPromise<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;

interface SeedAuthorRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedAuthorVariables): MutationRef<SeedAuthorData, SeedAuthorVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedAuthorVariables): MutationRef<SeedAuthorData, SeedAuthorVariables>;
  operationName: string;
}
export const seedAuthorRef: SeedAuthorRef;

export function seedAuthor(vars: SeedAuthorVariables): MutationPromise<SeedAuthorData, SeedAuthorVariables>;
export function seedAuthor(dc: DataConnect, vars: SeedAuthorVariables): MutationPromise<SeedAuthorData, SeedAuthorVariables>;

interface CreateAudiobookRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateAudiobookVariables): MutationRef<CreateAudiobookData, CreateAudiobookVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateAudiobookVariables): MutationRef<CreateAudiobookData, CreateAudiobookVariables>;
  operationName: string;
}
export const createAudiobookRef: CreateAudiobookRef;

export function createAudiobook(vars: CreateAudiobookVariables): MutationPromise<CreateAudiobookData, CreateAudiobookVariables>;
export function createAudiobook(dc: DataConnect, vars: CreateAudiobookVariables): MutationPromise<CreateAudiobookData, CreateAudiobookVariables>;

interface SeedEpisodeRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedEpisodeVariables): MutationRef<SeedEpisodeData, SeedEpisodeVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedEpisodeVariables): MutationRef<SeedEpisodeData, SeedEpisodeVariables>;
  operationName: string;
}
export const seedEpisodeRef: SeedEpisodeRef;

export function seedEpisode(vars: SeedEpisodeVariables): MutationPromise<SeedEpisodeData, SeedEpisodeVariables>;
export function seedEpisode(dc: DataConnect, vars: SeedEpisodeVariables): MutationPromise<SeedEpisodeData, SeedEpisodeVariables>;

interface SeedChapterRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedChapterVariables): MutationRef<SeedChapterData, SeedChapterVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedChapterVariables): MutationRef<SeedChapterData, SeedChapterVariables>;
  operationName: string;
}
export const seedChapterRef: SeedChapterRef;

export function seedChapter(vars: SeedChapterVariables): MutationPromise<SeedChapterData, SeedChapterVariables>;
export function seedChapter(dc: DataConnect, vars: SeedChapterVariables): MutationPromise<SeedChapterData, SeedChapterVariables>;

interface UpdateTrackVideoRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateTrackVideoVariables): MutationRef<UpdateTrackVideoData, UpdateTrackVideoVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: UpdateTrackVideoVariables): MutationRef<UpdateTrackVideoData, UpdateTrackVideoVariables>;
  operationName: string;
}
export const updateTrackVideoRef: UpdateTrackVideoRef;

export function updateTrackVideo(vars: UpdateTrackVideoVariables): MutationPromise<UpdateTrackVideoData, UpdateTrackVideoVariables>;
export function updateTrackVideo(dc: DataConnect, vars: UpdateTrackVideoVariables): MutationPromise<UpdateTrackVideoData, UpdateTrackVideoVariables>;

interface UpdateEpisodeAudioRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateEpisodeAudioVariables): MutationRef<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: UpdateEpisodeAudioVariables): MutationRef<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;
  operationName: string;
}
export const updateEpisodeAudioRef: UpdateEpisodeAudioRef;

export function updateEpisodeAudio(vars: UpdateEpisodeAudioVariables): MutationPromise<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;
export function updateEpisodeAudio(dc: DataConnect, vars: UpdateEpisodeAudioVariables): MutationPromise<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;

interface SeedInstrumentRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedInstrumentVariables): MutationRef<SeedInstrumentData, SeedInstrumentVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SeedInstrumentVariables): MutationRef<SeedInstrumentData, SeedInstrumentVariables>;
  operationName: string;
}
export const seedInstrumentRef: SeedInstrumentRef;

export function seedInstrument(vars: SeedInstrumentVariables): MutationPromise<SeedInstrumentData, SeedInstrumentVariables>;
export function seedInstrument(dc: DataConnect, vars: SeedInstrumentVariables): MutationPromise<SeedInstrumentData, SeedInstrumentVariables>;

interface CreateCoverArtRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateCoverArtVariables): MutationRef<CreateCoverArtData, CreateCoverArtVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateCoverArtVariables): MutationRef<CreateCoverArtData, CreateCoverArtVariables>;
  operationName: string;
}
export const createCoverArtRef: CreateCoverArtRef;

export function createCoverArt(vars: CreateCoverArtVariables): MutationPromise<CreateCoverArtData, CreateCoverArtVariables>;
export function createCoverArt(dc: DataConnect, vars: CreateCoverArtVariables): MutationPromise<CreateCoverArtData, CreateCoverArtVariables>;

interface CreateMusicVideoRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateMusicVideoVariables): MutationRef<CreateMusicVideoData, CreateMusicVideoVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateMusicVideoVariables): MutationRef<CreateMusicVideoData, CreateMusicVideoVariables>;
  operationName: string;
}
export const createMusicVideoRef: CreateMusicVideoRef;

export function createMusicVideo(vars: CreateMusicVideoVariables): MutationPromise<CreateMusicVideoData, CreateMusicVideoVariables>;
export function createMusicVideo(dc: DataConnect, vars: CreateMusicVideoVariables): MutationPromise<CreateMusicVideoData, CreateMusicVideoVariables>;

interface CreateJamSessionHistoryRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateJamSessionHistoryVariables): MutationRef<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: CreateJamSessionHistoryVariables): MutationRef<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;
  operationName: string;
}
export const createJamSessionHistoryRef: CreateJamSessionHistoryRef;

export function createJamSessionHistory(vars: CreateJamSessionHistoryVariables): MutationPromise<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;
export function createJamSessionHistory(dc: DataConnect, vars: CreateJamSessionHistoryVariables): MutationPromise<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;

interface SaveUserEpisodeProgressRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SaveUserEpisodeProgressVariables): MutationRef<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SaveUserEpisodeProgressVariables): MutationRef<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;
  operationName: string;
}
export const saveUserEpisodeProgressRef: SaveUserEpisodeProgressRef;

export function saveUserEpisodeProgress(vars: SaveUserEpisodeProgressVariables): MutationPromise<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;
export function saveUserEpisodeProgress(dc: DataConnect, vars: SaveUserEpisodeProgressVariables): MutationPromise<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;

interface GetUserTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserTracksData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetUserTracksData, undefined>;
  operationName: string;
}
export const getUserTracksRef: GetUserTracksRef;

export function getUserTracks(options?: ExecuteQueryOptions): QueryPromise<GetUserTracksData, undefined>;
export function getUserTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserTracksData, undefined>;

interface GetCommunityTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetCommunityTracksData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetCommunityTracksData, undefined>;
  operationName: string;
}
export const getCommunityTracksRef: GetCommunityTracksRef;

export function getCommunityTracks(options?: ExecuteQueryOptions): QueryPromise<GetCommunityTracksData, undefined>;
export function getCommunityTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetCommunityTracksData, undefined>;

interface GetCategoriesRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetCategoriesData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetCategoriesData, undefined>;
  operationName: string;
}
export const getCategoriesRef: GetCategoriesRef;

export function getCategories(options?: ExecuteQueryOptions): QueryPromise<GetCategoriesData, undefined>;
export function getCategories(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetCategoriesData, undefined>;

interface GetPlaylistsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPlaylistsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetPlaylistsData, undefined>;
  operationName: string;
}
export const getPlaylistsRef: GetPlaylistsRef;

export function getPlaylists(options?: ExecuteQueryOptions): QueryPromise<GetPlaylistsData, undefined>;
export function getPlaylists(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistsData, undefined>;

interface GetAlbumsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetAlbumsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetAlbumsData, undefined>;
  operationName: string;
}
export const getAlbumsRef: GetAlbumsRef;

export function getAlbums(options?: ExecuteQueryOptions): QueryPromise<GetAlbumsData, undefined>;
export function getAlbums(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetAlbumsData, undefined>;

interface GetPodcastsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPodcastsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetPodcastsData, undefined>;
  operationName: string;
}
export const getPodcastsRef: GetPodcastsRef;

export function getPodcasts(options?: ExecuteQueryOptions): QueryPromise<GetPodcastsData, undefined>;
export function getPodcasts(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPodcastsData, undefined>;

interface GetAudiobooksRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetAudiobooksData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetAudiobooksData, undefined>;
  operationName: string;
}
export const getAudiobooksRef: GetAudiobooksRef;

export function getAudiobooks(options?: ExecuteQueryOptions): QueryPromise<GetAudiobooksData, undefined>;
export function getAudiobooks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetAudiobooksData, undefined>;

interface GetUserSettingsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserSettingsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetUserSettingsData, undefined>;
  operationName: string;
}
export const getUserSettingsRef: GetUserSettingsRef;

export function getUserSettings(options?: ExecuteQueryOptions): QueryPromise<GetUserSettingsData, undefined>;
export function getUserSettings(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserSettingsData, undefined>;

interface GetPaymentHistoryRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPaymentHistoryData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetPaymentHistoryData, undefined>;
  operationName: string;
}
export const getPaymentHistoryRef: GetPaymentHistoryRef;

export function getPaymentHistory(options?: ExecuteQueryOptions): QueryPromise<GetPaymentHistoryData, undefined>;
export function getPaymentHistory(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPaymentHistoryData, undefined>;

interface GetLikedTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetLikedTracksData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetLikedTracksData, undefined>;
  operationName: string;
}
export const getLikedTracksRef: GetLikedTracksRef;

export function getLikedTracks(options?: ExecuteQueryOptions): QueryPromise<GetLikedTracksData, undefined>;
export function getLikedTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetLikedTracksData, undefined>;

interface GetBookmarkedTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetBookmarkedTracksData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetBookmarkedTracksData, undefined>;
  operationName: string;
}
export const getBookmarkedTracksRef: GetBookmarkedTracksRef;

export function getBookmarkedTracks(options?: ExecuteQueryOptions): QueryPromise<GetBookmarkedTracksData, undefined>;
export function getBookmarkedTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetBookmarkedTracksData, undefined>;

interface GetUserCameraCapturesRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserCameraCapturesData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetUserCameraCapturesData, undefined>;
  operationName: string;
}
export const getUserCameraCapturesRef: GetUserCameraCapturesRef;

export function getUserCameraCaptures(options?: ExecuteQueryOptions): QueryPromise<GetUserCameraCapturesData, undefined>;
export function getUserCameraCaptures(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserCameraCapturesData, undefined>;

interface GetUserVideoDigestionsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserVideoDigestionsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetUserVideoDigestionsData, undefined>;
  operationName: string;
}
export const getUserVideoDigestionsRef: GetUserVideoDigestionsRef;

export function getUserVideoDigestions(options?: ExecuteQueryOptions): QueryPromise<GetUserVideoDigestionsData, undefined>;
export function getUserVideoDigestions(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserVideoDigestionsData, undefined>;

interface GetPlaylistTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetPlaylistTracksVariables): QueryRef<GetPlaylistTracksData, GetPlaylistTracksVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetPlaylistTracksVariables): QueryRef<GetPlaylistTracksData, GetPlaylistTracksVariables>;
  operationName: string;
}
export const getPlaylistTracksRef: GetPlaylistTracksRef;

export function getPlaylistTracks(vars: GetPlaylistTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistTracksData, GetPlaylistTracksVariables>;
export function getPlaylistTracks(dc: DataConnect, vars: GetPlaylistTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistTracksData, GetPlaylistTracksVariables>;

interface GetCategoryTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetCategoryTracksVariables): QueryRef<GetCategoryTracksData, GetCategoryTracksVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetCategoryTracksVariables): QueryRef<GetCategoryTracksData, GetCategoryTracksVariables>;
  operationName: string;
}
export const getCategoryTracksRef: GetCategoryTracksRef;

export function getCategoryTracks(vars: GetCategoryTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetCategoryTracksData, GetCategoryTracksVariables>;
export function getCategoryTracks(dc: DataConnect, vars: GetCategoryTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetCategoryTracksData, GetCategoryTracksVariables>;

interface GetAlbumTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetAlbumTracksVariables): QueryRef<GetAlbumTracksData, GetAlbumTracksVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetAlbumTracksVariables): QueryRef<GetAlbumTracksData, GetAlbumTracksVariables>;
  operationName: string;
}
export const getAlbumTracksRef: GetAlbumTracksRef;

export function getAlbumTracks(vars: GetAlbumTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetAlbumTracksData, GetAlbumTracksVariables>;
export function getAlbumTracks(dc: DataConnect, vars: GetAlbumTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetAlbumTracksData, GetAlbumTracksVariables>;

interface SearchTracksRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchTracksVariables): QueryRef<SearchTracksData, SearchTracksVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SearchTracksVariables): QueryRef<SearchTracksData, SearchTracksVariables>;
  operationName: string;
}
export const searchTracksRef: SearchTracksRef;

export function searchTracks(vars: SearchTracksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchTracksData, SearchTracksVariables>;
export function searchTracks(dc: DataConnect, vars: SearchTracksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchTracksData, SearchTracksVariables>;

interface SearchPodcastsRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchPodcastsVariables): QueryRef<SearchPodcastsData, SearchPodcastsVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SearchPodcastsVariables): QueryRef<SearchPodcastsData, SearchPodcastsVariables>;
  operationName: string;
}
export const searchPodcastsRef: SearchPodcastsRef;

export function searchPodcasts(vars: SearchPodcastsVariables, options?: ExecuteQueryOptions): QueryPromise<SearchPodcastsData, SearchPodcastsVariables>;
export function searchPodcasts(dc: DataConnect, vars: SearchPodcastsVariables, options?: ExecuteQueryOptions): QueryPromise<SearchPodcastsData, SearchPodcastsVariables>;

interface SearchAudiobooksRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchAudiobooksVariables): QueryRef<SearchAudiobooksData, SearchAudiobooksVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: SearchAudiobooksVariables): QueryRef<SearchAudiobooksData, SearchAudiobooksVariables>;
  operationName: string;
}
export const searchAudiobooksRef: SearchAudiobooksRef;

export function searchAudiobooks(vars: SearchAudiobooksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchAudiobooksData, SearchAudiobooksVariables>;
export function searchAudiobooks(dc: DataConnect, vars: SearchAudiobooksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchAudiobooksData, SearchAudiobooksVariables>;

interface ListChatMessagesRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: ListChatMessagesVariables): QueryRef<ListChatMessagesData, ListChatMessagesVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: ListChatMessagesVariables): QueryRef<ListChatMessagesData, ListChatMessagesVariables>;
  operationName: string;
}
export const listChatMessagesRef: ListChatMessagesRef;

export function listChatMessages(vars: ListChatMessagesVariables, options?: ExecuteQueryOptions): QueryPromise<ListChatMessagesData, ListChatMessagesVariables>;
export function listChatMessages(dc: DataConnect, vars: ListChatMessagesVariables, options?: ExecuteQueryOptions): QueryPromise<ListChatMessagesData, ListChatMessagesVariables>;

interface ListSubscriptionPlansRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListSubscriptionPlansData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListSubscriptionPlansData, undefined>;
  operationName: string;
}
export const listSubscriptionPlansRef: ListSubscriptionPlansRef;

export function listSubscriptionPlans(options?: ExecuteQueryOptions): QueryPromise<ListSubscriptionPlansData, undefined>;
export function listSubscriptionPlans(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListSubscriptionPlansData, undefined>;

interface ListFaqItemsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListFaqItemsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListFaqItemsData, undefined>;
  operationName: string;
}
export const listFaqItemsRef: ListFaqItemsRef;

export function listFaqItems(options?: ExecuteQueryOptions): QueryPromise<ListFaqItemsData, undefined>;
export function listFaqItems(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListFaqItemsData, undefined>;

interface ListAiPresetsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListAiPresetsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListAiPresetsData, undefined>;
  operationName: string;
}
export const listAiPresetsRef: ListAiPresetsRef;

export function listAiPresets(options?: ExecuteQueryOptions): QueryPromise<ListAiPresetsData, undefined>;
export function listAiPresets(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListAiPresetsData, undefined>;

interface ListFeatureHighlightsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListFeatureHighlightsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListFeatureHighlightsData, undefined>;
  operationName: string;
}
export const listFeatureHighlightsRef: ListFeatureHighlightsRef;

export function listFeatureHighlights(options?: ExecuteQueryOptions): QueryPromise<ListFeatureHighlightsData, undefined>;
export function listFeatureHighlights(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListFeatureHighlightsData, undefined>;

interface ListHomeSectionsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListHomeSectionsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListHomeSectionsData, undefined>;
  operationName: string;
}
export const listHomeSectionsRef: ListHomeSectionsRef;

export function listHomeSections(options?: ExecuteQueryOptions): QueryPromise<ListHomeSectionsData, undefined>;
export function listHomeSections(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListHomeSectionsData, undefined>;

interface GetUsersRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUsersData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<GetUsersData, undefined>;
  operationName: string;
}
export const getUsersRef: GetUsersRef;

export function getUsers(options?: ExecuteQueryOptions): QueryPromise<GetUsersData, undefined>;
export function getUsers(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUsersData, undefined>;

interface GetTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetTrackVariables): QueryRef<GetTrackData, GetTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetTrackVariables): QueryRef<GetTrackData, GetTrackVariables>;
  operationName: string;
}
export const getTrackRef: GetTrackRef;

export function getTrack(vars: GetTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetTrackData, GetTrackVariables>;
export function getTrack(dc: DataConnect, vars: GetTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetTrackData, GetTrackVariables>;

interface GetEpisodesForShowRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetEpisodesForShowVariables): QueryRef<GetEpisodesForShowData, GetEpisodesForShowVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetEpisodesForShowVariables): QueryRef<GetEpisodesForShowData, GetEpisodesForShowVariables>;
  operationName: string;
}
export const getEpisodesForShowRef: GetEpisodesForShowRef;

export function getEpisodesForShow(vars: GetEpisodesForShowVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodesForShowData, GetEpisodesForShowVariables>;
export function getEpisodesForShow(dc: DataConnect, vars: GetEpisodesForShowVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodesForShowData, GetEpisodesForShowVariables>;

interface GetEpisodeRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetEpisodeVariables): QueryRef<GetEpisodeData, GetEpisodeVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetEpisodeVariables): QueryRef<GetEpisodeData, GetEpisodeVariables>;
  operationName: string;
}
export const getEpisodeRef: GetEpisodeRef;

export function getEpisode(vars: GetEpisodeVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodeData, GetEpisodeVariables>;
export function getEpisode(dc: DataConnect, vars: GetEpisodeVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodeData, GetEpisodeVariables>;

interface ListInstrumentsRef {
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListInstrumentsData, undefined>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect): QueryRef<ListInstrumentsData, undefined>;
  operationName: string;
}
export const listInstrumentsRef: ListInstrumentsRef;

export function listInstruments(options?: ExecuteQueryOptions): QueryPromise<ListInstrumentsData, undefined>;
export function listInstruments(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListInstrumentsData, undefined>;

interface GetCoverArtForTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetCoverArtForTrackVariables): QueryRef<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetCoverArtForTrackVariables): QueryRef<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;
  operationName: string;
}
export const getCoverArtForTrackRef: GetCoverArtForTrackRef;

export function getCoverArtForTrack(vars: GetCoverArtForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;
export function getCoverArtForTrack(dc: DataConnect, vars: GetCoverArtForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;

interface GetMusicVideoForTrackRef {
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetMusicVideoForTrackVariables): QueryRef<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars: GetMusicVideoForTrackVariables): QueryRef<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;
  operationName: string;
}
export const getMusicVideoForTrackRef: GetMusicVideoForTrackRef;

export function getMusicVideoForTrack(vars: GetMusicVideoForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;
export function getMusicVideoForTrack(dc: DataConnect, vars: GetMusicVideoForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;

