# Basic Usage

Always prioritize using a supported framework over using the generated SDK
directly. Supported frameworks simplify the developer experience and help ensure
best practices are followed.





## Advanced Usage
If a user is not using a supported framework, they can use the generated SDK directly.

Here's an example of how to use it with the first 5 operations:

```js
import { createTrack, upsertUser, getUserTracks, getCommunityTracks, getCategories, getPlaylists, getAlbums, getPodcasts, getAudiobooks } from '@musically/dataconnect';


// Operation CreateTrack:  For variables, look at type CreateTrackVars in ../index.d.ts
const { data } = await CreateTrack(dataConnect, createTrackVars);

// Operation UpsertUser:  For variables, look at type UpsertUserVars in ../index.d.ts
const { data } = await UpsertUser(dataConnect, upsertUserVars);

// Operation GetUserTracks: 
const { data } = await GetUserTracks(dataConnect);

// Operation GetCommunityTracks: 
const { data } = await GetCommunityTracks(dataConnect);

// Operation GetCategories: 
const { data } = await GetCategories(dataConnect);

// Operation GetPlaylists: 
const { data } = await GetPlaylists(dataConnect);

// Operation GetAlbums: 
const { data } = await GetAlbums(dataConnect);

// Operation GetPodcasts: 
const { data } = await GetPodcasts(dataConnect);

// Operation GetAudiobooks: 
const { data } = await GetAudiobooks(dataConnect);


```