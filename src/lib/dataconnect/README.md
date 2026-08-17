# Generated TypeScript README
This README will guide you through the process of using the generated JavaScript SDK package for the connector `default`. It will also provide examples on how to use your generated SDK to call your Data Connect queries and mutations.

***NOTE:** This README is generated alongside the generated SDK. If you make changes to this file, they will be overwritten when the SDK is regenerated.*

# Table of Contents
- [**Overview**](#generated-javascript-readme)
- [**Accessing the connector**](#accessing-the-connector)
  - [*Connecting to the local Emulator*](#connecting-to-the-local-emulator)
- [**Queries**](#queries)
  - [*GetUserTracks*](#getusertracks)
  - [*GetCommunityTracks*](#getcommunitytracks)
  - [*GetCategories*](#getcategories)
  - [*GetPlaylists*](#getplaylists)
  - [*GetAlbums*](#getalbums)
  - [*GetPodcasts*](#getpodcasts)
  - [*GetAudiobooks*](#getaudiobooks)
  - [*GetUserSettings*](#getusersettings)
  - [*GetPaymentHistory*](#getpaymenthistory)
  - [*GetLikedTracks*](#getlikedtracks)
  - [*GetBookmarkedTracks*](#getbookmarkedtracks)
  - [*GetUserCameraCaptures*](#getusercameracaptures)
  - [*GetUserVideoDigestions*](#getuservideodigestions)
  - [*GetPlaylistTracks*](#getplaylisttracks)
  - [*GetCategoryTracks*](#getcategorytracks)
  - [*GetAlbumTracks*](#getalbumtracks)
  - [*SearchTracks*](#searchtracks)
  - [*SearchPodcasts*](#searchpodcasts)
  - [*SearchAudiobooks*](#searchaudiobooks)
  - [*ListChatMessages*](#listchatmessages)
  - [*ListSubscriptionPlans*](#listsubscriptionplans)
  - [*ListFaqItems*](#listfaqitems)
  - [*ListAIPresets*](#listaipresets)
  - [*ListFeatureHighlights*](#listfeaturehighlights)
  - [*ListHomeSections*](#listhomesections)
  - [*GetUsers*](#getusers)
  - [*GetTrack*](#gettrack)
  - [*GetEpisodesForShow*](#getepisodesforshow)
  - [*GetEpisode*](#getepisode)
  - [*ListInstruments*](#listinstruments)
  - [*GetCoverArtForTrack*](#getcoverartfortrack)
  - [*GetMusicVideoForTrack*](#getmusicvideofortrack)
- [**Mutations**](#mutations)
  - [*CreateTrack*](#createtrack)
  - [*UpsertUser*](#upsertuser)
  - [*CreatePlaylist*](#createplaylist)
  - [*AddTrackToPlaylist*](#addtracktoplaylist)
  - [*CreatePodcast*](#createpodcast)
  - [*UpsertUserSettings*](#upsertusersettings)
  - [*UpdateUserPreferences*](#updateuserpreferences)
  - [*RecordPayment*](#recordpayment)
  - [*LikeTrack*](#liketrack)
  - [*RemoveLikedTrack*](#removelikedtrack)
  - [*BookmarkTrack*](#bookmarktrack)
  - [*RecordPlay*](#recordplay)
  - [*RemoveBookmarkedTrack*](#removebookmarkedtrack)
  - [*CreateCameraCapture*](#createcameracapture)
  - [*CreateVideoDigestion*](#createvideodigestion)
  - [*AddChatMessage*](#addchatmessage)
  - [*SeedTrack*](#seedtrack)
  - [*SeedUser*](#seeduser)
  - [*SeedAIPreset*](#seedaipreset)
  - [*UpdateShowContext*](#updateshowcontext)
  - [*UpdateAudiobookContext*](#updateaudiobookcontext)
  - [*SeedAuthor*](#seedauthor)
  - [*CreateAudiobook*](#createaudiobook)
  - [*SeedEpisode*](#seedepisode)
  - [*SeedChapter*](#seedchapter)
  - [*UpdateTrackVideo*](#updatetrackvideo)
  - [*UpdateEpisodeAudio*](#updateepisodeaudio)
  - [*SeedInstrument*](#seedinstrument)
  - [*CreateCoverArt*](#createcoverart)
  - [*CreateMusicVideo*](#createmusicvideo)
  - [*CreateJamSessionHistory*](#createjamsessionhistory)
  - [*SaveUserEpisodeProgress*](#saveuserepisodeprogress)

# Accessing the connector
A connector is a collection of Queries and Mutations. One SDK is generated for each connector - this SDK is generated for the connector `default`. You can find more information about connectors in the [Data Connect documentation](https://firebase.google.com/docs/data-connect#how-does).

You can use this generated SDK by importing from the package `@musically/dataconnect` as shown below. Both CommonJS and ESM imports are supported.

You can also follow the instructions from the [Data Connect documentation](https://firebase.google.com/docs/data-connect/web-sdk#set-client).

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig } from '@musically/dataconnect';

const dataConnect = getDataConnect(connectorConfig);
```

## Connecting to the local Emulator
By default, the connector will connect to the production service.

To connect to the emulator, you can use the following code.
You can also follow the emulator instructions from the [Data Connect documentation](https://firebase.google.com/docs/data-connect/web-sdk#instrument-clients).

```typescript
import { connectDataConnectEmulator, getDataConnect } from 'firebase/data-connect';
import { connectorConfig } from '@musically/dataconnect';

const dataConnect = getDataConnect(connectorConfig);
connectDataConnectEmulator(dataConnect, 'localhost', 9399);
```

After it's initialized, you can call your Data Connect [queries](#queries) and [mutations](#mutations) from your generated SDK.

# Queries

There are two ways to execute a Data Connect Query using the generated Web SDK:
- Using a Query Reference function, which returns a `QueryRef`
  - The `QueryRef` can be used as an argument to `executeQuery()`, which will execute the Query and return a `QueryPromise`
- Using an action shortcut function, which returns a `QueryPromise`
  - Calling the action shortcut function will execute the Query and return a `QueryPromise`

The following is true for both the action shortcut function and the `QueryRef` function:
- The `QueryPromise` returned will resolve to the result of the Query once it has finished executing
- If the Query accepts arguments, both the action shortcut function and the `QueryRef` function accept a single argument: an object that contains all the required variables (and the optional variables) for the Query
- Both functions can be called with or without passing in a `DataConnect` instance as an argument. If no `DataConnect` argument is passed in, then the generated SDK will call `getDataConnect(connectorConfig)` behind the scenes for you.

Below are examples of how to use the `default` connector's generated functions to execute each query. You can also follow the examples from the [Data Connect documentation](https://firebase.google.com/docs/data-connect/web-sdk#using-queries).

## GetUserTracks
You can execute the `GetUserTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getUserTracks(options?: ExecuteQueryOptions): QueryPromise<GetUserTracksData, undefined>;

interface GetUserTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserTracksData, undefined>;
}
export const getUserTracksRef: GetUserTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getUserTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserTracksData, undefined>;

interface GetUserTracksRef {
  ...
  (dc: DataConnect): QueryRef<GetUserTracksData, undefined>;
}
export const getUserTracksRef: GetUserTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getUserTracksRef:
```typescript
const name = getUserTracksRef.operationName;
console.log(name);
```

### Variables
The `GetUserTracks` query has no variables.
### Return Type
Recall that executing the `GetUserTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetUserTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetUserTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getUserTracks } from '@musically/dataconnect';


// Call the `getUserTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getUserTracks();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getUserTracks(dataConnect);

console.log(data.tracks);

// Or, you can use the `Promise` API.
getUserTracks().then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

### Using `GetUserTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getUserTracksRef } from '@musically/dataconnect';


// Call the `getUserTracksRef()` function to get a reference to the query.
const ref = getUserTracksRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getUserTracksRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.tracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

## GetCommunityTracks
You can execute the `GetCommunityTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getCommunityTracks(options?: ExecuteQueryOptions): QueryPromise<GetCommunityTracksData, undefined>;

interface GetCommunityTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetCommunityTracksData, undefined>;
}
export const getCommunityTracksRef: GetCommunityTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getCommunityTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetCommunityTracksData, undefined>;

interface GetCommunityTracksRef {
  ...
  (dc: DataConnect): QueryRef<GetCommunityTracksData, undefined>;
}
export const getCommunityTracksRef: GetCommunityTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getCommunityTracksRef:
```typescript
const name = getCommunityTracksRef.operationName;
console.log(name);
```

### Variables
The `GetCommunityTracks` query has no variables.
### Return Type
Recall that executing the `GetCommunityTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetCommunityTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetCommunityTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getCommunityTracks } from '@musically/dataconnect';


// Call the `getCommunityTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getCommunityTracks();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getCommunityTracks(dataConnect);

console.log(data.tracks);

// Or, you can use the `Promise` API.
getCommunityTracks().then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

### Using `GetCommunityTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getCommunityTracksRef } from '@musically/dataconnect';


// Call the `getCommunityTracksRef()` function to get a reference to the query.
const ref = getCommunityTracksRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getCommunityTracksRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.tracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

## GetCategories
You can execute the `GetCategories` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getCategories(options?: ExecuteQueryOptions): QueryPromise<GetCategoriesData, undefined>;

interface GetCategoriesRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetCategoriesData, undefined>;
}
export const getCategoriesRef: GetCategoriesRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getCategories(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetCategoriesData, undefined>;

interface GetCategoriesRef {
  ...
  (dc: DataConnect): QueryRef<GetCategoriesData, undefined>;
}
export const getCategoriesRef: GetCategoriesRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getCategoriesRef:
```typescript
const name = getCategoriesRef.operationName;
console.log(name);
```

### Variables
The `GetCategories` query has no variables.
### Return Type
Recall that executing the `GetCategories` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetCategoriesData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetCategoriesData {
  categories: ({
    id: string;
    name: string;
    type: CategoryType;
    colorHex?: string | null;
    imageUrl?: string | null;
  } & Category_Key)[];
}
```
### Using `GetCategories`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getCategories } from '@musically/dataconnect';


// Call the `getCategories()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getCategories();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getCategories(dataConnect);

console.log(data.categories);

// Or, you can use the `Promise` API.
getCategories().then((response) => {
  const data = response.data;
  console.log(data.categories);
});
```

### Using `GetCategories`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getCategoriesRef } from '@musically/dataconnect';


// Call the `getCategoriesRef()` function to get a reference to the query.
const ref = getCategoriesRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getCategoriesRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.categories);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.categories);
});
```

## GetPlaylists
You can execute the `GetPlaylists` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getPlaylists(options?: ExecuteQueryOptions): QueryPromise<GetPlaylistsData, undefined>;

interface GetPlaylistsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPlaylistsData, undefined>;
}
export const getPlaylistsRef: GetPlaylistsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getPlaylists(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistsData, undefined>;

interface GetPlaylistsRef {
  ...
  (dc: DataConnect): QueryRef<GetPlaylistsData, undefined>;
}
export const getPlaylistsRef: GetPlaylistsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getPlaylistsRef:
```typescript
const name = getPlaylistsRef.operationName;
console.log(name);
```

### Variables
The `GetPlaylists` query has no variables.
### Return Type
Recall that executing the `GetPlaylists` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetPlaylistsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetPlaylistsData {
  playlists: ({
    id: string;
    name: string;
    description?: string | null;
    coverUrl?: string | null;
    isPublic: boolean;
  } & Playlist_Key)[];
}
```
### Using `GetPlaylists`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getPlaylists } from '@musically/dataconnect';


// Call the `getPlaylists()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getPlaylists();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getPlaylists(dataConnect);

console.log(data.playlists);

// Or, you can use the `Promise` API.
getPlaylists().then((response) => {
  const data = response.data;
  console.log(data.playlists);
});
```

### Using `GetPlaylists`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getPlaylistsRef } from '@musically/dataconnect';


// Call the `getPlaylistsRef()` function to get a reference to the query.
const ref = getPlaylistsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getPlaylistsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.playlists);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.playlists);
});
```

## GetAlbums
You can execute the `GetAlbums` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getAlbums(options?: ExecuteQueryOptions): QueryPromise<GetAlbumsData, undefined>;

interface GetAlbumsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetAlbumsData, undefined>;
}
export const getAlbumsRef: GetAlbumsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getAlbums(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetAlbumsData, undefined>;

interface GetAlbumsRef {
  ...
  (dc: DataConnect): QueryRef<GetAlbumsData, undefined>;
}
export const getAlbumsRef: GetAlbumsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getAlbumsRef:
```typescript
const name = getAlbumsRef.operationName;
console.log(name);
```

### Variables
The `GetAlbums` query has no variables.
### Return Type
Recall that executing the `GetAlbums` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetAlbumsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetAlbums`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getAlbums } from '@musically/dataconnect';


// Call the `getAlbums()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getAlbums();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getAlbums(dataConnect);

console.log(data.albums);

// Or, you can use the `Promise` API.
getAlbums().then((response) => {
  const data = response.data;
  console.log(data.albums);
});
```

### Using `GetAlbums`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getAlbumsRef } from '@musically/dataconnect';


// Call the `getAlbumsRef()` function to get a reference to the query.
const ref = getAlbumsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getAlbumsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.albums);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.albums);
});
```

## GetPodcasts
You can execute the `GetPodcasts` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getPodcasts(options?: ExecuteQueryOptions): QueryPromise<GetPodcastsData, undefined>;

interface GetPodcastsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPodcastsData, undefined>;
}
export const getPodcastsRef: GetPodcastsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getPodcasts(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPodcastsData, undefined>;

interface GetPodcastsRef {
  ...
  (dc: DataConnect): QueryRef<GetPodcastsData, undefined>;
}
export const getPodcastsRef: GetPodcastsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getPodcastsRef:
```typescript
const name = getPodcastsRef.operationName;
console.log(name);
```

### Variables
The `GetPodcasts` query has no variables.
### Return Type
Recall that executing the `GetPodcasts` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetPodcastsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetPodcastsData {
  shows: ({
    id: string;
    title: string;
    publisher: string;
    coverUrl?: string | null;
    description?: string | null;
  } & Show_Key)[];
}
```
### Using `GetPodcasts`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getPodcasts } from '@musically/dataconnect';


// Call the `getPodcasts()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getPodcasts();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getPodcasts(dataConnect);

console.log(data.shows);

// Or, you can use the `Promise` API.
getPodcasts().then((response) => {
  const data = response.data;
  console.log(data.shows);
});
```

### Using `GetPodcasts`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getPodcastsRef } from '@musically/dataconnect';


// Call the `getPodcastsRef()` function to get a reference to the query.
const ref = getPodcastsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getPodcastsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.shows);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.shows);
});
```

## GetAudiobooks
You can execute the `GetAudiobooks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getAudiobooks(options?: ExecuteQueryOptions): QueryPromise<GetAudiobooksData, undefined>;

interface GetAudiobooksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetAudiobooksData, undefined>;
}
export const getAudiobooksRef: GetAudiobooksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getAudiobooks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetAudiobooksData, undefined>;

interface GetAudiobooksRef {
  ...
  (dc: DataConnect): QueryRef<GetAudiobooksData, undefined>;
}
export const getAudiobooksRef: GetAudiobooksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getAudiobooksRef:
```typescript
const name = getAudiobooksRef.operationName;
console.log(name);
```

### Variables
The `GetAudiobooks` query has no variables.
### Return Type
Recall that executing the `GetAudiobooks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetAudiobooksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetAudiobooks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getAudiobooks } from '@musically/dataconnect';


// Call the `getAudiobooks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getAudiobooks();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getAudiobooks(dataConnect);

console.log(data.audiobooks);

// Or, you can use the `Promise` API.
getAudiobooks().then((response) => {
  const data = response.data;
  console.log(data.audiobooks);
});
```

### Using `GetAudiobooks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getAudiobooksRef } from '@musically/dataconnect';


// Call the `getAudiobooksRef()` function to get a reference to the query.
const ref = getAudiobooksRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getAudiobooksRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.audiobooks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.audiobooks);
});
```

## GetUserSettings
You can execute the `GetUserSettings` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getUserSettings(options?: ExecuteQueryOptions): QueryPromise<GetUserSettingsData, undefined>;

interface GetUserSettingsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserSettingsData, undefined>;
}
export const getUserSettingsRef: GetUserSettingsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getUserSettings(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserSettingsData, undefined>;

interface GetUserSettingsRef {
  ...
  (dc: DataConnect): QueryRef<GetUserSettingsData, undefined>;
}
export const getUserSettingsRef: GetUserSettingsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getUserSettingsRef:
```typescript
const name = getUserSettingsRef.operationName;
console.log(name);
```

### Variables
The `GetUserSettings` query has no variables.
### Return Type
Recall that executing the `GetUserSettings` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetUserSettingsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetUserSettings`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getUserSettings } from '@musically/dataconnect';


// Call the `getUserSettings()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getUserSettings();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getUserSettings(dataConnect);

console.log(data.userSettings);

// Or, you can use the `Promise` API.
getUserSettings().then((response) => {
  const data = response.data;
  console.log(data.userSettings);
});
```

### Using `GetUserSettings`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getUserSettingsRef } from '@musically/dataconnect';


// Call the `getUserSettingsRef()` function to get a reference to the query.
const ref = getUserSettingsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getUserSettingsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.userSettings);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.userSettings);
});
```

## GetPaymentHistory
You can execute the `GetPaymentHistory` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getPaymentHistory(options?: ExecuteQueryOptions): QueryPromise<GetPaymentHistoryData, undefined>;

interface GetPaymentHistoryRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetPaymentHistoryData, undefined>;
}
export const getPaymentHistoryRef: GetPaymentHistoryRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getPaymentHistory(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetPaymentHistoryData, undefined>;

interface GetPaymentHistoryRef {
  ...
  (dc: DataConnect): QueryRef<GetPaymentHistoryData, undefined>;
}
export const getPaymentHistoryRef: GetPaymentHistoryRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getPaymentHistoryRef:
```typescript
const name = getPaymentHistoryRef.operationName;
console.log(name);
```

### Variables
The `GetPaymentHistory` query has no variables.
### Return Type
Recall that executing the `GetPaymentHistory` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetPaymentHistoryData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetPaymentHistory`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getPaymentHistory } from '@musically/dataconnect';


// Call the `getPaymentHistory()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getPaymentHistory();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getPaymentHistory(dataConnect);

console.log(data.paymentHistories);

// Or, you can use the `Promise` API.
getPaymentHistory().then((response) => {
  const data = response.data;
  console.log(data.paymentHistories);
});
```

### Using `GetPaymentHistory`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getPaymentHistoryRef } from '@musically/dataconnect';


// Call the `getPaymentHistoryRef()` function to get a reference to the query.
const ref = getPaymentHistoryRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getPaymentHistoryRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.paymentHistories);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.paymentHistories);
});
```

## GetLikedTracks
You can execute the `GetLikedTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getLikedTracks(options?: ExecuteQueryOptions): QueryPromise<GetLikedTracksData, undefined>;

interface GetLikedTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetLikedTracksData, undefined>;
}
export const getLikedTracksRef: GetLikedTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getLikedTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetLikedTracksData, undefined>;

interface GetLikedTracksRef {
  ...
  (dc: DataConnect): QueryRef<GetLikedTracksData, undefined>;
}
export const getLikedTracksRef: GetLikedTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getLikedTracksRef:
```typescript
const name = getLikedTracksRef.operationName;
console.log(name);
```

### Variables
The `GetLikedTracks` query has no variables.
### Return Type
Recall that executing the `GetLikedTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetLikedTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetLikedTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getLikedTracks } from '@musically/dataconnect';


// Call the `getLikedTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getLikedTracks();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getLikedTracks(dataConnect);

console.log(data.likedTracks);

// Or, you can use the `Promise` API.
getLikedTracks().then((response) => {
  const data = response.data;
  console.log(data.likedTracks);
});
```

### Using `GetLikedTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getLikedTracksRef } from '@musically/dataconnect';


// Call the `getLikedTracksRef()` function to get a reference to the query.
const ref = getLikedTracksRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getLikedTracksRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.likedTracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.likedTracks);
});
```

## GetBookmarkedTracks
You can execute the `GetBookmarkedTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getBookmarkedTracks(options?: ExecuteQueryOptions): QueryPromise<GetBookmarkedTracksData, undefined>;

interface GetBookmarkedTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetBookmarkedTracksData, undefined>;
}
export const getBookmarkedTracksRef: GetBookmarkedTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getBookmarkedTracks(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetBookmarkedTracksData, undefined>;

interface GetBookmarkedTracksRef {
  ...
  (dc: DataConnect): QueryRef<GetBookmarkedTracksData, undefined>;
}
export const getBookmarkedTracksRef: GetBookmarkedTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getBookmarkedTracksRef:
```typescript
const name = getBookmarkedTracksRef.operationName;
console.log(name);
```

### Variables
The `GetBookmarkedTracks` query has no variables.
### Return Type
Recall that executing the `GetBookmarkedTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetBookmarkedTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetBookmarkedTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getBookmarkedTracks } from '@musically/dataconnect';


// Call the `getBookmarkedTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getBookmarkedTracks();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getBookmarkedTracks(dataConnect);

console.log(data.bookmarkedTracks);

// Or, you can use the `Promise` API.
getBookmarkedTracks().then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTracks);
});
```

### Using `GetBookmarkedTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getBookmarkedTracksRef } from '@musically/dataconnect';


// Call the `getBookmarkedTracksRef()` function to get a reference to the query.
const ref = getBookmarkedTracksRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getBookmarkedTracksRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.bookmarkedTracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTracks);
});
```

## GetUserCameraCaptures
You can execute the `GetUserCameraCaptures` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getUserCameraCaptures(options?: ExecuteQueryOptions): QueryPromise<GetUserCameraCapturesData, undefined>;

interface GetUserCameraCapturesRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserCameraCapturesData, undefined>;
}
export const getUserCameraCapturesRef: GetUserCameraCapturesRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getUserCameraCaptures(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserCameraCapturesData, undefined>;

interface GetUserCameraCapturesRef {
  ...
  (dc: DataConnect): QueryRef<GetUserCameraCapturesData, undefined>;
}
export const getUserCameraCapturesRef: GetUserCameraCapturesRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getUserCameraCapturesRef:
```typescript
const name = getUserCameraCapturesRef.operationName;
console.log(name);
```

### Variables
The `GetUserCameraCaptures` query has no variables.
### Return Type
Recall that executing the `GetUserCameraCaptures` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetUserCameraCapturesData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetUserCameraCaptures`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getUserCameraCaptures } from '@musically/dataconnect';


// Call the `getUserCameraCaptures()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getUserCameraCaptures();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getUserCameraCaptures(dataConnect);

console.log(data.cameraCaptures);

// Or, you can use the `Promise` API.
getUserCameraCaptures().then((response) => {
  const data = response.data;
  console.log(data.cameraCaptures);
});
```

### Using `GetUserCameraCaptures`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getUserCameraCapturesRef } from '@musically/dataconnect';


// Call the `getUserCameraCapturesRef()` function to get a reference to the query.
const ref = getUserCameraCapturesRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getUserCameraCapturesRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.cameraCaptures);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.cameraCaptures);
});
```

## GetUserVideoDigestions
You can execute the `GetUserVideoDigestions` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getUserVideoDigestions(options?: ExecuteQueryOptions): QueryPromise<GetUserVideoDigestionsData, undefined>;

interface GetUserVideoDigestionsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUserVideoDigestionsData, undefined>;
}
export const getUserVideoDigestionsRef: GetUserVideoDigestionsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getUserVideoDigestions(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUserVideoDigestionsData, undefined>;

interface GetUserVideoDigestionsRef {
  ...
  (dc: DataConnect): QueryRef<GetUserVideoDigestionsData, undefined>;
}
export const getUserVideoDigestionsRef: GetUserVideoDigestionsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getUserVideoDigestionsRef:
```typescript
const name = getUserVideoDigestionsRef.operationName;
console.log(name);
```

### Variables
The `GetUserVideoDigestions` query has no variables.
### Return Type
Recall that executing the `GetUserVideoDigestions` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetUserVideoDigestionsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetUserVideoDigestions`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getUserVideoDigestions } from '@musically/dataconnect';


// Call the `getUserVideoDigestions()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getUserVideoDigestions();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getUserVideoDigestions(dataConnect);

console.log(data.videoDigestions);

// Or, you can use the `Promise` API.
getUserVideoDigestions().then((response) => {
  const data = response.data;
  console.log(data.videoDigestions);
});
```

### Using `GetUserVideoDigestions`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getUserVideoDigestionsRef } from '@musically/dataconnect';


// Call the `getUserVideoDigestionsRef()` function to get a reference to the query.
const ref = getUserVideoDigestionsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getUserVideoDigestionsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.videoDigestions);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.videoDigestions);
});
```

## GetPlaylistTracks
You can execute the `GetPlaylistTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getPlaylistTracks(vars: GetPlaylistTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistTracksData, GetPlaylistTracksVariables>;

interface GetPlaylistTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetPlaylistTracksVariables): QueryRef<GetPlaylistTracksData, GetPlaylistTracksVariables>;
}
export const getPlaylistTracksRef: GetPlaylistTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getPlaylistTracks(dc: DataConnect, vars: GetPlaylistTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetPlaylistTracksData, GetPlaylistTracksVariables>;

interface GetPlaylistTracksRef {
  ...
  (dc: DataConnect, vars: GetPlaylistTracksVariables): QueryRef<GetPlaylistTracksData, GetPlaylistTracksVariables>;
}
export const getPlaylistTracksRef: GetPlaylistTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getPlaylistTracksRef:
```typescript
const name = getPlaylistTracksRef.operationName;
console.log(name);
```

### Variables
The `GetPlaylistTracks` query requires an argument of type `GetPlaylistTracksVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetPlaylistTracksVariables {
  playlistId: string;
}
```
### Return Type
Recall that executing the `GetPlaylistTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetPlaylistTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetPlaylistTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getPlaylistTracks, GetPlaylistTracksVariables } from '@musically/dataconnect';

// The `GetPlaylistTracks` query requires an argument of type `GetPlaylistTracksVariables`:
const getPlaylistTracksVars: GetPlaylistTracksVariables = {
  playlistId: ..., 
};

// Call the `getPlaylistTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getPlaylistTracks(getPlaylistTracksVars);
// Variables can be defined inline as well.
const { data } = await getPlaylistTracks({ playlistId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getPlaylistTracks(dataConnect, getPlaylistTracksVars);

console.log(data.playlistEntries);

// Or, you can use the `Promise` API.
getPlaylistTracks(getPlaylistTracksVars).then((response) => {
  const data = response.data;
  console.log(data.playlistEntries);
});
```

### Using `GetPlaylistTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getPlaylistTracksRef, GetPlaylistTracksVariables } from '@musically/dataconnect';

// The `GetPlaylistTracks` query requires an argument of type `GetPlaylistTracksVariables`:
const getPlaylistTracksVars: GetPlaylistTracksVariables = {
  playlistId: ..., 
};

// Call the `getPlaylistTracksRef()` function to get a reference to the query.
const ref = getPlaylistTracksRef(getPlaylistTracksVars);
// Variables can be defined inline as well.
const ref = getPlaylistTracksRef({ playlistId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getPlaylistTracksRef(dataConnect, getPlaylistTracksVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.playlistEntries);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.playlistEntries);
});
```

## GetCategoryTracks
You can execute the `GetCategoryTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getCategoryTracks(vars: GetCategoryTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetCategoryTracksData, GetCategoryTracksVariables>;

interface GetCategoryTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetCategoryTracksVariables): QueryRef<GetCategoryTracksData, GetCategoryTracksVariables>;
}
export const getCategoryTracksRef: GetCategoryTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getCategoryTracks(dc: DataConnect, vars: GetCategoryTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetCategoryTracksData, GetCategoryTracksVariables>;

interface GetCategoryTracksRef {
  ...
  (dc: DataConnect, vars: GetCategoryTracksVariables): QueryRef<GetCategoryTracksData, GetCategoryTracksVariables>;
}
export const getCategoryTracksRef: GetCategoryTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getCategoryTracksRef:
```typescript
const name = getCategoryTracksRef.operationName;
console.log(name);
```

### Variables
The `GetCategoryTracks` query requires an argument of type `GetCategoryTracksVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetCategoryTracksVariables {
  categoryId: string;
}
```
### Return Type
Recall that executing the `GetCategoryTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetCategoryTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetCategoryTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getCategoryTracks, GetCategoryTracksVariables } from '@musically/dataconnect';

// The `GetCategoryTracks` query requires an argument of type `GetCategoryTracksVariables`:
const getCategoryTracksVars: GetCategoryTracksVariables = {
  categoryId: ..., 
};

// Call the `getCategoryTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getCategoryTracks(getCategoryTracksVars);
// Variables can be defined inline as well.
const { data } = await getCategoryTracks({ categoryId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getCategoryTracks(dataConnect, getCategoryTracksVars);

console.log(data.musicCategories);

// Or, you can use the `Promise` API.
getCategoryTracks(getCategoryTracksVars).then((response) => {
  const data = response.data;
  console.log(data.musicCategories);
});
```

### Using `GetCategoryTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getCategoryTracksRef, GetCategoryTracksVariables } from '@musically/dataconnect';

// The `GetCategoryTracks` query requires an argument of type `GetCategoryTracksVariables`:
const getCategoryTracksVars: GetCategoryTracksVariables = {
  categoryId: ..., 
};

// Call the `getCategoryTracksRef()` function to get a reference to the query.
const ref = getCategoryTracksRef(getCategoryTracksVars);
// Variables can be defined inline as well.
const ref = getCategoryTracksRef({ categoryId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getCategoryTracksRef(dataConnect, getCategoryTracksVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.musicCategories);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.musicCategories);
});
```

## GetAlbumTracks
You can execute the `GetAlbumTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getAlbumTracks(vars: GetAlbumTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetAlbumTracksData, GetAlbumTracksVariables>;

interface GetAlbumTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetAlbumTracksVariables): QueryRef<GetAlbumTracksData, GetAlbumTracksVariables>;
}
export const getAlbumTracksRef: GetAlbumTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getAlbumTracks(dc: DataConnect, vars: GetAlbumTracksVariables, options?: ExecuteQueryOptions): QueryPromise<GetAlbumTracksData, GetAlbumTracksVariables>;

interface GetAlbumTracksRef {
  ...
  (dc: DataConnect, vars: GetAlbumTracksVariables): QueryRef<GetAlbumTracksData, GetAlbumTracksVariables>;
}
export const getAlbumTracksRef: GetAlbumTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getAlbumTracksRef:
```typescript
const name = getAlbumTracksRef.operationName;
console.log(name);
```

### Variables
The `GetAlbumTracks` query requires an argument of type `GetAlbumTracksVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetAlbumTracksVariables {
  albumId: string;
}
```
### Return Type
Recall that executing the `GetAlbumTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetAlbumTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetAlbumTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getAlbumTracks, GetAlbumTracksVariables } from '@musically/dataconnect';

// The `GetAlbumTracks` query requires an argument of type `GetAlbumTracksVariables`:
const getAlbumTracksVars: GetAlbumTracksVariables = {
  albumId: ..., 
};

// Call the `getAlbumTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getAlbumTracks(getAlbumTracksVars);
// Variables can be defined inline as well.
const { data } = await getAlbumTracks({ albumId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getAlbumTracks(dataConnect, getAlbumTracksVars);

console.log(data.tracks);

// Or, you can use the `Promise` API.
getAlbumTracks(getAlbumTracksVars).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

### Using `GetAlbumTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getAlbumTracksRef, GetAlbumTracksVariables } from '@musically/dataconnect';

// The `GetAlbumTracks` query requires an argument of type `GetAlbumTracksVariables`:
const getAlbumTracksVars: GetAlbumTracksVariables = {
  albumId: ..., 
};

// Call the `getAlbumTracksRef()` function to get a reference to the query.
const ref = getAlbumTracksRef(getAlbumTracksVars);
// Variables can be defined inline as well.
const ref = getAlbumTracksRef({ albumId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getAlbumTracksRef(dataConnect, getAlbumTracksVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.tracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

## SearchTracks
You can execute the `SearchTracks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
searchTracks(vars: SearchTracksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchTracksData, SearchTracksVariables>;

interface SearchTracksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchTracksVariables): QueryRef<SearchTracksData, SearchTracksVariables>;
}
export const searchTracksRef: SearchTracksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
searchTracks(dc: DataConnect, vars: SearchTracksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchTracksData, SearchTracksVariables>;

interface SearchTracksRef {
  ...
  (dc: DataConnect, vars: SearchTracksVariables): QueryRef<SearchTracksData, SearchTracksVariables>;
}
export const searchTracksRef: SearchTracksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the searchTracksRef:
```typescript
const name = searchTracksRef.operationName;
console.log(name);
```

### Variables
The `SearchTracks` query requires an argument of type `SearchTracksVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SearchTracksVariables {
  query: string;
}
```
### Return Type
Recall that executing the `SearchTracks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SearchTracksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `SearchTracks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, searchTracks, SearchTracksVariables } from '@musically/dataconnect';

// The `SearchTracks` query requires an argument of type `SearchTracksVariables`:
const searchTracksVars: SearchTracksVariables = {
  query: ..., 
};

// Call the `searchTracks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await searchTracks(searchTracksVars);
// Variables can be defined inline as well.
const { data } = await searchTracks({ query: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await searchTracks(dataConnect, searchTracksVars);

console.log(data.tracks);

// Or, you can use the `Promise` API.
searchTracks(searchTracksVars).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

### Using `SearchTracks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, searchTracksRef, SearchTracksVariables } from '@musically/dataconnect';

// The `SearchTracks` query requires an argument of type `SearchTracksVariables`:
const searchTracksVars: SearchTracksVariables = {
  query: ..., 
};

// Call the `searchTracksRef()` function to get a reference to the query.
const ref = searchTracksRef(searchTracksVars);
// Variables can be defined inline as well.
const ref = searchTracksRef({ query: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = searchTracksRef(dataConnect, searchTracksVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.tracks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.tracks);
});
```

## SearchPodcasts
You can execute the `SearchPodcasts` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
searchPodcasts(vars: SearchPodcastsVariables, options?: ExecuteQueryOptions): QueryPromise<SearchPodcastsData, SearchPodcastsVariables>;

interface SearchPodcastsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchPodcastsVariables): QueryRef<SearchPodcastsData, SearchPodcastsVariables>;
}
export const searchPodcastsRef: SearchPodcastsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
searchPodcasts(dc: DataConnect, vars: SearchPodcastsVariables, options?: ExecuteQueryOptions): QueryPromise<SearchPodcastsData, SearchPodcastsVariables>;

interface SearchPodcastsRef {
  ...
  (dc: DataConnect, vars: SearchPodcastsVariables): QueryRef<SearchPodcastsData, SearchPodcastsVariables>;
}
export const searchPodcastsRef: SearchPodcastsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the searchPodcastsRef:
```typescript
const name = searchPodcastsRef.operationName;
console.log(name);
```

### Variables
The `SearchPodcasts` query requires an argument of type `SearchPodcastsVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SearchPodcastsVariables {
  query: string;
}
```
### Return Type
Recall that executing the `SearchPodcasts` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SearchPodcastsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SearchPodcastsData {
  shows: ({
    id: string;
    title: string;
    publisher: string;
    coverUrl?: string | null;
    description?: string | null;
  } & Show_Key)[];
}
```
### Using `SearchPodcasts`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, searchPodcasts, SearchPodcastsVariables } from '@musically/dataconnect';

// The `SearchPodcasts` query requires an argument of type `SearchPodcastsVariables`:
const searchPodcastsVars: SearchPodcastsVariables = {
  query: ..., 
};

// Call the `searchPodcasts()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await searchPodcasts(searchPodcastsVars);
// Variables can be defined inline as well.
const { data } = await searchPodcasts({ query: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await searchPodcasts(dataConnect, searchPodcastsVars);

console.log(data.shows);

// Or, you can use the `Promise` API.
searchPodcasts(searchPodcastsVars).then((response) => {
  const data = response.data;
  console.log(data.shows);
});
```

### Using `SearchPodcasts`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, searchPodcastsRef, SearchPodcastsVariables } from '@musically/dataconnect';

// The `SearchPodcasts` query requires an argument of type `SearchPodcastsVariables`:
const searchPodcastsVars: SearchPodcastsVariables = {
  query: ..., 
};

// Call the `searchPodcastsRef()` function to get a reference to the query.
const ref = searchPodcastsRef(searchPodcastsVars);
// Variables can be defined inline as well.
const ref = searchPodcastsRef({ query: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = searchPodcastsRef(dataConnect, searchPodcastsVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.shows);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.shows);
});
```

## SearchAudiobooks
You can execute the `SearchAudiobooks` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
searchAudiobooks(vars: SearchAudiobooksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchAudiobooksData, SearchAudiobooksVariables>;

interface SearchAudiobooksRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SearchAudiobooksVariables): QueryRef<SearchAudiobooksData, SearchAudiobooksVariables>;
}
export const searchAudiobooksRef: SearchAudiobooksRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
searchAudiobooks(dc: DataConnect, vars: SearchAudiobooksVariables, options?: ExecuteQueryOptions): QueryPromise<SearchAudiobooksData, SearchAudiobooksVariables>;

interface SearchAudiobooksRef {
  ...
  (dc: DataConnect, vars: SearchAudiobooksVariables): QueryRef<SearchAudiobooksData, SearchAudiobooksVariables>;
}
export const searchAudiobooksRef: SearchAudiobooksRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the searchAudiobooksRef:
```typescript
const name = searchAudiobooksRef.operationName;
console.log(name);
```

### Variables
The `SearchAudiobooks` query requires an argument of type `SearchAudiobooksVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SearchAudiobooksVariables {
  query: string;
}
```
### Return Type
Recall that executing the `SearchAudiobooks` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SearchAudiobooksData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `SearchAudiobooks`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, searchAudiobooks, SearchAudiobooksVariables } from '@musically/dataconnect';

// The `SearchAudiobooks` query requires an argument of type `SearchAudiobooksVariables`:
const searchAudiobooksVars: SearchAudiobooksVariables = {
  query: ..., 
};

// Call the `searchAudiobooks()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await searchAudiobooks(searchAudiobooksVars);
// Variables can be defined inline as well.
const { data } = await searchAudiobooks({ query: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await searchAudiobooks(dataConnect, searchAudiobooksVars);

console.log(data.audiobooks);

// Or, you can use the `Promise` API.
searchAudiobooks(searchAudiobooksVars).then((response) => {
  const data = response.data;
  console.log(data.audiobooks);
});
```

### Using `SearchAudiobooks`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, searchAudiobooksRef, SearchAudiobooksVariables } from '@musically/dataconnect';

// The `SearchAudiobooks` query requires an argument of type `SearchAudiobooksVariables`:
const searchAudiobooksVars: SearchAudiobooksVariables = {
  query: ..., 
};

// Call the `searchAudiobooksRef()` function to get a reference to the query.
const ref = searchAudiobooksRef(searchAudiobooksVars);
// Variables can be defined inline as well.
const ref = searchAudiobooksRef({ query: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = searchAudiobooksRef(dataConnect, searchAudiobooksVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.audiobooks);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.audiobooks);
});
```

## ListChatMessages
You can execute the `ListChatMessages` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listChatMessages(vars: ListChatMessagesVariables, options?: ExecuteQueryOptions): QueryPromise<ListChatMessagesData, ListChatMessagesVariables>;

interface ListChatMessagesRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: ListChatMessagesVariables): QueryRef<ListChatMessagesData, ListChatMessagesVariables>;
}
export const listChatMessagesRef: ListChatMessagesRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listChatMessages(dc: DataConnect, vars: ListChatMessagesVariables, options?: ExecuteQueryOptions): QueryPromise<ListChatMessagesData, ListChatMessagesVariables>;

interface ListChatMessagesRef {
  ...
  (dc: DataConnect, vars: ListChatMessagesVariables): QueryRef<ListChatMessagesData, ListChatMessagesVariables>;
}
export const listChatMessagesRef: ListChatMessagesRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listChatMessagesRef:
```typescript
const name = listChatMessagesRef.operationName;
console.log(name);
```

### Variables
The `ListChatMessages` query requires an argument of type `ListChatMessagesVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface ListChatMessagesVariables {
  userId: string;
}
```
### Return Type
Recall that executing the `ListChatMessages` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListChatMessagesData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListChatMessagesData {
  chatMessages: ({
    id: string;
    sender: string;
    text: string;
    createdAt: TimestampString;
  } & ChatMessage_Key)[];
}
```
### Using `ListChatMessages`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listChatMessages, ListChatMessagesVariables } from '@musically/dataconnect';

// The `ListChatMessages` query requires an argument of type `ListChatMessagesVariables`:
const listChatMessagesVars: ListChatMessagesVariables = {
  userId: ..., 
};

// Call the `listChatMessages()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listChatMessages(listChatMessagesVars);
// Variables can be defined inline as well.
const { data } = await listChatMessages({ userId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listChatMessages(dataConnect, listChatMessagesVars);

console.log(data.chatMessages);

// Or, you can use the `Promise` API.
listChatMessages(listChatMessagesVars).then((response) => {
  const data = response.data;
  console.log(data.chatMessages);
});
```

### Using `ListChatMessages`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listChatMessagesRef, ListChatMessagesVariables } from '@musically/dataconnect';

// The `ListChatMessages` query requires an argument of type `ListChatMessagesVariables`:
const listChatMessagesVars: ListChatMessagesVariables = {
  userId: ..., 
};

// Call the `listChatMessagesRef()` function to get a reference to the query.
const ref = listChatMessagesRef(listChatMessagesVars);
// Variables can be defined inline as well.
const ref = listChatMessagesRef({ userId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listChatMessagesRef(dataConnect, listChatMessagesVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.chatMessages);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.chatMessages);
});
```

## ListSubscriptionPlans
You can execute the `ListSubscriptionPlans` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listSubscriptionPlans(options?: ExecuteQueryOptions): QueryPromise<ListSubscriptionPlansData, undefined>;

interface ListSubscriptionPlansRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListSubscriptionPlansData, undefined>;
}
export const listSubscriptionPlansRef: ListSubscriptionPlansRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listSubscriptionPlans(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListSubscriptionPlansData, undefined>;

interface ListSubscriptionPlansRef {
  ...
  (dc: DataConnect): QueryRef<ListSubscriptionPlansData, undefined>;
}
export const listSubscriptionPlansRef: ListSubscriptionPlansRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listSubscriptionPlansRef:
```typescript
const name = listSubscriptionPlansRef.operationName;
console.log(name);
```

### Variables
The `ListSubscriptionPlans` query has no variables.
### Return Type
Recall that executing the `ListSubscriptionPlans` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListSubscriptionPlansData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `ListSubscriptionPlans`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listSubscriptionPlans } from '@musically/dataconnect';


// Call the `listSubscriptionPlans()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listSubscriptionPlans();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listSubscriptionPlans(dataConnect);

console.log(data.subscriptionPlans);

// Or, you can use the `Promise` API.
listSubscriptionPlans().then((response) => {
  const data = response.data;
  console.log(data.subscriptionPlans);
});
```

### Using `ListSubscriptionPlans`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listSubscriptionPlansRef } from '@musically/dataconnect';


// Call the `listSubscriptionPlansRef()` function to get a reference to the query.
const ref = listSubscriptionPlansRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listSubscriptionPlansRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.subscriptionPlans);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.subscriptionPlans);
});
```

## ListFaqItems
You can execute the `ListFaqItems` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listFaqItems(options?: ExecuteQueryOptions): QueryPromise<ListFaqItemsData, undefined>;

interface ListFaqItemsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListFaqItemsData, undefined>;
}
export const listFaqItemsRef: ListFaqItemsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listFaqItems(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListFaqItemsData, undefined>;

interface ListFaqItemsRef {
  ...
  (dc: DataConnect): QueryRef<ListFaqItemsData, undefined>;
}
export const listFaqItemsRef: ListFaqItemsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listFaqItemsRef:
```typescript
const name = listFaqItemsRef.operationName;
console.log(name);
```

### Variables
The `ListFaqItems` query has no variables.
### Return Type
Recall that executing the `ListFaqItems` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListFaqItemsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListFaqItemsData {
  faqItems: ({
    id: string;
    question: string;
    answer: string;
  } & FaqItem_Key)[];
}
```
### Using `ListFaqItems`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listFaqItems } from '@musically/dataconnect';


// Call the `listFaqItems()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listFaqItems();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listFaqItems(dataConnect);

console.log(data.faqItems);

// Or, you can use the `Promise` API.
listFaqItems().then((response) => {
  const data = response.data;
  console.log(data.faqItems);
});
```

### Using `ListFaqItems`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listFaqItemsRef } from '@musically/dataconnect';


// Call the `listFaqItemsRef()` function to get a reference to the query.
const ref = listFaqItemsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listFaqItemsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.faqItems);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.faqItems);
});
```

## ListAIPresets
You can execute the `ListAIPresets` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listAiPresets(options?: ExecuteQueryOptions): QueryPromise<ListAiPresetsData, undefined>;

interface ListAiPresetsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListAiPresetsData, undefined>;
}
export const listAiPresetsRef: ListAiPresetsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listAiPresets(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListAiPresetsData, undefined>;

interface ListAiPresetsRef {
  ...
  (dc: DataConnect): QueryRef<ListAiPresetsData, undefined>;
}
export const listAiPresetsRef: ListAiPresetsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listAiPresetsRef:
```typescript
const name = listAiPresetsRef.operationName;
console.log(name);
```

### Variables
The `ListAIPresets` query has no variables.
### Return Type
Recall that executing the `ListAIPresets` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListAiPresetsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListAiPresetsData {
  aIPresets: ({
    id: string;
    name: string;
    promptFragment: string;
    imageUrl?: string | null;
  } & AIPreset_Key)[];
}
```
### Using `ListAIPresets`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listAiPresets } from '@musically/dataconnect';


// Call the `listAiPresets()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listAiPresets();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listAiPresets(dataConnect);

console.log(data.aIPresets);

// Or, you can use the `Promise` API.
listAiPresets().then((response) => {
  const data = response.data;
  console.log(data.aIPresets);
});
```

### Using `ListAIPresets`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listAiPresetsRef } from '@musically/dataconnect';


// Call the `listAiPresetsRef()` function to get a reference to the query.
const ref = listAiPresetsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listAiPresetsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.aIPresets);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.aIPresets);
});
```

## ListFeatureHighlights
You can execute the `ListFeatureHighlights` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listFeatureHighlights(options?: ExecuteQueryOptions): QueryPromise<ListFeatureHighlightsData, undefined>;

interface ListFeatureHighlightsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListFeatureHighlightsData, undefined>;
}
export const listFeatureHighlightsRef: ListFeatureHighlightsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listFeatureHighlights(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListFeatureHighlightsData, undefined>;

interface ListFeatureHighlightsRef {
  ...
  (dc: DataConnect): QueryRef<ListFeatureHighlightsData, undefined>;
}
export const listFeatureHighlightsRef: ListFeatureHighlightsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listFeatureHighlightsRef:
```typescript
const name = listFeatureHighlightsRef.operationName;
console.log(name);
```

### Variables
The `ListFeatureHighlights` query has no variables.
### Return Type
Recall that executing the `ListFeatureHighlights` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListFeatureHighlightsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListFeatureHighlightsData {
  featureHighlights: ({
    id: string;
    iconName: string;
    title: string;
    description: string;
  } & FeatureHighlight_Key)[];
}
```
### Using `ListFeatureHighlights`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listFeatureHighlights } from '@musically/dataconnect';


// Call the `listFeatureHighlights()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listFeatureHighlights();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listFeatureHighlights(dataConnect);

console.log(data.featureHighlights);

// Or, you can use the `Promise` API.
listFeatureHighlights().then((response) => {
  const data = response.data;
  console.log(data.featureHighlights);
});
```

### Using `ListFeatureHighlights`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listFeatureHighlightsRef } from '@musically/dataconnect';


// Call the `listFeatureHighlightsRef()` function to get a reference to the query.
const ref = listFeatureHighlightsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listFeatureHighlightsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.featureHighlights);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.featureHighlights);
});
```

## ListHomeSections
You can execute the `ListHomeSections` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listHomeSections(options?: ExecuteQueryOptions): QueryPromise<ListHomeSectionsData, undefined>;

interface ListHomeSectionsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListHomeSectionsData, undefined>;
}
export const listHomeSectionsRef: ListHomeSectionsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listHomeSections(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListHomeSectionsData, undefined>;

interface ListHomeSectionsRef {
  ...
  (dc: DataConnect): QueryRef<ListHomeSectionsData, undefined>;
}
export const listHomeSectionsRef: ListHomeSectionsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listHomeSectionsRef:
```typescript
const name = listHomeSectionsRef.operationName;
console.log(name);
```

### Variables
The `ListHomeSections` query has no variables.
### Return Type
Recall that executing the `ListHomeSections` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListHomeSectionsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListHomeSectionsData {
  homeSections: ({
    id: string;
    title: string;
    orderIndex: number;
    route: string;
  } & HomeSection_Key)[];
}
```
### Using `ListHomeSections`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listHomeSections } from '@musically/dataconnect';


// Call the `listHomeSections()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listHomeSections();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listHomeSections(dataConnect);

console.log(data.homeSections);

// Or, you can use the `Promise` API.
listHomeSections().then((response) => {
  const data = response.data;
  console.log(data.homeSections);
});
```

### Using `ListHomeSections`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listHomeSectionsRef } from '@musically/dataconnect';


// Call the `listHomeSectionsRef()` function to get a reference to the query.
const ref = listHomeSectionsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listHomeSectionsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.homeSections);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.homeSections);
});
```

## GetUsers
You can execute the `GetUsers` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getUsers(options?: ExecuteQueryOptions): QueryPromise<GetUsersData, undefined>;

interface GetUsersRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<GetUsersData, undefined>;
}
export const getUsersRef: GetUsersRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getUsers(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<GetUsersData, undefined>;

interface GetUsersRef {
  ...
  (dc: DataConnect): QueryRef<GetUsersData, undefined>;
}
export const getUsersRef: GetUsersRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getUsersRef:
```typescript
const name = getUsersRef.operationName;
console.log(name);
```

### Variables
The `GetUsers` query has no variables.
### Return Type
Recall that executing the `GetUsers` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetUsersData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetUsersData {
  users: ({
    uid: string;
    username: string;
  } & User_Key)[];
}
```
### Using `GetUsers`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getUsers } from '@musically/dataconnect';


// Call the `getUsers()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getUsers();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getUsers(dataConnect);

console.log(data.users);

// Or, you can use the `Promise` API.
getUsers().then((response) => {
  const data = response.data;
  console.log(data.users);
});
```

### Using `GetUsers`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getUsersRef } from '@musically/dataconnect';


// Call the `getUsersRef()` function to get a reference to the query.
const ref = getUsersRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getUsersRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.users);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.users);
});
```

## GetTrack
You can execute the `GetTrack` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getTrack(vars: GetTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetTrackData, GetTrackVariables>;

interface GetTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetTrackVariables): QueryRef<GetTrackData, GetTrackVariables>;
}
export const getTrackRef: GetTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getTrack(dc: DataConnect, vars: GetTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetTrackData, GetTrackVariables>;

interface GetTrackRef {
  ...
  (dc: DataConnect, vars: GetTrackVariables): QueryRef<GetTrackData, GetTrackVariables>;
}
export const getTrackRef: GetTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getTrackRef:
```typescript
const name = getTrackRef.operationName;
console.log(name);
```

### Variables
The `GetTrack` query requires an argument of type `GetTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetTrackVariables {
  id: string;
}
```
### Return Type
Recall that executing the `GetTrack` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getTrack, GetTrackVariables } from '@musically/dataconnect';

// The `GetTrack` query requires an argument of type `GetTrackVariables`:
const getTrackVars: GetTrackVariables = {
  id: ..., 
};

// Call the `getTrack()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getTrack(getTrackVars);
// Variables can be defined inline as well.
const { data } = await getTrack({ id: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getTrack(dataConnect, getTrackVars);

console.log(data.track);

// Or, you can use the `Promise` API.
getTrack(getTrackVars).then((response) => {
  const data = response.data;
  console.log(data.track);
});
```

### Using `GetTrack`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getTrackRef, GetTrackVariables } from '@musically/dataconnect';

// The `GetTrack` query requires an argument of type `GetTrackVariables`:
const getTrackVars: GetTrackVariables = {
  id: ..., 
};

// Call the `getTrackRef()` function to get a reference to the query.
const ref = getTrackRef(getTrackVars);
// Variables can be defined inline as well.
const ref = getTrackRef({ id: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getTrackRef(dataConnect, getTrackVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.track);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.track);
});
```

## GetEpisodesForShow
You can execute the `GetEpisodesForShow` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getEpisodesForShow(vars: GetEpisodesForShowVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodesForShowData, GetEpisodesForShowVariables>;

interface GetEpisodesForShowRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetEpisodesForShowVariables): QueryRef<GetEpisodesForShowData, GetEpisodesForShowVariables>;
}
export const getEpisodesForShowRef: GetEpisodesForShowRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getEpisodesForShow(dc: DataConnect, vars: GetEpisodesForShowVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodesForShowData, GetEpisodesForShowVariables>;

interface GetEpisodesForShowRef {
  ...
  (dc: DataConnect, vars: GetEpisodesForShowVariables): QueryRef<GetEpisodesForShowData, GetEpisodesForShowVariables>;
}
export const getEpisodesForShowRef: GetEpisodesForShowRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getEpisodesForShowRef:
```typescript
const name = getEpisodesForShowRef.operationName;
console.log(name);
```

### Variables
The `GetEpisodesForShow` query requires an argument of type `GetEpisodesForShowVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetEpisodesForShowVariables {
  showId: string;
}
```
### Return Type
Recall that executing the `GetEpisodesForShow` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetEpisodesForShowData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetEpisodesForShow`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getEpisodesForShow, GetEpisodesForShowVariables } from '@musically/dataconnect';

// The `GetEpisodesForShow` query requires an argument of type `GetEpisodesForShowVariables`:
const getEpisodesForShowVars: GetEpisodesForShowVariables = {
  showId: ..., 
};

// Call the `getEpisodesForShow()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getEpisodesForShow(getEpisodesForShowVars);
// Variables can be defined inline as well.
const { data } = await getEpisodesForShow({ showId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getEpisodesForShow(dataConnect, getEpisodesForShowVars);

console.log(data.episodes);

// Or, you can use the `Promise` API.
getEpisodesForShow(getEpisodesForShowVars).then((response) => {
  const data = response.data;
  console.log(data.episodes);
});
```

### Using `GetEpisodesForShow`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getEpisodesForShowRef, GetEpisodesForShowVariables } from '@musically/dataconnect';

// The `GetEpisodesForShow` query requires an argument of type `GetEpisodesForShowVariables`:
const getEpisodesForShowVars: GetEpisodesForShowVariables = {
  showId: ..., 
};

// Call the `getEpisodesForShowRef()` function to get a reference to the query.
const ref = getEpisodesForShowRef(getEpisodesForShowVars);
// Variables can be defined inline as well.
const ref = getEpisodesForShowRef({ showId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getEpisodesForShowRef(dataConnect, getEpisodesForShowVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.episodes);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.episodes);
});
```

## GetEpisode
You can execute the `GetEpisode` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getEpisode(vars: GetEpisodeVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodeData, GetEpisodeVariables>;

interface GetEpisodeRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetEpisodeVariables): QueryRef<GetEpisodeData, GetEpisodeVariables>;
}
export const getEpisodeRef: GetEpisodeRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getEpisode(dc: DataConnect, vars: GetEpisodeVariables, options?: ExecuteQueryOptions): QueryPromise<GetEpisodeData, GetEpisodeVariables>;

interface GetEpisodeRef {
  ...
  (dc: DataConnect, vars: GetEpisodeVariables): QueryRef<GetEpisodeData, GetEpisodeVariables>;
}
export const getEpisodeRef: GetEpisodeRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getEpisodeRef:
```typescript
const name = getEpisodeRef.operationName;
console.log(name);
```

### Variables
The `GetEpisode` query requires an argument of type `GetEpisodeVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetEpisodeVariables {
  id: string;
}
```
### Return Type
Recall that executing the `GetEpisode` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetEpisodeData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
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
```
### Using `GetEpisode`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getEpisode, GetEpisodeVariables } from '@musically/dataconnect';

// The `GetEpisode` query requires an argument of type `GetEpisodeVariables`:
const getEpisodeVars: GetEpisodeVariables = {
  id: ..., 
};

// Call the `getEpisode()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getEpisode(getEpisodeVars);
// Variables can be defined inline as well.
const { data } = await getEpisode({ id: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getEpisode(dataConnect, getEpisodeVars);

console.log(data.episode);

// Or, you can use the `Promise` API.
getEpisode(getEpisodeVars).then((response) => {
  const data = response.data;
  console.log(data.episode);
});
```

### Using `GetEpisode`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getEpisodeRef, GetEpisodeVariables } from '@musically/dataconnect';

// The `GetEpisode` query requires an argument of type `GetEpisodeVariables`:
const getEpisodeVars: GetEpisodeVariables = {
  id: ..., 
};

// Call the `getEpisodeRef()` function to get a reference to the query.
const ref = getEpisodeRef(getEpisodeVars);
// Variables can be defined inline as well.
const ref = getEpisodeRef({ id: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getEpisodeRef(dataConnect, getEpisodeVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.episode);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.episode);
});
```

## ListInstruments
You can execute the `ListInstruments` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
listInstruments(options?: ExecuteQueryOptions): QueryPromise<ListInstrumentsData, undefined>;

interface ListInstrumentsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (): QueryRef<ListInstrumentsData, undefined>;
}
export const listInstrumentsRef: ListInstrumentsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
listInstruments(dc: DataConnect, options?: ExecuteQueryOptions): QueryPromise<ListInstrumentsData, undefined>;

interface ListInstrumentsRef {
  ...
  (dc: DataConnect): QueryRef<ListInstrumentsData, undefined>;
}
export const listInstrumentsRef: ListInstrumentsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the listInstrumentsRef:
```typescript
const name = listInstrumentsRef.operationName;
console.log(name);
```

### Variables
The `ListInstruments` query has no variables.
### Return Type
Recall that executing the `ListInstruments` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `ListInstrumentsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface ListInstrumentsData {
  instruments: ({
    name: string;
    iconUrl?: string | null;
  } & Instrument_Key)[];
}
```
### Using `ListInstruments`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, listInstruments } from '@musically/dataconnect';


// Call the `listInstruments()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await listInstruments();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await listInstruments(dataConnect);

console.log(data.instruments);

// Or, you can use the `Promise` API.
listInstruments().then((response) => {
  const data = response.data;
  console.log(data.instruments);
});
```

### Using `ListInstruments`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, listInstrumentsRef } from '@musically/dataconnect';


// Call the `listInstrumentsRef()` function to get a reference to the query.
const ref = listInstrumentsRef();

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = listInstrumentsRef(dataConnect);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.instruments);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.instruments);
});
```

## GetCoverArtForTrack
You can execute the `GetCoverArtForTrack` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getCoverArtForTrack(vars: GetCoverArtForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;

interface GetCoverArtForTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetCoverArtForTrackVariables): QueryRef<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;
}
export const getCoverArtForTrackRef: GetCoverArtForTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getCoverArtForTrack(dc: DataConnect, vars: GetCoverArtForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;

interface GetCoverArtForTrackRef {
  ...
  (dc: DataConnect, vars: GetCoverArtForTrackVariables): QueryRef<GetCoverArtForTrackData, GetCoverArtForTrackVariables>;
}
export const getCoverArtForTrackRef: GetCoverArtForTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getCoverArtForTrackRef:
```typescript
const name = getCoverArtForTrackRef.operationName;
console.log(name);
```

### Variables
The `GetCoverArtForTrack` query requires an argument of type `GetCoverArtForTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetCoverArtForTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `GetCoverArtForTrack` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetCoverArtForTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetCoverArtForTrackData {
  coverArts: ({
    id: string;
    imageUrl: string;
    promptUsed?: string | null;
    stylePreset?: string | null;
    createdAt: TimestampString;
  } & CoverArt_Key)[];
}
```
### Using `GetCoverArtForTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getCoverArtForTrack, GetCoverArtForTrackVariables } from '@musically/dataconnect';

// The `GetCoverArtForTrack` query requires an argument of type `GetCoverArtForTrackVariables`:
const getCoverArtForTrackVars: GetCoverArtForTrackVariables = {
  trackId: ..., 
};

// Call the `getCoverArtForTrack()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getCoverArtForTrack(getCoverArtForTrackVars);
// Variables can be defined inline as well.
const { data } = await getCoverArtForTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getCoverArtForTrack(dataConnect, getCoverArtForTrackVars);

console.log(data.coverArts);

// Or, you can use the `Promise` API.
getCoverArtForTrack(getCoverArtForTrackVars).then((response) => {
  const data = response.data;
  console.log(data.coverArts);
});
```

### Using `GetCoverArtForTrack`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getCoverArtForTrackRef, GetCoverArtForTrackVariables } from '@musically/dataconnect';

// The `GetCoverArtForTrack` query requires an argument of type `GetCoverArtForTrackVariables`:
const getCoverArtForTrackVars: GetCoverArtForTrackVariables = {
  trackId: ..., 
};

// Call the `getCoverArtForTrackRef()` function to get a reference to the query.
const ref = getCoverArtForTrackRef(getCoverArtForTrackVars);
// Variables can be defined inline as well.
const ref = getCoverArtForTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getCoverArtForTrackRef(dataConnect, getCoverArtForTrackVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.coverArts);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.coverArts);
});
```

## GetMusicVideoForTrack
You can execute the `GetMusicVideoForTrack` query using the following action shortcut function, or by calling `executeQuery()` after calling the following `QueryRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
getMusicVideoForTrack(vars: GetMusicVideoForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;

interface GetMusicVideoForTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: GetMusicVideoForTrackVariables): QueryRef<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;
}
export const getMusicVideoForTrackRef: GetMusicVideoForTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `QueryRef` function.
```typescript
getMusicVideoForTrack(dc: DataConnect, vars: GetMusicVideoForTrackVariables, options?: ExecuteQueryOptions): QueryPromise<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;

interface GetMusicVideoForTrackRef {
  ...
  (dc: DataConnect, vars: GetMusicVideoForTrackVariables): QueryRef<GetMusicVideoForTrackData, GetMusicVideoForTrackVariables>;
}
export const getMusicVideoForTrackRef: GetMusicVideoForTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the getMusicVideoForTrackRef:
```typescript
const name = getMusicVideoForTrackRef.operationName;
console.log(name);
```

### Variables
The `GetMusicVideoForTrack` query requires an argument of type `GetMusicVideoForTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface GetMusicVideoForTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `GetMusicVideoForTrack` query returns a `QueryPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `GetMusicVideoForTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface GetMusicVideoForTrackData {
  musicVideos: ({
    id: string;
    videoUrl: string;
    promptUsed?: string | null;
    motionPreset?: string | null;
    createdAt: TimestampString;
  } & MusicVideo_Key)[];
}
```
### Using `GetMusicVideoForTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, getMusicVideoForTrack, GetMusicVideoForTrackVariables } from '@musically/dataconnect';

// The `GetMusicVideoForTrack` query requires an argument of type `GetMusicVideoForTrackVariables`:
const getMusicVideoForTrackVars: GetMusicVideoForTrackVariables = {
  trackId: ..., 
};

// Call the `getMusicVideoForTrack()` function to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await getMusicVideoForTrack(getMusicVideoForTrackVars);
// Variables can be defined inline as well.
const { data } = await getMusicVideoForTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await getMusicVideoForTrack(dataConnect, getMusicVideoForTrackVars);

console.log(data.musicVideos);

// Or, you can use the `Promise` API.
getMusicVideoForTrack(getMusicVideoForTrackVars).then((response) => {
  const data = response.data;
  console.log(data.musicVideos);
});
```

### Using `GetMusicVideoForTrack`'s `QueryRef` function

```typescript
import { getDataConnect, executeQuery } from 'firebase/data-connect';
import { connectorConfig, getMusicVideoForTrackRef, GetMusicVideoForTrackVariables } from '@musically/dataconnect';

// The `GetMusicVideoForTrack` query requires an argument of type `GetMusicVideoForTrackVariables`:
const getMusicVideoForTrackVars: GetMusicVideoForTrackVariables = {
  trackId: ..., 
};

// Call the `getMusicVideoForTrackRef()` function to get a reference to the query.
const ref = getMusicVideoForTrackRef(getMusicVideoForTrackVars);
// Variables can be defined inline as well.
const ref = getMusicVideoForTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `QueryRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = getMusicVideoForTrackRef(dataConnect, getMusicVideoForTrackVars);

// Call `executeQuery()` on the reference to execute the query.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeQuery(ref);

console.log(data.musicVideos);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.musicVideos);
});
```

# Mutations

There are two ways to execute a Data Connect Mutation using the generated Web SDK:
- Using a Mutation Reference function, which returns a `MutationRef`
  - The `MutationRef` can be used as an argument to `executeMutation()`, which will execute the Mutation and return a `MutationPromise`
- Using an action shortcut function, which returns a `MutationPromise`
  - Calling the action shortcut function will execute the Mutation and return a `MutationPromise`

The following is true for both the action shortcut function and the `MutationRef` function:
- The `MutationPromise` returned will resolve to the result of the Mutation once it has finished executing
- If the Mutation accepts arguments, both the action shortcut function and the `MutationRef` function accept a single argument: an object that contains all the required variables (and the optional variables) for the Mutation
- Both functions can be called with or without passing in a `DataConnect` instance as an argument. If no `DataConnect` argument is passed in, then the generated SDK will call `getDataConnect(connectorConfig)` behind the scenes for you.

Below are examples of how to use the `default` connector's generated functions to execute each mutation. You can also follow the examples from the [Data Connect documentation](https://firebase.google.com/docs/data-connect/web-sdk#using-mutations).

## CreateTrack
You can execute the `CreateTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createTrack(vars: CreateTrackVariables): MutationPromise<CreateTrackData, CreateTrackVariables>;

interface CreateTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateTrackVariables): MutationRef<CreateTrackData, CreateTrackVariables>;
}
export const createTrackRef: CreateTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createTrack(dc: DataConnect, vars: CreateTrackVariables): MutationPromise<CreateTrackData, CreateTrackVariables>;

interface CreateTrackRef {
  ...
  (dc: DataConnect, vars: CreateTrackVariables): MutationRef<CreateTrackData, CreateTrackVariables>;
}
export const createTrackRef: CreateTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createTrackRef:
```typescript
const name = createTrackRef.operationName;
console.log(name);
```

### Variables
The `CreateTrack` mutation requires an argument of type `CreateTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateTrackVariables {
  title: string;
  albumId?: string | null;
  audioUrl: string;
  coverUrl?: string | null;
  durationMs?: number | null;
  prompt?: string | null;
  isCommunity: boolean;
}
```
### Return Type
Recall that executing the `CreateTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateTrackData {
  track_insert: Track_Key;
}
```
### Using `CreateTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createTrack, CreateTrackVariables } from '@musically/dataconnect';

// The `CreateTrack` mutation requires an argument of type `CreateTrackVariables`:
const createTrackVars: CreateTrackVariables = {
  title: ..., 
  albumId: ..., // optional
  audioUrl: ..., 
  coverUrl: ..., // optional
  durationMs: ..., // optional
  prompt: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createTrack(createTrackVars);
// Variables can be defined inline as well.
const { data } = await createTrack({ title: ..., albumId: ..., audioUrl: ..., coverUrl: ..., durationMs: ..., prompt: ..., isCommunity: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createTrack(dataConnect, createTrackVars);

console.log(data.track_insert);

// Or, you can use the `Promise` API.
createTrack(createTrackVars).then((response) => {
  const data = response.data;
  console.log(data.track_insert);
});
```

### Using `CreateTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createTrackRef, CreateTrackVariables } from '@musically/dataconnect';

// The `CreateTrack` mutation requires an argument of type `CreateTrackVariables`:
const createTrackVars: CreateTrackVariables = {
  title: ..., 
  albumId: ..., // optional
  audioUrl: ..., 
  coverUrl: ..., // optional
  durationMs: ..., // optional
  prompt: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrackRef()` function to get a reference to the mutation.
const ref = createTrackRef(createTrackVars);
// Variables can be defined inline as well.
const ref = createTrackRef({ title: ..., albumId: ..., audioUrl: ..., coverUrl: ..., durationMs: ..., prompt: ..., isCommunity: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createTrackRef(dataConnect, createTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.track_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.track_insert);
});
```

## UpsertUser
You can execute the `UpsertUser` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
upsertUser(vars: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

interface UpsertUserRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
}
export const upsertUserRef: UpsertUserRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
upsertUser(dc: DataConnect, vars: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

interface UpsertUserRef {
  ...
  (dc: DataConnect, vars: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
}
export const upsertUserRef: UpsertUserRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the upsertUserRef:
```typescript
const name = upsertUserRef.operationName;
console.log(name);
```

### Variables
The `UpsertUser` mutation requires an argument of type `UpsertUserVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpsertUserVariables {
  displayName?: string | null;
  username: string;
  email: string;
}
```
### Return Type
Recall that executing the `UpsertUser` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpsertUserData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpsertUserData {
  user_upsert: User_Key;
}
```
### Using `UpsertUser`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, upsertUser, UpsertUserVariables } from '@musically/dataconnect';

// The `UpsertUser` mutation requires an argument of type `UpsertUserVariables`:
const upsertUserVars: UpsertUserVariables = {
  displayName: ..., // optional
  username: ..., 
  email: ..., 
};

// Call the `upsertUser()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await upsertUser(upsertUserVars);
// Variables can be defined inline as well.
const { data } = await upsertUser({ displayName: ..., username: ..., email: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await upsertUser(dataConnect, upsertUserVars);

console.log(data.user_upsert);

// Or, you can use the `Promise` API.
upsertUser(upsertUserVars).then((response) => {
  const data = response.data;
  console.log(data.user_upsert);
});
```

### Using `UpsertUser`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, upsertUserRef, UpsertUserVariables } from '@musically/dataconnect';

// The `UpsertUser` mutation requires an argument of type `UpsertUserVariables`:
const upsertUserVars: UpsertUserVariables = {
  displayName: ..., // optional
  username: ..., 
  email: ..., 
};

// Call the `upsertUserRef()` function to get a reference to the mutation.
const ref = upsertUserRef(upsertUserVars);
// Variables can be defined inline as well.
const ref = upsertUserRef({ displayName: ..., username: ..., email: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = upsertUserRef(dataConnect, upsertUserVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.user_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.user_upsert);
});
```

## CreatePlaylist
You can execute the `CreatePlaylist` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createPlaylist(vars: CreatePlaylistVariables): MutationPromise<CreatePlaylistData, CreatePlaylistVariables>;

interface CreatePlaylistRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreatePlaylistVariables): MutationRef<CreatePlaylistData, CreatePlaylistVariables>;
}
export const createPlaylistRef: CreatePlaylistRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createPlaylist(dc: DataConnect, vars: CreatePlaylistVariables): MutationPromise<CreatePlaylistData, CreatePlaylistVariables>;

interface CreatePlaylistRef {
  ...
  (dc: DataConnect, vars: CreatePlaylistVariables): MutationRef<CreatePlaylistData, CreatePlaylistVariables>;
}
export const createPlaylistRef: CreatePlaylistRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createPlaylistRef:
```typescript
const name = createPlaylistRef.operationName;
console.log(name);
```

### Variables
The `CreatePlaylist` mutation requires an argument of type `CreatePlaylistVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreatePlaylistVariables {
  name: string;
  description?: string | null;
}
```
### Return Type
Recall that executing the `CreatePlaylist` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreatePlaylistData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreatePlaylistData {
  playlist_insert: Playlist_Key;
}
```
### Using `CreatePlaylist`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createPlaylist, CreatePlaylistVariables } from '@musically/dataconnect';

// The `CreatePlaylist` mutation requires an argument of type `CreatePlaylistVariables`:
const createPlaylistVars: CreatePlaylistVariables = {
  name: ..., 
  description: ..., // optional
};

// Call the `createPlaylist()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createPlaylist(createPlaylistVars);
// Variables can be defined inline as well.
const { data } = await createPlaylist({ name: ..., description: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createPlaylist(dataConnect, createPlaylistVars);

console.log(data.playlist_insert);

// Or, you can use the `Promise` API.
createPlaylist(createPlaylistVars).then((response) => {
  const data = response.data;
  console.log(data.playlist_insert);
});
```

### Using `CreatePlaylist`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createPlaylistRef, CreatePlaylistVariables } from '@musically/dataconnect';

// The `CreatePlaylist` mutation requires an argument of type `CreatePlaylistVariables`:
const createPlaylistVars: CreatePlaylistVariables = {
  name: ..., 
  description: ..., // optional
};

// Call the `createPlaylistRef()` function to get a reference to the mutation.
const ref = createPlaylistRef(createPlaylistVars);
// Variables can be defined inline as well.
const ref = createPlaylistRef({ name: ..., description: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createPlaylistRef(dataConnect, createPlaylistVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.playlist_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.playlist_insert);
});
```

## AddTrackToPlaylist
You can execute the `AddTrackToPlaylist` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
addTrackToPlaylist(vars: AddTrackToPlaylistVariables): MutationPromise<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;

interface AddTrackToPlaylistRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: AddTrackToPlaylistVariables): MutationRef<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;
}
export const addTrackToPlaylistRef: AddTrackToPlaylistRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
addTrackToPlaylist(dc: DataConnect, vars: AddTrackToPlaylistVariables): MutationPromise<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;

interface AddTrackToPlaylistRef {
  ...
  (dc: DataConnect, vars: AddTrackToPlaylistVariables): MutationRef<AddTrackToPlaylistData, AddTrackToPlaylistVariables>;
}
export const addTrackToPlaylistRef: AddTrackToPlaylistRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the addTrackToPlaylistRef:
```typescript
const name = addTrackToPlaylistRef.operationName;
console.log(name);
```

### Variables
The `AddTrackToPlaylist` mutation requires an argument of type `AddTrackToPlaylistVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface AddTrackToPlaylistVariables {
  playlistId: string;
  trackId: string;
}
```
### Return Type
Recall that executing the `AddTrackToPlaylist` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `AddTrackToPlaylistData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface AddTrackToPlaylistData {
  playlistEntry_insert: PlaylistEntry_Key;
}
```
### Using `AddTrackToPlaylist`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, addTrackToPlaylist, AddTrackToPlaylistVariables } from '@musically/dataconnect';

// The `AddTrackToPlaylist` mutation requires an argument of type `AddTrackToPlaylistVariables`:
const addTrackToPlaylistVars: AddTrackToPlaylistVariables = {
  playlistId: ..., 
  trackId: ..., 
};

// Call the `addTrackToPlaylist()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await addTrackToPlaylist(addTrackToPlaylistVars);
// Variables can be defined inline as well.
const { data } = await addTrackToPlaylist({ playlistId: ..., trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await addTrackToPlaylist(dataConnect, addTrackToPlaylistVars);

console.log(data.playlistEntry_insert);

// Or, you can use the `Promise` API.
addTrackToPlaylist(addTrackToPlaylistVars).then((response) => {
  const data = response.data;
  console.log(data.playlistEntry_insert);
});
```

### Using `AddTrackToPlaylist`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, addTrackToPlaylistRef, AddTrackToPlaylistVariables } from '@musically/dataconnect';

// The `AddTrackToPlaylist` mutation requires an argument of type `AddTrackToPlaylistVariables`:
const addTrackToPlaylistVars: AddTrackToPlaylistVariables = {
  playlistId: ..., 
  trackId: ..., 
};

// Call the `addTrackToPlaylistRef()` function to get a reference to the mutation.
const ref = addTrackToPlaylistRef(addTrackToPlaylistVars);
// Variables can be defined inline as well.
const ref = addTrackToPlaylistRef({ playlistId: ..., trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = addTrackToPlaylistRef(dataConnect, addTrackToPlaylistVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.playlistEntry_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.playlistEntry_insert);
});
```

## CreatePodcast
You can execute the `CreatePodcast` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createPodcast(vars: CreatePodcastVariables): MutationPromise<CreatePodcastData, CreatePodcastVariables>;

interface CreatePodcastRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreatePodcastVariables): MutationRef<CreatePodcastData, CreatePodcastVariables>;
}
export const createPodcastRef: CreatePodcastRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createPodcast(dc: DataConnect, vars: CreatePodcastVariables): MutationPromise<CreatePodcastData, CreatePodcastVariables>;

interface CreatePodcastRef {
  ...
  (dc: DataConnect, vars: CreatePodcastVariables): MutationRef<CreatePodcastData, CreatePodcastVariables>;
}
export const createPodcastRef: CreatePodcastRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createPodcastRef:
```typescript
const name = createPodcastRef.operationName;
console.log(name);
```

### Variables
The `CreatePodcast` mutation requires an argument of type `CreatePodcastVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreatePodcastVariables {
  title: string;
  publisher: string;
  coverUrl?: string | null;
  description?: string | null;
  storyContext?: string | null;
}
```
### Return Type
Recall that executing the `CreatePodcast` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreatePodcastData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreatePodcastData {
  show_insert: Show_Key;
}
```
### Using `CreatePodcast`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createPodcast, CreatePodcastVariables } from '@musically/dataconnect';

// The `CreatePodcast` mutation requires an argument of type `CreatePodcastVariables`:
const createPodcastVars: CreatePodcastVariables = {
  title: ..., 
  publisher: ..., 
  coverUrl: ..., // optional
  description: ..., // optional
  storyContext: ..., // optional
};

// Call the `createPodcast()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createPodcast(createPodcastVars);
// Variables can be defined inline as well.
const { data } = await createPodcast({ title: ..., publisher: ..., coverUrl: ..., description: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createPodcast(dataConnect, createPodcastVars);

console.log(data.show_insert);

// Or, you can use the `Promise` API.
createPodcast(createPodcastVars).then((response) => {
  const data = response.data;
  console.log(data.show_insert);
});
```

### Using `CreatePodcast`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createPodcastRef, CreatePodcastVariables } from '@musically/dataconnect';

// The `CreatePodcast` mutation requires an argument of type `CreatePodcastVariables`:
const createPodcastVars: CreatePodcastVariables = {
  title: ..., 
  publisher: ..., 
  coverUrl: ..., // optional
  description: ..., // optional
  storyContext: ..., // optional
};

// Call the `createPodcastRef()` function to get a reference to the mutation.
const ref = createPodcastRef(createPodcastVars);
// Variables can be defined inline as well.
const ref = createPodcastRef({ title: ..., publisher: ..., coverUrl: ..., description: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createPodcastRef(dataConnect, createPodcastVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.show_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.show_insert);
});
```

## UpsertUserSettings
You can execute the `UpsertUserSettings` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
upsertUserSettings(vars?: UpsertUserSettingsVariables): MutationPromise<UpsertUserSettingsData, UpsertUserSettingsVariables>;

interface UpsertUserSettingsRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars?: UpsertUserSettingsVariables): MutationRef<UpsertUserSettingsData, UpsertUserSettingsVariables>;
}
export const upsertUserSettingsRef: UpsertUserSettingsRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
upsertUserSettings(dc: DataConnect, vars?: UpsertUserSettingsVariables): MutationPromise<UpsertUserSettingsData, UpsertUserSettingsVariables>;

interface UpsertUserSettingsRef {
  ...
  (dc: DataConnect, vars?: UpsertUserSettingsVariables): MutationRef<UpsertUserSettingsData, UpsertUserSettingsVariables>;
}
export const upsertUserSettingsRef: UpsertUserSettingsRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the upsertUserSettingsRef:
```typescript
const name = upsertUserSettingsRef.operationName;
console.log(name);
```

### Variables
The `UpsertUserSettings` mutation has an optional argument of type `UpsertUserSettingsVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpsertUserSettingsVariables {
  theme?: string | null;
  parentalControlsEnabled?: boolean | null;
  notificationsEnabled?: boolean | null;
  appsDevicesEnabled?: boolean | null;
  offlineMode?: boolean | null;
  stripeCustomerId?: string | null;
}
```
### Return Type
Recall that executing the `UpsertUserSettings` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpsertUserSettingsData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpsertUserSettingsData {
  userSettings_upsert: UserSettings_Key;
}
```
### Using `UpsertUserSettings`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, upsertUserSettings, UpsertUserSettingsVariables } from '@musically/dataconnect';

// The `UpsertUserSettings` mutation has an optional argument of type `UpsertUserSettingsVariables`:
const upsertUserSettingsVars: UpsertUserSettingsVariables = {
  theme: ..., // optional
  parentalControlsEnabled: ..., // optional
  notificationsEnabled: ..., // optional
  appsDevicesEnabled: ..., // optional
  offlineMode: ..., // optional
  stripeCustomerId: ..., // optional
};

// Call the `upsertUserSettings()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await upsertUserSettings(upsertUserSettingsVars);
// Variables can be defined inline as well.
const { data } = await upsertUserSettings({ theme: ..., parentalControlsEnabled: ..., notificationsEnabled: ..., appsDevicesEnabled: ..., offlineMode: ..., stripeCustomerId: ..., });
// Since all variables are optional for this mutation, you can omit the `UpsertUserSettingsVariables` argument.
const { data } = await upsertUserSettings();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await upsertUserSettings(dataConnect, upsertUserSettingsVars);

console.log(data.userSettings_upsert);

// Or, you can use the `Promise` API.
upsertUserSettings(upsertUserSettingsVars).then((response) => {
  const data = response.data;
  console.log(data.userSettings_upsert);
});
```

### Using `UpsertUserSettings`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, upsertUserSettingsRef, UpsertUserSettingsVariables } from '@musically/dataconnect';

// The `UpsertUserSettings` mutation has an optional argument of type `UpsertUserSettingsVariables`:
const upsertUserSettingsVars: UpsertUserSettingsVariables = {
  theme: ..., // optional
  parentalControlsEnabled: ..., // optional
  notificationsEnabled: ..., // optional
  appsDevicesEnabled: ..., // optional
  offlineMode: ..., // optional
  stripeCustomerId: ..., // optional
};

// Call the `upsertUserSettingsRef()` function to get a reference to the mutation.
const ref = upsertUserSettingsRef(upsertUserSettingsVars);
// Variables can be defined inline as well.
const ref = upsertUserSettingsRef({ theme: ..., parentalControlsEnabled: ..., notificationsEnabled: ..., appsDevicesEnabled: ..., offlineMode: ..., stripeCustomerId: ..., });
// Since all variables are optional for this mutation, you can omit the `UpsertUserSettingsVariables` argument.
const ref = upsertUserSettingsRef();

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = upsertUserSettingsRef(dataConnect, upsertUserSettingsVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.userSettings_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.userSettings_upsert);
});
```

## UpdateUserPreferences
You can execute the `UpdateUserPreferences` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
updateUserPreferences(vars?: UpdateUserPreferencesVariables): MutationPromise<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;

interface UpdateUserPreferencesRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars?: UpdateUserPreferencesVariables): MutationRef<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;
}
export const updateUserPreferencesRef: UpdateUserPreferencesRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
updateUserPreferences(dc: DataConnect, vars?: UpdateUserPreferencesVariables): MutationPromise<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;

interface UpdateUserPreferencesRef {
  ...
  (dc: DataConnect, vars?: UpdateUserPreferencesVariables): MutationRef<UpdateUserPreferencesData, UpdateUserPreferencesVariables>;
}
export const updateUserPreferencesRef: UpdateUserPreferencesRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the updateUserPreferencesRef:
```typescript
const name = updateUserPreferencesRef.operationName;
console.log(name);
```

### Variables
The `UpdateUserPreferences` mutation has an optional argument of type `UpdateUserPreferencesVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpdateUserPreferencesVariables {
  favoriteArtists?: string[] | null;
  favoriteGenres?: string[] | null;
}
```
### Return Type
Recall that executing the `UpdateUserPreferences` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpdateUserPreferencesData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpdateUserPreferencesData {
  user_update?: User_Key | null;
}
```
### Using `UpdateUserPreferences`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, updateUserPreferences, UpdateUserPreferencesVariables } from '@musically/dataconnect';

// The `UpdateUserPreferences` mutation has an optional argument of type `UpdateUserPreferencesVariables`:
const updateUserPreferencesVars: UpdateUserPreferencesVariables = {
  favoriteArtists: ..., // optional
  favoriteGenres: ..., // optional
};

// Call the `updateUserPreferences()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await updateUserPreferences(updateUserPreferencesVars);
// Variables can be defined inline as well.
const { data } = await updateUserPreferences({ favoriteArtists: ..., favoriteGenres: ..., });
// Since all variables are optional for this mutation, you can omit the `UpdateUserPreferencesVariables` argument.
const { data } = await updateUserPreferences();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await updateUserPreferences(dataConnect, updateUserPreferencesVars);

console.log(data.user_update);

// Or, you can use the `Promise` API.
updateUserPreferences(updateUserPreferencesVars).then((response) => {
  const data = response.data;
  console.log(data.user_update);
});
```

### Using `UpdateUserPreferences`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, updateUserPreferencesRef, UpdateUserPreferencesVariables } from '@musically/dataconnect';

// The `UpdateUserPreferences` mutation has an optional argument of type `UpdateUserPreferencesVariables`:
const updateUserPreferencesVars: UpdateUserPreferencesVariables = {
  favoriteArtists: ..., // optional
  favoriteGenres: ..., // optional
};

// Call the `updateUserPreferencesRef()` function to get a reference to the mutation.
const ref = updateUserPreferencesRef(updateUserPreferencesVars);
// Variables can be defined inline as well.
const ref = updateUserPreferencesRef({ favoriteArtists: ..., favoriteGenres: ..., });
// Since all variables are optional for this mutation, you can omit the `UpdateUserPreferencesVariables` argument.
const ref = updateUserPreferencesRef();

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = updateUserPreferencesRef(dataConnect, updateUserPreferencesVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.user_update);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.user_update);
});
```

## RecordPayment
You can execute the `RecordPayment` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
recordPayment(vars: RecordPaymentVariables): MutationPromise<RecordPaymentData, RecordPaymentVariables>;

interface RecordPaymentRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: RecordPaymentVariables): MutationRef<RecordPaymentData, RecordPaymentVariables>;
}
export const recordPaymentRef: RecordPaymentRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
recordPayment(dc: DataConnect, vars: RecordPaymentVariables): MutationPromise<RecordPaymentData, RecordPaymentVariables>;

interface RecordPaymentRef {
  ...
  (dc: DataConnect, vars: RecordPaymentVariables): MutationRef<RecordPaymentData, RecordPaymentVariables>;
}
export const recordPaymentRef: RecordPaymentRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the recordPaymentRef:
```typescript
const name = recordPaymentRef.operationName;
console.log(name);
```

### Variables
The `RecordPayment` mutation requires an argument of type `RecordPaymentVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface RecordPaymentVariables {
  userUid: string;
  amount: number;
  currency: string;
  status: string;
  stripeInvoiceId?: string | null;
}
```
### Return Type
Recall that executing the `RecordPayment` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `RecordPaymentData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface RecordPaymentData {
  paymentHistory_insert: PaymentHistory_Key;
}
```
### Using `RecordPayment`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, recordPayment, RecordPaymentVariables } from '@musically/dataconnect';

// The `RecordPayment` mutation requires an argument of type `RecordPaymentVariables`:
const recordPaymentVars: RecordPaymentVariables = {
  userUid: ..., 
  amount: ..., 
  currency: ..., 
  status: ..., 
  stripeInvoiceId: ..., // optional
};

// Call the `recordPayment()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await recordPayment(recordPaymentVars);
// Variables can be defined inline as well.
const { data } = await recordPayment({ userUid: ..., amount: ..., currency: ..., status: ..., stripeInvoiceId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await recordPayment(dataConnect, recordPaymentVars);

console.log(data.paymentHistory_insert);

// Or, you can use the `Promise` API.
recordPayment(recordPaymentVars).then((response) => {
  const data = response.data;
  console.log(data.paymentHistory_insert);
});
```

### Using `RecordPayment`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, recordPaymentRef, RecordPaymentVariables } from '@musically/dataconnect';

// The `RecordPayment` mutation requires an argument of type `RecordPaymentVariables`:
const recordPaymentVars: RecordPaymentVariables = {
  userUid: ..., 
  amount: ..., 
  currency: ..., 
  status: ..., 
  stripeInvoiceId: ..., // optional
};

// Call the `recordPaymentRef()` function to get a reference to the mutation.
const ref = recordPaymentRef(recordPaymentVars);
// Variables can be defined inline as well.
const ref = recordPaymentRef({ userUid: ..., amount: ..., currency: ..., status: ..., stripeInvoiceId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = recordPaymentRef(dataConnect, recordPaymentVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.paymentHistory_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.paymentHistory_insert);
});
```

## LikeTrack
You can execute the `LikeTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
likeTrack(vars: LikeTrackVariables): MutationPromise<LikeTrackData, LikeTrackVariables>;

interface LikeTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: LikeTrackVariables): MutationRef<LikeTrackData, LikeTrackVariables>;
}
export const likeTrackRef: LikeTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
likeTrack(dc: DataConnect, vars: LikeTrackVariables): MutationPromise<LikeTrackData, LikeTrackVariables>;

interface LikeTrackRef {
  ...
  (dc: DataConnect, vars: LikeTrackVariables): MutationRef<LikeTrackData, LikeTrackVariables>;
}
export const likeTrackRef: LikeTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the likeTrackRef:
```typescript
const name = likeTrackRef.operationName;
console.log(name);
```

### Variables
The `LikeTrack` mutation requires an argument of type `LikeTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface LikeTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `LikeTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `LikeTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface LikeTrackData {
  likedTrack_upsert: LikedTrack_Key;
}
```
### Using `LikeTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, likeTrack, LikeTrackVariables } from '@musically/dataconnect';

// The `LikeTrack` mutation requires an argument of type `LikeTrackVariables`:
const likeTrackVars: LikeTrackVariables = {
  trackId: ..., 
};

// Call the `likeTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await likeTrack(likeTrackVars);
// Variables can be defined inline as well.
const { data } = await likeTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await likeTrack(dataConnect, likeTrackVars);

console.log(data.likedTrack_upsert);

// Or, you can use the `Promise` API.
likeTrack(likeTrackVars).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_upsert);
});
```

### Using `LikeTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, likeTrackRef, LikeTrackVariables } from '@musically/dataconnect';

// The `LikeTrack` mutation requires an argument of type `LikeTrackVariables`:
const likeTrackVars: LikeTrackVariables = {
  trackId: ..., 
};

// Call the `likeTrackRef()` function to get a reference to the mutation.
const ref = likeTrackRef(likeTrackVars);
// Variables can be defined inline as well.
const ref = likeTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = likeTrackRef(dataConnect, likeTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.likedTrack_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_upsert);
});
```

## RemoveLikedTrack
You can execute the `RemoveLikedTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
removeLikedTrack(vars: RemoveLikedTrackVariables): MutationPromise<RemoveLikedTrackData, RemoveLikedTrackVariables>;

interface RemoveLikedTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: RemoveLikedTrackVariables): MutationRef<RemoveLikedTrackData, RemoveLikedTrackVariables>;
}
export const removeLikedTrackRef: RemoveLikedTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
removeLikedTrack(dc: DataConnect, vars: RemoveLikedTrackVariables): MutationPromise<RemoveLikedTrackData, RemoveLikedTrackVariables>;

interface RemoveLikedTrackRef {
  ...
  (dc: DataConnect, vars: RemoveLikedTrackVariables): MutationRef<RemoveLikedTrackData, RemoveLikedTrackVariables>;
}
export const removeLikedTrackRef: RemoveLikedTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the removeLikedTrackRef:
```typescript
const name = removeLikedTrackRef.operationName;
console.log(name);
```

### Variables
The `RemoveLikedTrack` mutation requires an argument of type `RemoveLikedTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface RemoveLikedTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `RemoveLikedTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `RemoveLikedTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface RemoveLikedTrackData {
  likedTrack_delete?: LikedTrack_Key | null;
}
```
### Using `RemoveLikedTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, removeLikedTrack, RemoveLikedTrackVariables } from '@musically/dataconnect';

// The `RemoveLikedTrack` mutation requires an argument of type `RemoveLikedTrackVariables`:
const removeLikedTrackVars: RemoveLikedTrackVariables = {
  trackId: ..., 
};

// Call the `removeLikedTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await removeLikedTrack(removeLikedTrackVars);
// Variables can be defined inline as well.
const { data } = await removeLikedTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await removeLikedTrack(dataConnect, removeLikedTrackVars);

console.log(data.likedTrack_delete);

// Or, you can use the `Promise` API.
removeLikedTrack(removeLikedTrackVars).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_delete);
});
```

### Using `RemoveLikedTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, removeLikedTrackRef, RemoveLikedTrackVariables } from '@musically/dataconnect';

// The `RemoveLikedTrack` mutation requires an argument of type `RemoveLikedTrackVariables`:
const removeLikedTrackVars: RemoveLikedTrackVariables = {
  trackId: ..., 
};

// Call the `removeLikedTrackRef()` function to get a reference to the mutation.
const ref = removeLikedTrackRef(removeLikedTrackVars);
// Variables can be defined inline as well.
const ref = removeLikedTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = removeLikedTrackRef(dataConnect, removeLikedTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.likedTrack_delete);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_delete);
});
```

## BookmarkTrack
You can execute the `BookmarkTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
bookmarkTrack(vars: BookmarkTrackVariables): MutationPromise<BookmarkTrackData, BookmarkTrackVariables>;

interface BookmarkTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: BookmarkTrackVariables): MutationRef<BookmarkTrackData, BookmarkTrackVariables>;
}
export const bookmarkTrackRef: BookmarkTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
bookmarkTrack(dc: DataConnect, vars: BookmarkTrackVariables): MutationPromise<BookmarkTrackData, BookmarkTrackVariables>;

interface BookmarkTrackRef {
  ...
  (dc: DataConnect, vars: BookmarkTrackVariables): MutationRef<BookmarkTrackData, BookmarkTrackVariables>;
}
export const bookmarkTrackRef: BookmarkTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the bookmarkTrackRef:
```typescript
const name = bookmarkTrackRef.operationName;
console.log(name);
```

### Variables
The `BookmarkTrack` mutation requires an argument of type `BookmarkTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface BookmarkTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `BookmarkTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `BookmarkTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface BookmarkTrackData {
  bookmarkedTrack_insert: BookmarkedTrack_Key;
}
```
### Using `BookmarkTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, bookmarkTrack, BookmarkTrackVariables } from '@musically/dataconnect';

// The `BookmarkTrack` mutation requires an argument of type `BookmarkTrackVariables`:
const bookmarkTrackVars: BookmarkTrackVariables = {
  trackId: ..., 
};

// Call the `bookmarkTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await bookmarkTrack(bookmarkTrackVars);
// Variables can be defined inline as well.
const { data } = await bookmarkTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await bookmarkTrack(dataConnect, bookmarkTrackVars);

console.log(data.bookmarkedTrack_insert);

// Or, you can use the `Promise` API.
bookmarkTrack(bookmarkTrackVars).then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTrack_insert);
});
```

### Using `BookmarkTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, bookmarkTrackRef, BookmarkTrackVariables } from '@musically/dataconnect';

// The `BookmarkTrack` mutation requires an argument of type `BookmarkTrackVariables`:
const bookmarkTrackVars: BookmarkTrackVariables = {
  trackId: ..., 
};

// Call the `bookmarkTrackRef()` function to get a reference to the mutation.
const ref = bookmarkTrackRef(bookmarkTrackVars);
// Variables can be defined inline as well.
const ref = bookmarkTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = bookmarkTrackRef(dataConnect, bookmarkTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.bookmarkedTrack_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTrack_insert);
});
```

## RecordPlay
You can execute the `RecordPlay` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
recordPlay(vars: RecordPlayVariables): MutationPromise<RecordPlayData, RecordPlayVariables>;

interface RecordPlayRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: RecordPlayVariables): MutationRef<RecordPlayData, RecordPlayVariables>;
}
export const recordPlayRef: RecordPlayRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
recordPlay(dc: DataConnect, vars: RecordPlayVariables): MutationPromise<RecordPlayData, RecordPlayVariables>;

interface RecordPlayRef {
  ...
  (dc: DataConnect, vars: RecordPlayVariables): MutationRef<RecordPlayData, RecordPlayVariables>;
}
export const recordPlayRef: RecordPlayRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the recordPlayRef:
```typescript
const name = recordPlayRef.operationName;
console.log(name);
```

### Variables
The `RecordPlay` mutation requires an argument of type `RecordPlayVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface RecordPlayVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `RecordPlay` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `RecordPlayData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface RecordPlayData {
  playHistory_insert: PlayHistory_Key;
}
```
### Using `RecordPlay`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, recordPlay, RecordPlayVariables } from '@musically/dataconnect';

// The `RecordPlay` mutation requires an argument of type `RecordPlayVariables`:
const recordPlayVars: RecordPlayVariables = {
  trackId: ..., 
};

// Call the `recordPlay()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await recordPlay(recordPlayVars);
// Variables can be defined inline as well.
const { data } = await recordPlay({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await recordPlay(dataConnect, recordPlayVars);

console.log(data.playHistory_insert);

// Or, you can use the `Promise` API.
recordPlay(recordPlayVars).then((response) => {
  const data = response.data;
  console.log(data.playHistory_insert);
});
```

### Using `RecordPlay`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, recordPlayRef, RecordPlayVariables } from '@musically/dataconnect';

// The `RecordPlay` mutation requires an argument of type `RecordPlayVariables`:
const recordPlayVars: RecordPlayVariables = {
  trackId: ..., 
};

// Call the `recordPlayRef()` function to get a reference to the mutation.
const ref = recordPlayRef(recordPlayVars);
// Variables can be defined inline as well.
const ref = recordPlayRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = recordPlayRef(dataConnect, recordPlayVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.playHistory_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.playHistory_insert);
});
```

## RemoveBookmarkedTrack
You can execute the `RemoveBookmarkedTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
removeBookmarkedTrack(vars: RemoveBookmarkedTrackVariables): MutationPromise<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;

interface RemoveBookmarkedTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: RemoveBookmarkedTrackVariables): MutationRef<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;
}
export const removeBookmarkedTrackRef: RemoveBookmarkedTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
removeBookmarkedTrack(dc: DataConnect, vars: RemoveBookmarkedTrackVariables): MutationPromise<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;

interface RemoveBookmarkedTrackRef {
  ...
  (dc: DataConnect, vars: RemoveBookmarkedTrackVariables): MutationRef<RemoveBookmarkedTrackData, RemoveBookmarkedTrackVariables>;
}
export const removeBookmarkedTrackRef: RemoveBookmarkedTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the removeBookmarkedTrackRef:
```typescript
const name = removeBookmarkedTrackRef.operationName;
console.log(name);
```

### Variables
The `RemoveBookmarkedTrack` mutation requires an argument of type `RemoveBookmarkedTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface RemoveBookmarkedTrackVariables {
  trackId: string;
}
```
### Return Type
Recall that executing the `RemoveBookmarkedTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `RemoveBookmarkedTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface RemoveBookmarkedTrackData {
  bookmarkedTrack_delete?: BookmarkedTrack_Key | null;
}
```
### Using `RemoveBookmarkedTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, removeBookmarkedTrack, RemoveBookmarkedTrackVariables } from '@musically/dataconnect';

// The `RemoveBookmarkedTrack` mutation requires an argument of type `RemoveBookmarkedTrackVariables`:
const removeBookmarkedTrackVars: RemoveBookmarkedTrackVariables = {
  trackId: ..., 
};

// Call the `removeBookmarkedTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await removeBookmarkedTrack(removeBookmarkedTrackVars);
// Variables can be defined inline as well.
const { data } = await removeBookmarkedTrack({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await removeBookmarkedTrack(dataConnect, removeBookmarkedTrackVars);

console.log(data.bookmarkedTrack_delete);

// Or, you can use the `Promise` API.
removeBookmarkedTrack(removeBookmarkedTrackVars).then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTrack_delete);
});
```

### Using `RemoveBookmarkedTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, removeBookmarkedTrackRef, RemoveBookmarkedTrackVariables } from '@musically/dataconnect';

// The `RemoveBookmarkedTrack` mutation requires an argument of type `RemoveBookmarkedTrackVariables`:
const removeBookmarkedTrackVars: RemoveBookmarkedTrackVariables = {
  trackId: ..., 
};

// Call the `removeBookmarkedTrackRef()` function to get a reference to the mutation.
const ref = removeBookmarkedTrackRef(removeBookmarkedTrackVars);
// Variables can be defined inline as well.
const ref = removeBookmarkedTrackRef({ trackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = removeBookmarkedTrackRef(dataConnect, removeBookmarkedTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.bookmarkedTrack_delete);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.bookmarkedTrack_delete);
});
```

## CreateCameraCapture
You can execute the `CreateCameraCapture` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createCameraCapture(vars?: CreateCameraCaptureVariables): MutationPromise<CreateCameraCaptureData, CreateCameraCaptureVariables>;

interface CreateCameraCaptureRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars?: CreateCameraCaptureVariables): MutationRef<CreateCameraCaptureData, CreateCameraCaptureVariables>;
}
export const createCameraCaptureRef: CreateCameraCaptureRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createCameraCapture(dc: DataConnect, vars?: CreateCameraCaptureVariables): MutationPromise<CreateCameraCaptureData, CreateCameraCaptureVariables>;

interface CreateCameraCaptureRef {
  ...
  (dc: DataConnect, vars?: CreateCameraCaptureVariables): MutationRef<CreateCameraCaptureData, CreateCameraCaptureVariables>;
}
export const createCameraCaptureRef: CreateCameraCaptureRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createCameraCaptureRef:
```typescript
const name = createCameraCaptureRef.operationName;
console.log(name);
```

### Variables
The `CreateCameraCapture` mutation has an optional argument of type `CreateCameraCaptureVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateCameraCaptureVariables {
  imageUrl?: string | null;
  videoUrl?: string | null;
  environmentData?: string | null;
  generatedTrackId?: string | null;
}
```
### Return Type
Recall that executing the `CreateCameraCapture` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateCameraCaptureData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateCameraCaptureData {
  cameraCapture_insert: CameraCapture_Key;
}
```
### Using `CreateCameraCapture`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createCameraCapture, CreateCameraCaptureVariables } from '@musically/dataconnect';

// The `CreateCameraCapture` mutation has an optional argument of type `CreateCameraCaptureVariables`:
const createCameraCaptureVars: CreateCameraCaptureVariables = {
  imageUrl: ..., // optional
  videoUrl: ..., // optional
  environmentData: ..., // optional
  generatedTrackId: ..., // optional
};

// Call the `createCameraCapture()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createCameraCapture(createCameraCaptureVars);
// Variables can be defined inline as well.
const { data } = await createCameraCapture({ imageUrl: ..., videoUrl: ..., environmentData: ..., generatedTrackId: ..., });
// Since all variables are optional for this mutation, you can omit the `CreateCameraCaptureVariables` argument.
const { data } = await createCameraCapture();

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createCameraCapture(dataConnect, createCameraCaptureVars);

console.log(data.cameraCapture_insert);

// Or, you can use the `Promise` API.
createCameraCapture(createCameraCaptureVars).then((response) => {
  const data = response.data;
  console.log(data.cameraCapture_insert);
});
```

### Using `CreateCameraCapture`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createCameraCaptureRef, CreateCameraCaptureVariables } from '@musically/dataconnect';

// The `CreateCameraCapture` mutation has an optional argument of type `CreateCameraCaptureVariables`:
const createCameraCaptureVars: CreateCameraCaptureVariables = {
  imageUrl: ..., // optional
  videoUrl: ..., // optional
  environmentData: ..., // optional
  generatedTrackId: ..., // optional
};

// Call the `createCameraCaptureRef()` function to get a reference to the mutation.
const ref = createCameraCaptureRef(createCameraCaptureVars);
// Variables can be defined inline as well.
const ref = createCameraCaptureRef({ imageUrl: ..., videoUrl: ..., environmentData: ..., generatedTrackId: ..., });
// Since all variables are optional for this mutation, you can omit the `CreateCameraCaptureVariables` argument.
const ref = createCameraCaptureRef();

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createCameraCaptureRef(dataConnect, createCameraCaptureVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.cameraCapture_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.cameraCapture_insert);
});
```

## CreateVideoDigestion
You can execute the `CreateVideoDigestion` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createVideoDigestion(vars: CreateVideoDigestionVariables): MutationPromise<CreateVideoDigestionData, CreateVideoDigestionVariables>;

interface CreateVideoDigestionRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateVideoDigestionVariables): MutationRef<CreateVideoDigestionData, CreateVideoDigestionVariables>;
}
export const createVideoDigestionRef: CreateVideoDigestionRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createVideoDigestion(dc: DataConnect, vars: CreateVideoDigestionVariables): MutationPromise<CreateVideoDigestionData, CreateVideoDigestionVariables>;

interface CreateVideoDigestionRef {
  ...
  (dc: DataConnect, vars: CreateVideoDigestionVariables): MutationRef<CreateVideoDigestionData, CreateVideoDigestionVariables>;
}
export const createVideoDigestionRef: CreateVideoDigestionRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createVideoDigestionRef:
```typescript
const name = createVideoDigestionRef.operationName;
console.log(name);
```

### Variables
The `CreateVideoDigestion` mutation requires an argument of type `CreateVideoDigestionVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateVideoDigestionVariables {
  videoUrl: string;
  extractedAudioUrl?: string | null;
  atmosphereSummary?: string | null;
  generatedTrackId?: string | null;
}
```
### Return Type
Recall that executing the `CreateVideoDigestion` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateVideoDigestionData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateVideoDigestionData {
  videoDigestion_insert: VideoDigestion_Key;
}
```
### Using `CreateVideoDigestion`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createVideoDigestion, CreateVideoDigestionVariables } from '@musically/dataconnect';

// The `CreateVideoDigestion` mutation requires an argument of type `CreateVideoDigestionVariables`:
const createVideoDigestionVars: CreateVideoDigestionVariables = {
  videoUrl: ..., 
  extractedAudioUrl: ..., // optional
  atmosphereSummary: ..., // optional
  generatedTrackId: ..., // optional
};

// Call the `createVideoDigestion()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createVideoDigestion(createVideoDigestionVars);
// Variables can be defined inline as well.
const { data } = await createVideoDigestion({ videoUrl: ..., extractedAudioUrl: ..., atmosphereSummary: ..., generatedTrackId: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createVideoDigestion(dataConnect, createVideoDigestionVars);

console.log(data.videoDigestion_insert);

// Or, you can use the `Promise` API.
createVideoDigestion(createVideoDigestionVars).then((response) => {
  const data = response.data;
  console.log(data.videoDigestion_insert);
});
```

### Using `CreateVideoDigestion`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createVideoDigestionRef, CreateVideoDigestionVariables } from '@musically/dataconnect';

// The `CreateVideoDigestion` mutation requires an argument of type `CreateVideoDigestionVariables`:
const createVideoDigestionVars: CreateVideoDigestionVariables = {
  videoUrl: ..., 
  extractedAudioUrl: ..., // optional
  atmosphereSummary: ..., // optional
  generatedTrackId: ..., // optional
};

// Call the `createVideoDigestionRef()` function to get a reference to the mutation.
const ref = createVideoDigestionRef(createVideoDigestionVars);
// Variables can be defined inline as well.
const ref = createVideoDigestionRef({ videoUrl: ..., extractedAudioUrl: ..., atmosphereSummary: ..., generatedTrackId: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createVideoDigestionRef(dataConnect, createVideoDigestionVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.videoDigestion_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.videoDigestion_insert);
});
```

## AddChatMessage
You can execute the `AddChatMessage` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
addChatMessage(vars: AddChatMessageVariables): MutationPromise<AddChatMessageData, AddChatMessageVariables>;

interface AddChatMessageRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: AddChatMessageVariables): MutationRef<AddChatMessageData, AddChatMessageVariables>;
}
export const addChatMessageRef: AddChatMessageRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
addChatMessage(dc: DataConnect, vars: AddChatMessageVariables): MutationPromise<AddChatMessageData, AddChatMessageVariables>;

interface AddChatMessageRef {
  ...
  (dc: DataConnect, vars: AddChatMessageVariables): MutationRef<AddChatMessageData, AddChatMessageVariables>;
}
export const addChatMessageRef: AddChatMessageRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the addChatMessageRef:
```typescript
const name = addChatMessageRef.operationName;
console.log(name);
```

### Variables
The `AddChatMessage` mutation requires an argument of type `AddChatMessageVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface AddChatMessageVariables {
  userId: string;
  sender: string;
  text: string;
}
```
### Return Type
Recall that executing the `AddChatMessage` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `AddChatMessageData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface AddChatMessageData {
  chatMessage_insert: ChatMessage_Key;
}
```
### Using `AddChatMessage`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, addChatMessage, AddChatMessageVariables } from '@musically/dataconnect';

// The `AddChatMessage` mutation requires an argument of type `AddChatMessageVariables`:
const addChatMessageVars: AddChatMessageVariables = {
  userId: ..., 
  sender: ..., 
  text: ..., 
};

// Call the `addChatMessage()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await addChatMessage(addChatMessageVars);
// Variables can be defined inline as well.
const { data } = await addChatMessage({ userId: ..., sender: ..., text: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await addChatMessage(dataConnect, addChatMessageVars);

console.log(data.chatMessage_insert);

// Or, you can use the `Promise` API.
addChatMessage(addChatMessageVars).then((response) => {
  const data = response.data;
  console.log(data.chatMessage_insert);
});
```

### Using `AddChatMessage`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, addChatMessageRef, AddChatMessageVariables } from '@musically/dataconnect';

// The `AddChatMessage` mutation requires an argument of type `AddChatMessageVariables`:
const addChatMessageVars: AddChatMessageVariables = {
  userId: ..., 
  sender: ..., 
  text: ..., 
};

// Call the `addChatMessageRef()` function to get a reference to the mutation.
const ref = addChatMessageRef(addChatMessageVars);
// Variables can be defined inline as well.
const ref = addChatMessageRef({ userId: ..., sender: ..., text: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = addChatMessageRef(dataConnect, addChatMessageVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.chatMessage_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.chatMessage_insert);
});
```

## SeedTrack
You can execute the `SeedTrack` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedTrack(vars: SeedTrackVariables): MutationPromise<SeedTrackData, SeedTrackVariables>;

interface SeedTrackRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedTrackVariables): MutationRef<SeedTrackData, SeedTrackVariables>;
}
export const seedTrackRef: SeedTrackRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedTrack(dc: DataConnect, vars: SeedTrackVariables): MutationPromise<SeedTrackData, SeedTrackVariables>;

interface SeedTrackRef {
  ...
  (dc: DataConnect, vars: SeedTrackVariables): MutationRef<SeedTrackData, SeedTrackVariables>;
}
export const seedTrackRef: SeedTrackRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedTrackRef:
```typescript
const name = seedTrackRef.operationName;
console.log(name);
```

### Variables
The `SeedTrack` mutation requires an argument of type `SeedTrackVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedTrackVariables {
  title: string;
  audioUrl: string;
  coverUrl?: string | null;
  durationMs?: number | null;
  prompt?: string | null;
  isCommunity: boolean;
  ownerUid: string;
}
```
### Return Type
Recall that executing the `SeedTrack` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedTrackData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedTrackData {
  track_insert: Track_Key;
}
```
### Using `SeedTrack`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedTrack, SeedTrackVariables } from '@musically/dataconnect';

// The `SeedTrack` mutation requires an argument of type `SeedTrackVariables`:
const seedTrackVars: SeedTrackVariables = {
  title: ..., 
  audioUrl: ..., 
  coverUrl: ..., // optional
  durationMs: ..., // optional
  prompt: ..., // optional
  isCommunity: ..., 
  ownerUid: ..., 
};

// Call the `seedTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedTrack(seedTrackVars);
// Variables can be defined inline as well.
const { data } = await seedTrack({ title: ..., audioUrl: ..., coverUrl: ..., durationMs: ..., prompt: ..., isCommunity: ..., ownerUid: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedTrack(dataConnect, seedTrackVars);

console.log(data.track_insert);

// Or, you can use the `Promise` API.
seedTrack(seedTrackVars).then((response) => {
  const data = response.data;
  console.log(data.track_insert);
});
```

### Using `SeedTrack`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedTrackRef, SeedTrackVariables } from '@musically/dataconnect';

// The `SeedTrack` mutation requires an argument of type `SeedTrackVariables`:
const seedTrackVars: SeedTrackVariables = {
  title: ..., 
  audioUrl: ..., 
  coverUrl: ..., // optional
  durationMs: ..., // optional
  prompt: ..., // optional
  isCommunity: ..., 
  ownerUid: ..., 
};

// Call the `seedTrackRef()` function to get a reference to the mutation.
const ref = seedTrackRef(seedTrackVars);
// Variables can be defined inline as well.
const ref = seedTrackRef({ title: ..., audioUrl: ..., coverUrl: ..., durationMs: ..., prompt: ..., isCommunity: ..., ownerUid: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedTrackRef(dataConnect, seedTrackVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.track_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.track_insert);
});
```

## SeedUser
You can execute the `SeedUser` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedUser(vars: SeedUserVariables): MutationPromise<SeedUserData, SeedUserVariables>;

interface SeedUserRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedUserVariables): MutationRef<SeedUserData, SeedUserVariables>;
}
export const seedUserRef: SeedUserRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedUser(dc: DataConnect, vars: SeedUserVariables): MutationPromise<SeedUserData, SeedUserVariables>;

interface SeedUserRef {
  ...
  (dc: DataConnect, vars: SeedUserVariables): MutationRef<SeedUserData, SeedUserVariables>;
}
export const seedUserRef: SeedUserRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedUserRef:
```typescript
const name = seedUserRef.operationName;
console.log(name);
```

### Variables
The `SeedUser` mutation requires an argument of type `SeedUserVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedUserVariables {
  uid: string;
  username: string;
  email: string;
}
```
### Return Type
Recall that executing the `SeedUser` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedUserData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedUserData {
  user_upsert: User_Key;
}
```
### Using `SeedUser`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedUser, SeedUserVariables } from '@musically/dataconnect';

// The `SeedUser` mutation requires an argument of type `SeedUserVariables`:
const seedUserVars: SeedUserVariables = {
  uid: ..., 
  username: ..., 
  email: ..., 
};

// Call the `seedUser()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedUser(seedUserVars);
// Variables can be defined inline as well.
const { data } = await seedUser({ uid: ..., username: ..., email: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedUser(dataConnect, seedUserVars);

console.log(data.user_upsert);

// Or, you can use the `Promise` API.
seedUser(seedUserVars).then((response) => {
  const data = response.data;
  console.log(data.user_upsert);
});
```

### Using `SeedUser`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedUserRef, SeedUserVariables } from '@musically/dataconnect';

// The `SeedUser` mutation requires an argument of type `SeedUserVariables`:
const seedUserVars: SeedUserVariables = {
  uid: ..., 
  username: ..., 
  email: ..., 
};

// Call the `seedUserRef()` function to get a reference to the mutation.
const ref = seedUserRef(seedUserVars);
// Variables can be defined inline as well.
const ref = seedUserRef({ uid: ..., username: ..., email: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedUserRef(dataConnect, seedUserVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.user_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.user_upsert);
});
```

## SeedAIPreset
You can execute the `SeedAIPreset` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedAiPreset(vars: SeedAiPresetVariables): MutationPromise<SeedAiPresetData, SeedAiPresetVariables>;

interface SeedAiPresetRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedAiPresetVariables): MutationRef<SeedAiPresetData, SeedAiPresetVariables>;
}
export const seedAiPresetRef: SeedAiPresetRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedAiPreset(dc: DataConnect, vars: SeedAiPresetVariables): MutationPromise<SeedAiPresetData, SeedAiPresetVariables>;

interface SeedAiPresetRef {
  ...
  (dc: DataConnect, vars: SeedAiPresetVariables): MutationRef<SeedAiPresetData, SeedAiPresetVariables>;
}
export const seedAiPresetRef: SeedAiPresetRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedAiPresetRef:
```typescript
const name = seedAiPresetRef.operationName;
console.log(name);
```

### Variables
The `SeedAIPreset` mutation requires an argument of type `SeedAiPresetVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedAiPresetVariables {
  name: string;
  promptFragment: string;
  imageUrl?: string | null;
}
```
### Return Type
Recall that executing the `SeedAIPreset` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedAiPresetData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedAiPresetData {
  aIPreset_insert: AIPreset_Key;
}
```
### Using `SeedAIPreset`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedAiPreset, SeedAiPresetVariables } from '@musically/dataconnect';

// The `SeedAIPreset` mutation requires an argument of type `SeedAiPresetVariables`:
const seedAiPresetVars: SeedAiPresetVariables = {
  name: ..., 
  promptFragment: ..., 
  imageUrl: ..., // optional
};

// Call the `seedAiPreset()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedAiPreset(seedAiPresetVars);
// Variables can be defined inline as well.
const { data } = await seedAiPreset({ name: ..., promptFragment: ..., imageUrl: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedAiPreset(dataConnect, seedAiPresetVars);

console.log(data.aIPreset_insert);

// Or, you can use the `Promise` API.
seedAiPreset(seedAiPresetVars).then((response) => {
  const data = response.data;
  console.log(data.aIPreset_insert);
});
```

### Using `SeedAIPreset`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedAiPresetRef, SeedAiPresetVariables } from '@musically/dataconnect';

// The `SeedAIPreset` mutation requires an argument of type `SeedAiPresetVariables`:
const seedAiPresetVars: SeedAiPresetVariables = {
  name: ..., 
  promptFragment: ..., 
  imageUrl: ..., // optional
};

// Call the `seedAiPresetRef()` function to get a reference to the mutation.
const ref = seedAiPresetRef(seedAiPresetVars);
// Variables can be defined inline as well.
const ref = seedAiPresetRef({ name: ..., promptFragment: ..., imageUrl: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedAiPresetRef(dataConnect, seedAiPresetVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.aIPreset_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.aIPreset_insert);
});
```

## UpdateShowContext
You can execute the `UpdateShowContext` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
updateShowContext(vars: UpdateShowContextVariables): MutationPromise<UpdateShowContextData, UpdateShowContextVariables>;

interface UpdateShowContextRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateShowContextVariables): MutationRef<UpdateShowContextData, UpdateShowContextVariables>;
}
export const updateShowContextRef: UpdateShowContextRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
updateShowContext(dc: DataConnect, vars: UpdateShowContextVariables): MutationPromise<UpdateShowContextData, UpdateShowContextVariables>;

interface UpdateShowContextRef {
  ...
  (dc: DataConnect, vars: UpdateShowContextVariables): MutationRef<UpdateShowContextData, UpdateShowContextVariables>;
}
export const updateShowContextRef: UpdateShowContextRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the updateShowContextRef:
```typescript
const name = updateShowContextRef.operationName;
console.log(name);
```

### Variables
The `UpdateShowContext` mutation requires an argument of type `UpdateShowContextVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpdateShowContextVariables {
  id: string;
  storyContext: string;
}
```
### Return Type
Recall that executing the `UpdateShowContext` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpdateShowContextData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpdateShowContextData {
  show_update?: Show_Key | null;
}
```
### Using `UpdateShowContext`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, updateShowContext, UpdateShowContextVariables } from '@musically/dataconnect';

// The `UpdateShowContext` mutation requires an argument of type `UpdateShowContextVariables`:
const updateShowContextVars: UpdateShowContextVariables = {
  id: ..., 
  storyContext: ..., 
};

// Call the `updateShowContext()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await updateShowContext(updateShowContextVars);
// Variables can be defined inline as well.
const { data } = await updateShowContext({ id: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await updateShowContext(dataConnect, updateShowContextVars);

console.log(data.show_update);

// Or, you can use the `Promise` API.
updateShowContext(updateShowContextVars).then((response) => {
  const data = response.data;
  console.log(data.show_update);
});
```

### Using `UpdateShowContext`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, updateShowContextRef, UpdateShowContextVariables } from '@musically/dataconnect';

// The `UpdateShowContext` mutation requires an argument of type `UpdateShowContextVariables`:
const updateShowContextVars: UpdateShowContextVariables = {
  id: ..., 
  storyContext: ..., 
};

// Call the `updateShowContextRef()` function to get a reference to the mutation.
const ref = updateShowContextRef(updateShowContextVars);
// Variables can be defined inline as well.
const ref = updateShowContextRef({ id: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = updateShowContextRef(dataConnect, updateShowContextVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.show_update);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.show_update);
});
```

## UpdateAudiobookContext
You can execute the `UpdateAudiobookContext` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
updateAudiobookContext(vars: UpdateAudiobookContextVariables): MutationPromise<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;

interface UpdateAudiobookContextRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateAudiobookContextVariables): MutationRef<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;
}
export const updateAudiobookContextRef: UpdateAudiobookContextRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
updateAudiobookContext(dc: DataConnect, vars: UpdateAudiobookContextVariables): MutationPromise<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;

interface UpdateAudiobookContextRef {
  ...
  (dc: DataConnect, vars: UpdateAudiobookContextVariables): MutationRef<UpdateAudiobookContextData, UpdateAudiobookContextVariables>;
}
export const updateAudiobookContextRef: UpdateAudiobookContextRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the updateAudiobookContextRef:
```typescript
const name = updateAudiobookContextRef.operationName;
console.log(name);
```

### Variables
The `UpdateAudiobookContext` mutation requires an argument of type `UpdateAudiobookContextVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpdateAudiobookContextVariables {
  id: string;
  storyContext: string;
}
```
### Return Type
Recall that executing the `UpdateAudiobookContext` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpdateAudiobookContextData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpdateAudiobookContextData {
  audiobook_update?: Audiobook_Key | null;
}
```
### Using `UpdateAudiobookContext`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, updateAudiobookContext, UpdateAudiobookContextVariables } from '@musically/dataconnect';

// The `UpdateAudiobookContext` mutation requires an argument of type `UpdateAudiobookContextVariables`:
const updateAudiobookContextVars: UpdateAudiobookContextVariables = {
  id: ..., 
  storyContext: ..., 
};

// Call the `updateAudiobookContext()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await updateAudiobookContext(updateAudiobookContextVars);
// Variables can be defined inline as well.
const { data } = await updateAudiobookContext({ id: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await updateAudiobookContext(dataConnect, updateAudiobookContextVars);

console.log(data.audiobook_update);

// Or, you can use the `Promise` API.
updateAudiobookContext(updateAudiobookContextVars).then((response) => {
  const data = response.data;
  console.log(data.audiobook_update);
});
```

### Using `UpdateAudiobookContext`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, updateAudiobookContextRef, UpdateAudiobookContextVariables } from '@musically/dataconnect';

// The `UpdateAudiobookContext` mutation requires an argument of type `UpdateAudiobookContextVariables`:
const updateAudiobookContextVars: UpdateAudiobookContextVariables = {
  id: ..., 
  storyContext: ..., 
};

// Call the `updateAudiobookContextRef()` function to get a reference to the mutation.
const ref = updateAudiobookContextRef(updateAudiobookContextVars);
// Variables can be defined inline as well.
const ref = updateAudiobookContextRef({ id: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = updateAudiobookContextRef(dataConnect, updateAudiobookContextVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.audiobook_update);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.audiobook_update);
});
```

## SeedAuthor
You can execute the `SeedAuthor` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedAuthor(vars: SeedAuthorVariables): MutationPromise<SeedAuthorData, SeedAuthorVariables>;

interface SeedAuthorRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedAuthorVariables): MutationRef<SeedAuthorData, SeedAuthorVariables>;
}
export const seedAuthorRef: SeedAuthorRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedAuthor(dc: DataConnect, vars: SeedAuthorVariables): MutationPromise<SeedAuthorData, SeedAuthorVariables>;

interface SeedAuthorRef {
  ...
  (dc: DataConnect, vars: SeedAuthorVariables): MutationRef<SeedAuthorData, SeedAuthorVariables>;
}
export const seedAuthorRef: SeedAuthorRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedAuthorRef:
```typescript
const name = seedAuthorRef.operationName;
console.log(name);
```

### Variables
The `SeedAuthor` mutation requires an argument of type `SeedAuthorVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedAuthorVariables {
  id: string;
  name: string;
  bio?: string | null;
}
```
### Return Type
Recall that executing the `SeedAuthor` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedAuthorData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedAuthorData {
  author_upsert: Author_Key;
}
```
### Using `SeedAuthor`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedAuthor, SeedAuthorVariables } from '@musically/dataconnect';

// The `SeedAuthor` mutation requires an argument of type `SeedAuthorVariables`:
const seedAuthorVars: SeedAuthorVariables = {
  id: ..., 
  name: ..., 
  bio: ..., // optional
};

// Call the `seedAuthor()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedAuthor(seedAuthorVars);
// Variables can be defined inline as well.
const { data } = await seedAuthor({ id: ..., name: ..., bio: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedAuthor(dataConnect, seedAuthorVars);

console.log(data.author_upsert);

// Or, you can use the `Promise` API.
seedAuthor(seedAuthorVars).then((response) => {
  const data = response.data;
  console.log(data.author_upsert);
});
```

### Using `SeedAuthor`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedAuthorRef, SeedAuthorVariables } from '@musically/dataconnect';

// The `SeedAuthor` mutation requires an argument of type `SeedAuthorVariables`:
const seedAuthorVars: SeedAuthorVariables = {
  id: ..., 
  name: ..., 
  bio: ..., // optional
};

// Call the `seedAuthorRef()` function to get a reference to the mutation.
const ref = seedAuthorRef(seedAuthorVars);
// Variables can be defined inline as well.
const ref = seedAuthorRef({ id: ..., name: ..., bio: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedAuthorRef(dataConnect, seedAuthorVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.author_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.author_upsert);
});
```

## CreateAudiobook
You can execute the `CreateAudiobook` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createAudiobook(vars: CreateAudiobookVariables): MutationPromise<CreateAudiobookData, CreateAudiobookVariables>;

interface CreateAudiobookRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateAudiobookVariables): MutationRef<CreateAudiobookData, CreateAudiobookVariables>;
}
export const createAudiobookRef: CreateAudiobookRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createAudiobook(dc: DataConnect, vars: CreateAudiobookVariables): MutationPromise<CreateAudiobookData, CreateAudiobookVariables>;

interface CreateAudiobookRef {
  ...
  (dc: DataConnect, vars: CreateAudiobookVariables): MutationRef<CreateAudiobookData, CreateAudiobookVariables>;
}
export const createAudiobookRef: CreateAudiobookRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createAudiobookRef:
```typescript
const name = createAudiobookRef.operationName;
console.log(name);
```

### Variables
The `CreateAudiobook` mutation requires an argument of type `CreateAudiobookVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateAudiobookVariables {
  title: string;
  authorId: string;
  narrator?: string | null;
  coverUrl?: string | null;
  storyContext?: string | null;
}
```
### Return Type
Recall that executing the `CreateAudiobook` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateAudiobookData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateAudiobookData {
  audiobook_insert: Audiobook_Key;
}
```
### Using `CreateAudiobook`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createAudiobook, CreateAudiobookVariables } from '@musically/dataconnect';

// The `CreateAudiobook` mutation requires an argument of type `CreateAudiobookVariables`:
const createAudiobookVars: CreateAudiobookVariables = {
  title: ..., 
  authorId: ..., 
  narrator: ..., // optional
  coverUrl: ..., // optional
  storyContext: ..., // optional
};

// Call the `createAudiobook()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createAudiobook(createAudiobookVars);
// Variables can be defined inline as well.
const { data } = await createAudiobook({ title: ..., authorId: ..., narrator: ..., coverUrl: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createAudiobook(dataConnect, createAudiobookVars);

console.log(data.audiobook_insert);

// Or, you can use the `Promise` API.
createAudiobook(createAudiobookVars).then((response) => {
  const data = response.data;
  console.log(data.audiobook_insert);
});
```

### Using `CreateAudiobook`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createAudiobookRef, CreateAudiobookVariables } from '@musically/dataconnect';

// The `CreateAudiobook` mutation requires an argument of type `CreateAudiobookVariables`:
const createAudiobookVars: CreateAudiobookVariables = {
  title: ..., 
  authorId: ..., 
  narrator: ..., // optional
  coverUrl: ..., // optional
  storyContext: ..., // optional
};

// Call the `createAudiobookRef()` function to get a reference to the mutation.
const ref = createAudiobookRef(createAudiobookVars);
// Variables can be defined inline as well.
const ref = createAudiobookRef({ title: ..., authorId: ..., narrator: ..., coverUrl: ..., storyContext: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createAudiobookRef(dataConnect, createAudiobookVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.audiobook_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.audiobook_insert);
});
```

## SeedEpisode
You can execute the `SeedEpisode` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedEpisode(vars: SeedEpisodeVariables): MutationPromise<SeedEpisodeData, SeedEpisodeVariables>;

interface SeedEpisodeRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedEpisodeVariables): MutationRef<SeedEpisodeData, SeedEpisodeVariables>;
}
export const seedEpisodeRef: SeedEpisodeRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedEpisode(dc: DataConnect, vars: SeedEpisodeVariables): MutationPromise<SeedEpisodeData, SeedEpisodeVariables>;

interface SeedEpisodeRef {
  ...
  (dc: DataConnect, vars: SeedEpisodeVariables): MutationRef<SeedEpisodeData, SeedEpisodeVariables>;
}
export const seedEpisodeRef: SeedEpisodeRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedEpisodeRef:
```typescript
const name = seedEpisodeRef.operationName;
console.log(name);
```

### Variables
The `SeedEpisode` mutation requires an argument of type `SeedEpisodeVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedEpisodeVariables {
  showId: string;
  title: string;
  description?: string | null;
  audioUrl?: string | null;
  durationMs?: number | null;
  publishDate: TimestampString;
}
```
### Return Type
Recall that executing the `SeedEpisode` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedEpisodeData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedEpisodeData {
  episode_insert: Episode_Key;
}
```
### Using `SeedEpisode`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedEpisode, SeedEpisodeVariables } from '@musically/dataconnect';

// The `SeedEpisode` mutation requires an argument of type `SeedEpisodeVariables`:
const seedEpisodeVars: SeedEpisodeVariables = {
  showId: ..., 
  title: ..., 
  description: ..., // optional
  audioUrl: ..., // optional
  durationMs: ..., // optional
  publishDate: ..., 
};

// Call the `seedEpisode()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedEpisode(seedEpisodeVars);
// Variables can be defined inline as well.
const { data } = await seedEpisode({ showId: ..., title: ..., description: ..., audioUrl: ..., durationMs: ..., publishDate: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedEpisode(dataConnect, seedEpisodeVars);

console.log(data.episode_insert);

// Or, you can use the `Promise` API.
seedEpisode(seedEpisodeVars).then((response) => {
  const data = response.data;
  console.log(data.episode_insert);
});
```

### Using `SeedEpisode`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedEpisodeRef, SeedEpisodeVariables } from '@musically/dataconnect';

// The `SeedEpisode` mutation requires an argument of type `SeedEpisodeVariables`:
const seedEpisodeVars: SeedEpisodeVariables = {
  showId: ..., 
  title: ..., 
  description: ..., // optional
  audioUrl: ..., // optional
  durationMs: ..., // optional
  publishDate: ..., 
};

// Call the `seedEpisodeRef()` function to get a reference to the mutation.
const ref = seedEpisodeRef(seedEpisodeVars);
// Variables can be defined inline as well.
const ref = seedEpisodeRef({ showId: ..., title: ..., description: ..., audioUrl: ..., durationMs: ..., publishDate: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedEpisodeRef(dataConnect, seedEpisodeVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.episode_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.episode_insert);
});
```

## SeedChapter
You can execute the `SeedChapter` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedChapter(vars: SeedChapterVariables): MutationPromise<SeedChapterData, SeedChapterVariables>;

interface SeedChapterRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedChapterVariables): MutationRef<SeedChapterData, SeedChapterVariables>;
}
export const seedChapterRef: SeedChapterRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedChapter(dc: DataConnect, vars: SeedChapterVariables): MutationPromise<SeedChapterData, SeedChapterVariables>;

interface SeedChapterRef {
  ...
  (dc: DataConnect, vars: SeedChapterVariables): MutationRef<SeedChapterData, SeedChapterVariables>;
}
export const seedChapterRef: SeedChapterRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedChapterRef:
```typescript
const name = seedChapterRef.operationName;
console.log(name);
```

### Variables
The `SeedChapter` mutation requires an argument of type `SeedChapterVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedChapterVariables {
  audiobookId: string;
  title: string;
  chapterNumber: number;
  audioUrl?: string | null;
  durationMs?: number | null;
}
```
### Return Type
Recall that executing the `SeedChapter` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedChapterData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedChapterData {
  chapter_insert: Chapter_Key;
}
```
### Using `SeedChapter`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedChapter, SeedChapterVariables } from '@musically/dataconnect';

// The `SeedChapter` mutation requires an argument of type `SeedChapterVariables`:
const seedChapterVars: SeedChapterVariables = {
  audiobookId: ..., 
  title: ..., 
  chapterNumber: ..., 
  audioUrl: ..., // optional
  durationMs: ..., // optional
};

// Call the `seedChapter()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedChapter(seedChapterVars);
// Variables can be defined inline as well.
const { data } = await seedChapter({ audiobookId: ..., title: ..., chapterNumber: ..., audioUrl: ..., durationMs: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedChapter(dataConnect, seedChapterVars);

console.log(data.chapter_insert);

// Or, you can use the `Promise` API.
seedChapter(seedChapterVars).then((response) => {
  const data = response.data;
  console.log(data.chapter_insert);
});
```

### Using `SeedChapter`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedChapterRef, SeedChapterVariables } from '@musically/dataconnect';

// The `SeedChapter` mutation requires an argument of type `SeedChapterVariables`:
const seedChapterVars: SeedChapterVariables = {
  audiobookId: ..., 
  title: ..., 
  chapterNumber: ..., 
  audioUrl: ..., // optional
  durationMs: ..., // optional
};

// Call the `seedChapterRef()` function to get a reference to the mutation.
const ref = seedChapterRef(seedChapterVars);
// Variables can be defined inline as well.
const ref = seedChapterRef({ audiobookId: ..., title: ..., chapterNumber: ..., audioUrl: ..., durationMs: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedChapterRef(dataConnect, seedChapterVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.chapter_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.chapter_insert);
});
```

## UpdateTrackVideo
You can execute the `UpdateTrackVideo` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
updateTrackVideo(vars: UpdateTrackVideoVariables): MutationPromise<UpdateTrackVideoData, UpdateTrackVideoVariables>;

interface UpdateTrackVideoRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateTrackVideoVariables): MutationRef<UpdateTrackVideoData, UpdateTrackVideoVariables>;
}
export const updateTrackVideoRef: UpdateTrackVideoRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
updateTrackVideo(dc: DataConnect, vars: UpdateTrackVideoVariables): MutationPromise<UpdateTrackVideoData, UpdateTrackVideoVariables>;

interface UpdateTrackVideoRef {
  ...
  (dc: DataConnect, vars: UpdateTrackVideoVariables): MutationRef<UpdateTrackVideoData, UpdateTrackVideoVariables>;
}
export const updateTrackVideoRef: UpdateTrackVideoRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the updateTrackVideoRef:
```typescript
const name = updateTrackVideoRef.operationName;
console.log(name);
```

### Variables
The `UpdateTrackVideo` mutation requires an argument of type `UpdateTrackVideoVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpdateTrackVideoVariables {
  id: string;
  videoUrl: string;
}
```
### Return Type
Recall that executing the `UpdateTrackVideo` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpdateTrackVideoData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpdateTrackVideoData {
  track_update?: Track_Key | null;
}
```
### Using `UpdateTrackVideo`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, updateTrackVideo, UpdateTrackVideoVariables } from '@musically/dataconnect';

// The `UpdateTrackVideo` mutation requires an argument of type `UpdateTrackVideoVariables`:
const updateTrackVideoVars: UpdateTrackVideoVariables = {
  id: ..., 
  videoUrl: ..., 
};

// Call the `updateTrackVideo()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await updateTrackVideo(updateTrackVideoVars);
// Variables can be defined inline as well.
const { data } = await updateTrackVideo({ id: ..., videoUrl: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await updateTrackVideo(dataConnect, updateTrackVideoVars);

console.log(data.track_update);

// Or, you can use the `Promise` API.
updateTrackVideo(updateTrackVideoVars).then((response) => {
  const data = response.data;
  console.log(data.track_update);
});
```

### Using `UpdateTrackVideo`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, updateTrackVideoRef, UpdateTrackVideoVariables } from '@musically/dataconnect';

// The `UpdateTrackVideo` mutation requires an argument of type `UpdateTrackVideoVariables`:
const updateTrackVideoVars: UpdateTrackVideoVariables = {
  id: ..., 
  videoUrl: ..., 
};

// Call the `updateTrackVideoRef()` function to get a reference to the mutation.
const ref = updateTrackVideoRef(updateTrackVideoVars);
// Variables can be defined inline as well.
const ref = updateTrackVideoRef({ id: ..., videoUrl: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = updateTrackVideoRef(dataConnect, updateTrackVideoVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.track_update);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.track_update);
});
```

## UpdateEpisodeAudio
You can execute the `UpdateEpisodeAudio` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
updateEpisodeAudio(vars: UpdateEpisodeAudioVariables): MutationPromise<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;

interface UpdateEpisodeAudioRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: UpdateEpisodeAudioVariables): MutationRef<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;
}
export const updateEpisodeAudioRef: UpdateEpisodeAudioRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
updateEpisodeAudio(dc: DataConnect, vars: UpdateEpisodeAudioVariables): MutationPromise<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;

interface UpdateEpisodeAudioRef {
  ...
  (dc: DataConnect, vars: UpdateEpisodeAudioVariables): MutationRef<UpdateEpisodeAudioData, UpdateEpisodeAudioVariables>;
}
export const updateEpisodeAudioRef: UpdateEpisodeAudioRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the updateEpisodeAudioRef:
```typescript
const name = updateEpisodeAudioRef.operationName;
console.log(name);
```

### Variables
The `UpdateEpisodeAudio` mutation requires an argument of type `UpdateEpisodeAudioVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpdateEpisodeAudioVariables {
  id: string;
  audioUrl: string;
}
```
### Return Type
Recall that executing the `UpdateEpisodeAudio` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `UpdateEpisodeAudioData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface UpdateEpisodeAudioData {
  episode_update?: Episode_Key | null;
}
```
### Using `UpdateEpisodeAudio`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, updateEpisodeAudio, UpdateEpisodeAudioVariables } from '@musically/dataconnect';

// The `UpdateEpisodeAudio` mutation requires an argument of type `UpdateEpisodeAudioVariables`:
const updateEpisodeAudioVars: UpdateEpisodeAudioVariables = {
  id: ..., 
  audioUrl: ..., 
};

// Call the `updateEpisodeAudio()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await updateEpisodeAudio(updateEpisodeAudioVars);
// Variables can be defined inline as well.
const { data } = await updateEpisodeAudio({ id: ..., audioUrl: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await updateEpisodeAudio(dataConnect, updateEpisodeAudioVars);

console.log(data.episode_update);

// Or, you can use the `Promise` API.
updateEpisodeAudio(updateEpisodeAudioVars).then((response) => {
  const data = response.data;
  console.log(data.episode_update);
});
```

### Using `UpdateEpisodeAudio`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, updateEpisodeAudioRef, UpdateEpisodeAudioVariables } from '@musically/dataconnect';

// The `UpdateEpisodeAudio` mutation requires an argument of type `UpdateEpisodeAudioVariables`:
const updateEpisodeAudioVars: UpdateEpisodeAudioVariables = {
  id: ..., 
  audioUrl: ..., 
};

// Call the `updateEpisodeAudioRef()` function to get a reference to the mutation.
const ref = updateEpisodeAudioRef(updateEpisodeAudioVars);
// Variables can be defined inline as well.
const ref = updateEpisodeAudioRef({ id: ..., audioUrl: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = updateEpisodeAudioRef(dataConnect, updateEpisodeAudioVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.episode_update);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.episode_update);
});
```

## SeedInstrument
You can execute the `SeedInstrument` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
seedInstrument(vars: SeedInstrumentVariables): MutationPromise<SeedInstrumentData, SeedInstrumentVariables>;

interface SeedInstrumentRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SeedInstrumentVariables): MutationRef<SeedInstrumentData, SeedInstrumentVariables>;
}
export const seedInstrumentRef: SeedInstrumentRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
seedInstrument(dc: DataConnect, vars: SeedInstrumentVariables): MutationPromise<SeedInstrumentData, SeedInstrumentVariables>;

interface SeedInstrumentRef {
  ...
  (dc: DataConnect, vars: SeedInstrumentVariables): MutationRef<SeedInstrumentData, SeedInstrumentVariables>;
}
export const seedInstrumentRef: SeedInstrumentRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the seedInstrumentRef:
```typescript
const name = seedInstrumentRef.operationName;
console.log(name);
```

### Variables
The `SeedInstrument` mutation requires an argument of type `SeedInstrumentVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SeedInstrumentVariables {
  name: string;
  iconUrl?: string | null;
}
```
### Return Type
Recall that executing the `SeedInstrument` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SeedInstrumentData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SeedInstrumentData {
  instrument_upsert: Instrument_Key;
}
```
### Using `SeedInstrument`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, seedInstrument, SeedInstrumentVariables } from '@musically/dataconnect';

// The `SeedInstrument` mutation requires an argument of type `SeedInstrumentVariables`:
const seedInstrumentVars: SeedInstrumentVariables = {
  name: ..., 
  iconUrl: ..., // optional
};

// Call the `seedInstrument()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await seedInstrument(seedInstrumentVars);
// Variables can be defined inline as well.
const { data } = await seedInstrument({ name: ..., iconUrl: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await seedInstrument(dataConnect, seedInstrumentVars);

console.log(data.instrument_upsert);

// Or, you can use the `Promise` API.
seedInstrument(seedInstrumentVars).then((response) => {
  const data = response.data;
  console.log(data.instrument_upsert);
});
```

### Using `SeedInstrument`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, seedInstrumentRef, SeedInstrumentVariables } from '@musically/dataconnect';

// The `SeedInstrument` mutation requires an argument of type `SeedInstrumentVariables`:
const seedInstrumentVars: SeedInstrumentVariables = {
  name: ..., 
  iconUrl: ..., // optional
};

// Call the `seedInstrumentRef()` function to get a reference to the mutation.
const ref = seedInstrumentRef(seedInstrumentVars);
// Variables can be defined inline as well.
const ref = seedInstrumentRef({ name: ..., iconUrl: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = seedInstrumentRef(dataConnect, seedInstrumentVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.instrument_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.instrument_upsert);
});
```

## CreateCoverArt
You can execute the `CreateCoverArt` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createCoverArt(vars: CreateCoverArtVariables): MutationPromise<CreateCoverArtData, CreateCoverArtVariables>;

interface CreateCoverArtRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateCoverArtVariables): MutationRef<CreateCoverArtData, CreateCoverArtVariables>;
}
export const createCoverArtRef: CreateCoverArtRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createCoverArt(dc: DataConnect, vars: CreateCoverArtVariables): MutationPromise<CreateCoverArtData, CreateCoverArtVariables>;

interface CreateCoverArtRef {
  ...
  (dc: DataConnect, vars: CreateCoverArtVariables): MutationRef<CreateCoverArtData, CreateCoverArtVariables>;
}
export const createCoverArtRef: CreateCoverArtRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createCoverArtRef:
```typescript
const name = createCoverArtRef.operationName;
console.log(name);
```

### Variables
The `CreateCoverArt` mutation requires an argument of type `CreateCoverArtVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateCoverArtVariables {
  trackId: string;
  imageUrl: string;
  promptUsed?: string | null;
  stylePreset?: string | null;
}
```
### Return Type
Recall that executing the `CreateCoverArt` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateCoverArtData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateCoverArtData {
  coverArt_insert: CoverArt_Key;
}
```
### Using `CreateCoverArt`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createCoverArt, CreateCoverArtVariables } from '@musically/dataconnect';

// The `CreateCoverArt` mutation requires an argument of type `CreateCoverArtVariables`:
const createCoverArtVars: CreateCoverArtVariables = {
  trackId: ..., 
  imageUrl: ..., 
  promptUsed: ..., // optional
  stylePreset: ..., // optional
};

// Call the `createCoverArt()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createCoverArt(createCoverArtVars);
// Variables can be defined inline as well.
const { data } = await createCoverArt({ trackId: ..., imageUrl: ..., promptUsed: ..., stylePreset: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createCoverArt(dataConnect, createCoverArtVars);

console.log(data.coverArt_insert);

// Or, you can use the `Promise` API.
createCoverArt(createCoverArtVars).then((response) => {
  const data = response.data;
  console.log(data.coverArt_insert);
});
```

### Using `CreateCoverArt`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createCoverArtRef, CreateCoverArtVariables } from '@musically/dataconnect';

// The `CreateCoverArt` mutation requires an argument of type `CreateCoverArtVariables`:
const createCoverArtVars: CreateCoverArtVariables = {
  trackId: ..., 
  imageUrl: ..., 
  promptUsed: ..., // optional
  stylePreset: ..., // optional
};

// Call the `createCoverArtRef()` function to get a reference to the mutation.
const ref = createCoverArtRef(createCoverArtVars);
// Variables can be defined inline as well.
const ref = createCoverArtRef({ trackId: ..., imageUrl: ..., promptUsed: ..., stylePreset: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createCoverArtRef(dataConnect, createCoverArtVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.coverArt_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.coverArt_insert);
});
```

## CreateMusicVideo
You can execute the `CreateMusicVideo` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createMusicVideo(vars: CreateMusicVideoVariables): MutationPromise<CreateMusicVideoData, CreateMusicVideoVariables>;

interface CreateMusicVideoRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateMusicVideoVariables): MutationRef<CreateMusicVideoData, CreateMusicVideoVariables>;
}
export const createMusicVideoRef: CreateMusicVideoRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createMusicVideo(dc: DataConnect, vars: CreateMusicVideoVariables): MutationPromise<CreateMusicVideoData, CreateMusicVideoVariables>;

interface CreateMusicVideoRef {
  ...
  (dc: DataConnect, vars: CreateMusicVideoVariables): MutationRef<CreateMusicVideoData, CreateMusicVideoVariables>;
}
export const createMusicVideoRef: CreateMusicVideoRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createMusicVideoRef:
```typescript
const name = createMusicVideoRef.operationName;
console.log(name);
```

### Variables
The `CreateMusicVideo` mutation requires an argument of type `CreateMusicVideoVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateMusicVideoVariables {
  trackId: string;
  videoUrl: string;
  promptUsed?: string | null;
  motionPreset?: string | null;
}
```
### Return Type
Recall that executing the `CreateMusicVideo` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateMusicVideoData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateMusicVideoData {
  musicVideo_insert: MusicVideo_Key;
}
```
### Using `CreateMusicVideo`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createMusicVideo, CreateMusicVideoVariables } from '@musically/dataconnect';

// The `CreateMusicVideo` mutation requires an argument of type `CreateMusicVideoVariables`:
const createMusicVideoVars: CreateMusicVideoVariables = {
  trackId: ..., 
  videoUrl: ..., 
  promptUsed: ..., // optional
  motionPreset: ..., // optional
};

// Call the `createMusicVideo()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createMusicVideo(createMusicVideoVars);
// Variables can be defined inline as well.
const { data } = await createMusicVideo({ trackId: ..., videoUrl: ..., promptUsed: ..., motionPreset: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createMusicVideo(dataConnect, createMusicVideoVars);

console.log(data.musicVideo_insert);

// Or, you can use the `Promise` API.
createMusicVideo(createMusicVideoVars).then((response) => {
  const data = response.data;
  console.log(data.musicVideo_insert);
});
```

### Using `CreateMusicVideo`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createMusicVideoRef, CreateMusicVideoVariables } from '@musically/dataconnect';

// The `CreateMusicVideo` mutation requires an argument of type `CreateMusicVideoVariables`:
const createMusicVideoVars: CreateMusicVideoVariables = {
  trackId: ..., 
  videoUrl: ..., 
  promptUsed: ..., // optional
  motionPreset: ..., // optional
};

// Call the `createMusicVideoRef()` function to get a reference to the mutation.
const ref = createMusicVideoRef(createMusicVideoVars);
// Variables can be defined inline as well.
const ref = createMusicVideoRef({ trackId: ..., videoUrl: ..., promptUsed: ..., motionPreset: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createMusicVideoRef(dataConnect, createMusicVideoVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.musicVideo_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.musicVideo_insert);
});
```

## CreateJamSessionHistory
You can execute the `CreateJamSessionHistory` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
createJamSessionHistory(vars: CreateJamSessionHistoryVariables): MutationPromise<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;

interface CreateJamSessionHistoryRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: CreateJamSessionHistoryVariables): MutationRef<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;
}
export const createJamSessionHistoryRef: CreateJamSessionHistoryRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
createJamSessionHistory(dc: DataConnect, vars: CreateJamSessionHistoryVariables): MutationPromise<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;

interface CreateJamSessionHistoryRef {
  ...
  (dc: DataConnect, vars: CreateJamSessionHistoryVariables): MutationRef<CreateJamSessionHistoryData, CreateJamSessionHistoryVariables>;
}
export const createJamSessionHistoryRef: CreateJamSessionHistoryRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the createJamSessionHistoryRef:
```typescript
const name = createJamSessionHistoryRef.operationName;
console.log(name);
```

### Variables
The `CreateJamSessionHistory` mutation requires an argument of type `CreateJamSessionHistoryVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface CreateJamSessionHistoryVariables {
  roomId: string;
  gameMode: string;
  participantCount: number;
}
```
### Return Type
Recall that executing the `CreateJamSessionHistory` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `CreateJamSessionHistoryData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface CreateJamSessionHistoryData {
  jamSessionHistory_insert: JamSessionHistory_Key;
}
```
### Using `CreateJamSessionHistory`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, createJamSessionHistory, CreateJamSessionHistoryVariables } from '@musically/dataconnect';

// The `CreateJamSessionHistory` mutation requires an argument of type `CreateJamSessionHistoryVariables`:
const createJamSessionHistoryVars: CreateJamSessionHistoryVariables = {
  roomId: ..., 
  gameMode: ..., 
  participantCount: ..., 
};

// Call the `createJamSessionHistory()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createJamSessionHistory(createJamSessionHistoryVars);
// Variables can be defined inline as well.
const { data } = await createJamSessionHistory({ roomId: ..., gameMode: ..., participantCount: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await createJamSessionHistory(dataConnect, createJamSessionHistoryVars);

console.log(data.jamSessionHistory_insert);

// Or, you can use the `Promise` API.
createJamSessionHistory(createJamSessionHistoryVars).then((response) => {
  const data = response.data;
  console.log(data.jamSessionHistory_insert);
});
```

### Using `CreateJamSessionHistory`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, createJamSessionHistoryRef, CreateJamSessionHistoryVariables } from '@musically/dataconnect';

// The `CreateJamSessionHistory` mutation requires an argument of type `CreateJamSessionHistoryVariables`:
const createJamSessionHistoryVars: CreateJamSessionHistoryVariables = {
  roomId: ..., 
  gameMode: ..., 
  participantCount: ..., 
};

// Call the `createJamSessionHistoryRef()` function to get a reference to the mutation.
const ref = createJamSessionHistoryRef(createJamSessionHistoryVars);
// Variables can be defined inline as well.
const ref = createJamSessionHistoryRef({ roomId: ..., gameMode: ..., participantCount: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = createJamSessionHistoryRef(dataConnect, createJamSessionHistoryVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.jamSessionHistory_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.jamSessionHistory_insert);
});
```

## SaveUserEpisodeProgress
You can execute the `SaveUserEpisodeProgress` mutation using the following action shortcut function, or by calling `executeMutation()` after calling the following `MutationRef` function, both of which are defined in [dataconnect/index.d.ts](./index.d.ts):
```typescript
saveUserEpisodeProgress(vars: SaveUserEpisodeProgressVariables): MutationPromise<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;

interface SaveUserEpisodeProgressRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars: SaveUserEpisodeProgressVariables): MutationRef<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;
}
export const saveUserEpisodeProgressRef: SaveUserEpisodeProgressRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
saveUserEpisodeProgress(dc: DataConnect, vars: SaveUserEpisodeProgressVariables): MutationPromise<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;

interface SaveUserEpisodeProgressRef {
  ...
  (dc: DataConnect, vars: SaveUserEpisodeProgressVariables): MutationRef<SaveUserEpisodeProgressData, SaveUserEpisodeProgressVariables>;
}
export const saveUserEpisodeProgressRef: SaveUserEpisodeProgressRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the saveUserEpisodeProgressRef:
```typescript
const name = saveUserEpisodeProgressRef.operationName;
console.log(name);
```

### Variables
The `SaveUserEpisodeProgress` mutation requires an argument of type `SaveUserEpisodeProgressVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface SaveUserEpisodeProgressVariables {
  episodeId: string;
  progressMs: number;
}
```
### Return Type
Recall that executing the `SaveUserEpisodeProgress` mutation returns a `MutationPromise` that resolves to an object with a `data` property.

The `data` property is an object of type `SaveUserEpisodeProgressData`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:
```typescript
export interface SaveUserEpisodeProgressData {
  userEpisodeProgress_upsert: UserEpisodeProgress_Key;
}
```
### Using `SaveUserEpisodeProgress`'s action shortcut function

```typescript
import { getDataConnect } from 'firebase/data-connect';
import { connectorConfig, saveUserEpisodeProgress, SaveUserEpisodeProgressVariables } from '@musically/dataconnect';

// The `SaveUserEpisodeProgress` mutation requires an argument of type `SaveUserEpisodeProgressVariables`:
const saveUserEpisodeProgressVars: SaveUserEpisodeProgressVariables = {
  episodeId: ..., 
  progressMs: ..., 
};

// Call the `saveUserEpisodeProgress()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await saveUserEpisodeProgress(saveUserEpisodeProgressVars);
// Variables can be defined inline as well.
const { data } = await saveUserEpisodeProgress({ episodeId: ..., progressMs: ..., });

// You can also pass in a `DataConnect` instance to the action shortcut function.
const dataConnect = getDataConnect(connectorConfig);
const { data } = await saveUserEpisodeProgress(dataConnect, saveUserEpisodeProgressVars);

console.log(data.userEpisodeProgress_upsert);

// Or, you can use the `Promise` API.
saveUserEpisodeProgress(saveUserEpisodeProgressVars).then((response) => {
  const data = response.data;
  console.log(data.userEpisodeProgress_upsert);
});
```

### Using `SaveUserEpisodeProgress`'s `MutationRef` function

```typescript
import { getDataConnect, executeMutation } from 'firebase/data-connect';
import { connectorConfig, saveUserEpisodeProgressRef, SaveUserEpisodeProgressVariables } from '@musically/dataconnect';

// The `SaveUserEpisodeProgress` mutation requires an argument of type `SaveUserEpisodeProgressVariables`:
const saveUserEpisodeProgressVars: SaveUserEpisodeProgressVariables = {
  episodeId: ..., 
  progressMs: ..., 
};

// Call the `saveUserEpisodeProgressRef()` function to get a reference to the mutation.
const ref = saveUserEpisodeProgressRef(saveUserEpisodeProgressVars);
// Variables can be defined inline as well.
const ref = saveUserEpisodeProgressRef({ episodeId: ..., progressMs: ..., });

// You can also pass in a `DataConnect` instance to the `MutationRef` function.
const dataConnect = getDataConnect(connectorConfig);
const ref = saveUserEpisodeProgressRef(dataConnect, saveUserEpisodeProgressVars);

// Call `executeMutation()` on the reference to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await executeMutation(ref);

console.log(data.userEpisodeProgress_upsert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.userEpisodeProgress_upsert);
});
```

