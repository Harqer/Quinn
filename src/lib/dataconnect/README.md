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
  - [*RemoveBookmarkedTrack*](#removebookmarkedtrack)
  - [*CreateCameraCapture*](#createcameracapture)
  - [*CreateVideoDigestion*](#createvideodigestion)

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
  albumId: string;
  audioUrl: string;
  coverUrl?: string | null;
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
  albumId: ..., 
  audioUrl: ..., 
  coverUrl: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createTrack(createTrackVars);
// Variables can be defined inline as well.
const { data } = await createTrack({ title: ..., albumId: ..., audioUrl: ..., coverUrl: ..., isCommunity: ..., });

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
  albumId: ..., 
  audioUrl: ..., 
  coverUrl: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrackRef()` function to get a reference to the mutation.
const ref = createTrackRef(createTrackVars);
// Variables can be defined inline as well.
const ref = createTrackRef({ title: ..., albumId: ..., audioUrl: ..., coverUrl: ..., isCommunity: ..., });

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
};

// Call the `createPodcast()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createPodcast(createPodcastVars);
// Variables can be defined inline as well.
const { data } = await createPodcast({ title: ..., publisher: ..., coverUrl: ..., description: ..., });

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
};

// Call the `createPodcastRef()` function to get a reference to the mutation.
const ref = createPodcastRef(createPodcastVars);
// Variables can be defined inline as well.
const ref = createPodcastRef({ title: ..., publisher: ..., coverUrl: ..., description: ..., });

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
  stripeCustomerId: ..., // optional
};

// Call the `upsertUserSettings()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await upsertUserSettings(upsertUserSettingsVars);
// Variables can be defined inline as well.
const { data } = await upsertUserSettings({ theme: ..., parentalControlsEnabled: ..., stripeCustomerId: ..., });
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
  stripeCustomerId: ..., // optional
};

// Call the `upsertUserSettingsRef()` function to get a reference to the mutation.
const ref = upsertUserSettingsRef(upsertUserSettingsVars);
// Variables can be defined inline as well.
const ref = upsertUserSettingsRef({ theme: ..., parentalControlsEnabled: ..., stripeCustomerId: ..., });
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
  likedTrack_insert: LikedTrack_Key;
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

console.log(data.likedTrack_insert);

// Or, you can use the `Promise` API.
likeTrack(likeTrackVars).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_insert);
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

console.log(data.likedTrack_insert);

// Or, you can use the `Promise` API.
executeMutation(ref).then((response) => {
  const data = response.data;
  console.log(data.likedTrack_insert);
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

