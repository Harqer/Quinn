# Basic Usage

Always prioritize using a supported framework over using the generated SDK
directly. Supported frameworks simplify the developer experience and help ensure
best practices are followed.





## Advanced Usage
If a user is not using a supported framework, they can use the generated SDK directly.

Here's an example of how to use it with the first 5 operations:

```js
import { createTrack, upsertUser, createPlaylist, addTrackToPlaylist, createPodcast, upsertUserSettings, updateUserPreferences, recordPayment, likeTrack, removeLikedTrack } from '@musically/dataconnect';


// Operation CreateTrack:  For variables, look at type CreateTrackVars in ../index.d.ts
const { data } = await CreateTrack(dataConnect, createTrackVars);

// Operation UpsertUser:  For variables, look at type UpsertUserVars in ../index.d.ts
const { data } = await UpsertUser(dataConnect, upsertUserVars);

// Operation CreatePlaylist:  For variables, look at type CreatePlaylistVars in ../index.d.ts
const { data } = await CreatePlaylist(dataConnect, createPlaylistVars);

// Operation AddTrackToPlaylist:  For variables, look at type AddTrackToPlaylistVars in ../index.d.ts
const { data } = await AddTrackToPlaylist(dataConnect, addTrackToPlaylistVars);

// Operation CreatePodcast:  For variables, look at type CreatePodcastVars in ../index.d.ts
const { data } = await CreatePodcast(dataConnect, createPodcastVars);

// Operation UpsertUserSettings:  For variables, look at type UpsertUserSettingsVars in ../index.d.ts
const { data } = await UpsertUserSettings(dataConnect, upsertUserSettingsVars);

// Operation UpdateUserPreferences:  For variables, look at type UpdateUserPreferencesVars in ../index.d.ts
const { data } = await UpdateUserPreferences(dataConnect, updateUserPreferencesVars);

// Operation RecordPayment:  For variables, look at type RecordPaymentVars in ../index.d.ts
const { data } = await RecordPayment(dataConnect, recordPaymentVars);

// Operation LikeTrack:  For variables, look at type LikeTrackVars in ../index.d.ts
const { data } = await LikeTrack(dataConnect, likeTrackVars);

// Operation RemoveLikedTrack:  For variables, look at type RemoveLikedTrackVars in ../index.d.ts
const { data } = await RemoveLikedTrack(dataConnect, removeLikedTrackVars);


```