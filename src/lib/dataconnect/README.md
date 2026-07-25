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
- [**Mutations**](#mutations)
  - [*CreateTrack*](#createtrack)
  - [*UpsertUser*](#upsertuser)
  - [*CreatePlaylist*](#createplaylist)
  - [*AddTrackToPlaylist*](#addtracktoplaylist)

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
    name: string;
    artistName: string;
    albumName: string;
    imageUrl?: string | null;
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
    imageUrl?: string | null;
    type: string;
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
    imageUrl?: string | null;
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
    name: string;
    artistName: string;
    imageUrl?: string | null;
    releaseYear?: number | null;
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
  podcasts: ({
    id: string;
    name: string;
    publisher: string;
    imageUrl?: string | null;
    description?: string | null;
  } & Podcast_Key)[];
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

console.log(data.podcasts);

// Or, you can use the `Promise` API.
getPodcasts().then((response) => {
  const data = response.data;
  console.log(data.podcasts);
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

console.log(data.podcasts);

// Or, you can use the `Promise` API.
executeQuery(ref).then((response) => {
  const data = response.data;
  console.log(data.podcasts);
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
    author: string;
    narrator?: string | null;
    imageUrl?: string | null;
    duration?: number | null;
    audioUrl?: string | null;
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
  name: string;
  artistName: string;
  albumName: string;
  imageUrl?: string | null;
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
  name: ..., 
  artistName: ..., 
  albumName: ..., 
  imageUrl: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrack()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await createTrack(createTrackVars);
// Variables can be defined inline as well.
const { data } = await createTrack({ name: ..., artistName: ..., albumName: ..., imageUrl: ..., isCommunity: ..., });

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
  name: ..., 
  artistName: ..., 
  albumName: ..., 
  imageUrl: ..., // optional
  isCommunity: ..., 
};

// Call the `createTrackRef()` function to get a reference to the mutation.
const ref = createTrackRef(createTrackVars);
// Variables can be defined inline as well.
const ref = createTrackRef({ name: ..., artistName: ..., albumName: ..., imageUrl: ..., isCommunity: ..., });

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
upsertUser(vars?: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

interface UpsertUserRef {
  ...
  /* Allow users to create refs without passing in DataConnect */
  (vars?: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
}
export const upsertUserRef: UpsertUserRef;
```
You can also pass in a `DataConnect` instance to the action shortcut function or `MutationRef` function.
```typescript
upsertUser(dc: DataConnect, vars?: UpsertUserVariables): MutationPromise<UpsertUserData, UpsertUserVariables>;

interface UpsertUserRef {
  ...
  (dc: DataConnect, vars?: UpsertUserVariables): MutationRef<UpsertUserData, UpsertUserVariables>;
}
export const upsertUserRef: UpsertUserRef;
```

If you need the name of the operation without creating a ref, you can retrieve the operation name by calling the `operationName` property on the upsertUserRef:
```typescript
const name = upsertUserRef.operationName;
console.log(name);
```

### Variables
The `UpsertUser` mutation has an optional argument of type `UpsertUserVariables`, which is defined in [dataconnect/index.d.ts](./index.d.ts). It has the following fields:

```typescript
export interface UpsertUserVariables {
  displayName?: string | null;
  email?: string | null;
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

// The `UpsertUser` mutation has an optional argument of type `UpsertUserVariables`:
const upsertUserVars: UpsertUserVariables = {
  displayName: ..., // optional
  email: ..., // optional
};

// Call the `upsertUser()` function to execute the mutation.
// You can use the `await` keyword to wait for the promise to resolve.
const { data } = await upsertUser(upsertUserVars);
// Variables can be defined inline as well.
const { data } = await upsertUser({ displayName: ..., email: ..., });
// Since all variables are optional for this mutation, you can omit the `UpsertUserVariables` argument.
const { data } = await upsertUser();

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

// The `UpsertUser` mutation has an optional argument of type `UpsertUserVariables`:
const upsertUserVars: UpsertUserVariables = {
  displayName: ..., // optional
  email: ..., // optional
};

// Call the `upsertUserRef()` function to get a reference to the mutation.
const ref = upsertUserRef(upsertUserVars);
// Variables can be defined inline as well.
const ref = upsertUserRef({ displayName: ..., email: ..., });
// Since all variables are optional for this mutation, you can omit the `UpsertUserVariables` argument.
const ref = upsertUserRef();

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

