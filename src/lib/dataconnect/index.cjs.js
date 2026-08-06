const { queryRef, executeQuery, validateArgsWithOptions, mutationRef, executeMutation, validateArgs } = require('firebase/data-connect');

const CategoryType = {
  GENRE: "GENRE",
  MOOD: "MOOD",
  ACTIVITY: "ACTIVITY",
}
exports.CategoryType = CategoryType;

const SubscriptionTier = {
  FREE: "FREE",
  PREMIUM: "PREMIUM",
  FAMILY: "FAMILY",
}
exports.SubscriptionTier = SubscriptionTier;

const connectorConfig = {
  connector: 'default',
  service: 'musically-studio',
  location: 'us-central1'
};
exports.connectorConfig = connectorConfig;

const createTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'CreateTrack', inputVars);
}
createTrackRef.operationName = 'CreateTrack';
exports.createTrackRef = createTrackRef;

exports.createTrack = function createTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(createTrackRef(dcInstance, inputVars));
}
;

const upsertUserRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'UpsertUser', inputVars);
}
upsertUserRef.operationName = 'UpsertUser';
exports.upsertUserRef = upsertUserRef;

exports.upsertUser = function upsertUser(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(upsertUserRef(dcInstance, inputVars));
}
;

const createPlaylistRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'CreatePlaylist', inputVars);
}
createPlaylistRef.operationName = 'CreatePlaylist';
exports.createPlaylistRef = createPlaylistRef;

exports.createPlaylist = function createPlaylist(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(createPlaylistRef(dcInstance, inputVars));
}
;

const addTrackToPlaylistRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'AddTrackToPlaylist', inputVars);
}
addTrackToPlaylistRef.operationName = 'AddTrackToPlaylist';
exports.addTrackToPlaylistRef = addTrackToPlaylistRef;

exports.addTrackToPlaylist = function addTrackToPlaylist(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(addTrackToPlaylistRef(dcInstance, inputVars));
}
;

const createPodcastRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'CreatePodcast', inputVars);
}
createPodcastRef.operationName = 'CreatePodcast';
exports.createPodcastRef = createPodcastRef;

exports.createPodcast = function createPodcast(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(createPodcastRef(dcInstance, inputVars));
}
;

const upsertUserSettingsRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'UpsertUserSettings', inputVars);
}
upsertUserSettingsRef.operationName = 'UpsertUserSettings';
exports.upsertUserSettingsRef = upsertUserSettingsRef;

exports.upsertUserSettings = function upsertUserSettings(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars);
  return executeMutation(upsertUserSettingsRef(dcInstance, inputVars));
}
;

const updateUserPreferencesRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'UpdateUserPreferences', inputVars);
}
updateUserPreferencesRef.operationName = 'UpdateUserPreferences';
exports.updateUserPreferencesRef = updateUserPreferencesRef;

exports.updateUserPreferences = function updateUserPreferences(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars);
  return executeMutation(updateUserPreferencesRef(dcInstance, inputVars));
}
;

const recordPaymentRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'RecordPayment', inputVars);
}
recordPaymentRef.operationName = 'RecordPayment';
exports.recordPaymentRef = recordPaymentRef;

exports.recordPayment = function recordPayment(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(recordPaymentRef(dcInstance, inputVars));
}
;

const likeTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'LikeTrack', inputVars);
}
likeTrackRef.operationName = 'LikeTrack';
exports.likeTrackRef = likeTrackRef;

exports.likeTrack = function likeTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(likeTrackRef(dcInstance, inputVars));
}
;

const removeLikedTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'RemoveLikedTrack', inputVars);
}
removeLikedTrackRef.operationName = 'RemoveLikedTrack';
exports.removeLikedTrackRef = removeLikedTrackRef;

exports.removeLikedTrack = function removeLikedTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(removeLikedTrackRef(dcInstance, inputVars));
}
;

const bookmarkTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'BookmarkTrack', inputVars);
}
bookmarkTrackRef.operationName = 'BookmarkTrack';
exports.bookmarkTrackRef = bookmarkTrackRef;

exports.bookmarkTrack = function bookmarkTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(bookmarkTrackRef(dcInstance, inputVars));
}
;

const removeBookmarkedTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'RemoveBookmarkedTrack', inputVars);
}
removeBookmarkedTrackRef.operationName = 'RemoveBookmarkedTrack';
exports.removeBookmarkedTrackRef = removeBookmarkedTrackRef;

exports.removeBookmarkedTrack = function removeBookmarkedTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(removeBookmarkedTrackRef(dcInstance, inputVars));
}
;

const createCameraCaptureRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'CreateCameraCapture', inputVars);
}
createCameraCaptureRef.operationName = 'CreateCameraCapture';
exports.createCameraCaptureRef = createCameraCaptureRef;

exports.createCameraCapture = function createCameraCapture(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars);
  return executeMutation(createCameraCaptureRef(dcInstance, inputVars));
}
;

const createVideoDigestionRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'CreateVideoDigestion', inputVars);
}
createVideoDigestionRef.operationName = 'CreateVideoDigestion';
exports.createVideoDigestionRef = createVideoDigestionRef;

exports.createVideoDigestion = function createVideoDigestion(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(createVideoDigestionRef(dcInstance, inputVars));
}
;

const addChatMessageRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'AddChatMessage', inputVars);
}
addChatMessageRef.operationName = 'AddChatMessage';
exports.addChatMessageRef = addChatMessageRef;

exports.addChatMessage = function addChatMessage(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(addChatMessageRef(dcInstance, inputVars));
}
;

const seedTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'SeedTrack', inputVars);
}
seedTrackRef.operationName = 'SeedTrack';
exports.seedTrackRef = seedTrackRef;

exports.seedTrack = function seedTrack(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(seedTrackRef(dcInstance, inputVars));
}
;

const seedUserRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return mutationRef(dcInstance, 'SeedUser', inputVars);
}
seedUserRef.operationName = 'SeedUser';
exports.seedUserRef = seedUserRef;

exports.seedUser = function seedUser(dcOrVars, vars) {
  const { dc: dcInstance, vars: inputVars } = validateArgs(connectorConfig, dcOrVars, vars, true);
  return executeMutation(seedUserRef(dcInstance, inputVars));
}
;

const getUserTracksRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetUserTracks');
}
getUserTracksRef.operationName = 'GetUserTracks';
exports.getUserTracksRef = getUserTracksRef;

exports.getUserTracks = function getUserTracks(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getUserTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getCommunityTracksRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetCommunityTracks');
}
getCommunityTracksRef.operationName = 'GetCommunityTracks';
exports.getCommunityTracksRef = getCommunityTracksRef;

exports.getCommunityTracks = function getCommunityTracks(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getCommunityTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getCategoriesRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetCategories');
}
getCategoriesRef.operationName = 'GetCategories';
exports.getCategoriesRef = getCategoriesRef;

exports.getCategories = function getCategories(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getCategoriesRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getPlaylistsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetPlaylists');
}
getPlaylistsRef.operationName = 'GetPlaylists';
exports.getPlaylistsRef = getPlaylistsRef;

exports.getPlaylists = function getPlaylists(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getPlaylistsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getAlbumsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetAlbums');
}
getAlbumsRef.operationName = 'GetAlbums';
exports.getAlbumsRef = getAlbumsRef;

exports.getAlbums = function getAlbums(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getAlbumsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getPodcastsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetPodcasts');
}
getPodcastsRef.operationName = 'GetPodcasts';
exports.getPodcastsRef = getPodcastsRef;

exports.getPodcasts = function getPodcasts(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getPodcastsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getAudiobooksRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetAudiobooks');
}
getAudiobooksRef.operationName = 'GetAudiobooks';
exports.getAudiobooksRef = getAudiobooksRef;

exports.getAudiobooks = function getAudiobooks(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getAudiobooksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getUserSettingsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetUserSettings');
}
getUserSettingsRef.operationName = 'GetUserSettings';
exports.getUserSettingsRef = getUserSettingsRef;

exports.getUserSettings = function getUserSettings(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getUserSettingsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getPaymentHistoryRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetPaymentHistory');
}
getPaymentHistoryRef.operationName = 'GetPaymentHistory';
exports.getPaymentHistoryRef = getPaymentHistoryRef;

exports.getPaymentHistory = function getPaymentHistory(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getPaymentHistoryRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getLikedTracksRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetLikedTracks');
}
getLikedTracksRef.operationName = 'GetLikedTracks';
exports.getLikedTracksRef = getLikedTracksRef;

exports.getLikedTracks = function getLikedTracks(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getLikedTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getBookmarkedTracksRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetBookmarkedTracks');
}
getBookmarkedTracksRef.operationName = 'GetBookmarkedTracks';
exports.getBookmarkedTracksRef = getBookmarkedTracksRef;

exports.getBookmarkedTracks = function getBookmarkedTracks(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getBookmarkedTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getUserCameraCapturesRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetUserCameraCaptures');
}
getUserCameraCapturesRef.operationName = 'GetUserCameraCaptures';
exports.getUserCameraCapturesRef = getUserCameraCapturesRef;

exports.getUserCameraCaptures = function getUserCameraCaptures(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getUserCameraCapturesRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getUserVideoDigestionsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetUserVideoDigestions');
}
getUserVideoDigestionsRef.operationName = 'GetUserVideoDigestions';
exports.getUserVideoDigestionsRef = getUserVideoDigestionsRef;

exports.getUserVideoDigestions = function getUserVideoDigestions(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getUserVideoDigestionsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getPlaylistTracksRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetPlaylistTracks', inputVars);
}
getPlaylistTracksRef.operationName = 'GetPlaylistTracks';
exports.getPlaylistTracksRef = getPlaylistTracksRef;

exports.getPlaylistTracks = function getPlaylistTracks(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(getPlaylistTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getCategoryTracksRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetCategoryTracks', inputVars);
}
getCategoryTracksRef.operationName = 'GetCategoryTracks';
exports.getCategoryTracksRef = getCategoryTracksRef;

exports.getCategoryTracks = function getCategoryTracks(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(getCategoryTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getAlbumTracksRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetAlbumTracks', inputVars);
}
getAlbumTracksRef.operationName = 'GetAlbumTracks';
exports.getAlbumTracksRef = getAlbumTracksRef;

exports.getAlbumTracks = function getAlbumTracks(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(getAlbumTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const searchTracksRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'SearchTracks', inputVars);
}
searchTracksRef.operationName = 'SearchTracks';
exports.searchTracksRef = searchTracksRef;

exports.searchTracks = function searchTracks(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(searchTracksRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listChatMessagesRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListChatMessages', inputVars);
}
listChatMessagesRef.operationName = 'ListChatMessages';
exports.listChatMessagesRef = listChatMessagesRef;

exports.listChatMessages = function listChatMessages(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(listChatMessagesRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listSubscriptionPlansRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListSubscriptionPlans');
}
listSubscriptionPlansRef.operationName = 'ListSubscriptionPlans';
exports.listSubscriptionPlansRef = listSubscriptionPlansRef;

exports.listSubscriptionPlans = function listSubscriptionPlans(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(listSubscriptionPlansRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listFaqItemsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListFaqItems');
}
listFaqItemsRef.operationName = 'ListFaqItems';
exports.listFaqItemsRef = listFaqItemsRef;

exports.listFaqItems = function listFaqItems(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(listFaqItemsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listAiPresetsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListAIPresets');
}
listAiPresetsRef.operationName = 'ListAIPresets';
exports.listAiPresetsRef = listAiPresetsRef;

exports.listAiPresets = function listAiPresets(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(listAiPresetsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listFeatureHighlightsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListFeatureHighlights');
}
listFeatureHighlightsRef.operationName = 'ListFeatureHighlights';
exports.listFeatureHighlightsRef = listFeatureHighlightsRef;

exports.listFeatureHighlights = function listFeatureHighlights(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(listFeatureHighlightsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const listHomeSectionsRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'ListHomeSections');
}
listHomeSectionsRef.operationName = 'ListHomeSections';
exports.listHomeSectionsRef = listHomeSectionsRef;

exports.listHomeSections = function listHomeSections(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(listHomeSectionsRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getUsersRef = (dc) => {
  const { dc: dcInstance} = validateArgs(connectorConfig, dc, undefined);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetUsers');
}
getUsersRef.operationName = 'GetUsers';
exports.getUsersRef = getUsersRef;

exports.getUsers = function getUsers(dcOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrOptions, options, undefined,false, false);
  return executeQuery(getUsersRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;

const getTrackRef = (dcOrVars, vars) => {
  const { dc: dcInstance, vars: inputVars} = validateArgs(connectorConfig, dcOrVars, vars, true);
  dcInstance._useGeneratedSdk();
  return queryRef(dcInstance, 'GetTrack', inputVars);
}
getTrackRef.operationName = 'GetTrack';
exports.getTrackRef = getTrackRef;

exports.getTrack = function getTrack(dcOrVars, varsOrOptions, options) {
  
  const { dc: dcInstance, vars: inputVars, options: inputOpts } = validateArgsWithOptions(connectorConfig, dcOrVars, varsOrOptions, options, true, true);
  return executeQuery(getTrackRef(dcInstance, inputVars), inputOpts && { fetchPolicy: inputOpts.fetchPolicy });
}
;
