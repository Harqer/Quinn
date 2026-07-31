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

export interface CreateCameraCaptureData {
  cameraCapture_insert: CameraCapture_Key;
}

export interface CreateCameraCaptureVariables {
  imageUrl?: string | null;
  videoUrl?: string | null;
  environmentData?: string | null;
  generatedTrackId?: string | null;
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
}

export interface CreateTrackData {
  track_insert: Track_Key;
}

export interface CreateTrackVariables {
  title: string;
  albumId: string;
  audioUrl: string;
  coverUrl?: string | null;
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
      album: {
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
  } & Category_Key)[];
}

export interface GetCommunityTracksData {
  tracks: ({
    id: string;
    title: string;
    album: {
      id: string;
      title: string;
      primaryArtist: {
        id: string;
        name: string;
      } & Artist_Key;
    } & Album_Key;
    coverUrl?: string | null;
    createdAt: TimestampString;
    owner: {
      uid: string;
      displayName?: string | null;
    } & User_Key;
  } & Track_Key)[];
}

export interface GetLikedTracksData {
  likedTracks: ({
    track: {
      id: string;
      title: string;
      album: {
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
    album: {
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

export interface LikeTrackData {
  likedTrack_insert: LikedTrack_Key;
}

export interface LikeTrackVariables {
  trackId: string;
}

export interface LikedTrack_Key {
  userUid: string;
  trackId: string;
  __typename?: 'LikedTrack_Key';
}

export interface MusicCategory_Key {
  trackId: string;
  categoryId: string;
  __typename?: 'MusicCategory_Key';
}

export interface PaymentHistory_Key {
  id: string;
  __typename?: 'PaymentHistory_Key';
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

export interface Show_Key {
  id: string;
  __typename?: 'Show_Key';
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

