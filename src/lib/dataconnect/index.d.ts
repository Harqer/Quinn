import { ConnectorConfig, DataConnect, QueryRef, QueryPromise, ExecuteQueryOptions, MutationRef, MutationPromise } from 'firebase/data-connect';

export const connectorConfig: ConnectorConfig;

export type TimestampString = string;
export type UUIDString = string;
export type Int64String = string;
export type DateString = string;




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

export interface Audiobook_Key {
  id: string;
  __typename?: 'Audiobook_Key';
}

export interface Category_Key {
  id: string;
  __typename?: 'Category_Key';
}

export interface CreatePlaylistData {
  playlist_insert: Playlist_Key;
}

export interface CreatePlaylistVariables {
  name: string;
  description?: string | null;
}

export interface CreatePodcastData {
  podcast_insert: Podcast_Key;
}

export interface CreatePodcastVariables {
  name: string;
  publisher: string;
  imageUrl?: string | null;
  description?: string | null;
}

export interface CreateTrackData {
  track_insert: Track_Key;
}

export interface CreateTrackVariables {
  name: string;
  artistName: string;
  albumName: string;
  imageUrl?: string | null;
  isCommunity: boolean;
}

export interface GetAlbumsData {
  albums: ({
    id: string;
    name: string;
    artistName: string;
    imageUrl?: string | null;
    releaseYear?: number | null;
  } & Album_Key)[];
}

export interface GetAudiobooksData {
  audiobooks: ({
    id: string;
    title: string;
    author: string;
    narrator?: string | null;
    imageUrl?: string | null;
    duration?: number | null;
    audioUrl?: string | null;
  } & Audiobook_Key)[];
}

export interface GetCategoriesData {
  categories: ({
    id: string;
    name: string;
    imageUrl?: string | null;
    type: string;
  } & Category_Key)[];
}

export interface GetCommunityTracksData {
  tracks: ({
    id: string;
    name: string;
    artistName: string;
    albumName: string;
    imageUrl?: string | null;
    createdAt: TimestampString;
    owner: {
      uid: string;
      displayName?: string | null;
    } & User_Key;
  } & Track_Key)[];
}

export interface GetPlaylistsData {
  playlists: ({
    id: string;
    name: string;
    description?: string | null;
    imageUrl?: string | null;
    isPublic: boolean;
  } & Playlist_Key)[];
}

export interface GetPodcastsData {
  podcasts: ({
    id: string;
    name: string;
    publisher: string;
    imageUrl?: string | null;
    description?: string | null;
  } & Podcast_Key)[];
}

export interface GetUserTracksData {
  tracks: ({
    id: string;
    name: string;
    artistName: string;
    albumName: string;
    imageUrl?: string | null;
    createdAt: TimestampString;
  } & Track_Key)[];
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

export interface PodcastEpisode_Key {
  id: string;
  __typename?: 'PodcastEpisode_Key';
}

export interface Podcast_Key {
  id: string;
  __typename?: 'Podcast_Key';
}

export interface Track_Key {
  id: string;
  __typename?: 'Track_Key';
}

export interface UpsertUserData {
  user_upsert: User_Key;
}

export interface UpsertUserVariables {
  displayName?: string | null;
  email?: string | null;
}

export interface User_Key {
  uid: string;
  __typename?: 'User_Key';
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
  (vars?: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
  /* Allow users to pass in custom DataConnect instances */
  (dc: DataConnect, vars?: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
  operationName: string;
}
export const upsertUserRef: UpsertUserRef;

export function upsertUser(vars?: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;
export function upsertUser(dc: DataConnect, vars?: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

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

