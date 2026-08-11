import fetch from 'node-fetch';

describe('fetchSpotifyTrends (LIVE TEST)', () => {
  it('should fetch tracks from the REAL Spotify API and verify response format', async () => {
    const clientId = process.env.SPOTIFY_CLIENT_ID;
    const clientSecret = process.env.SPOTIFY_CLIENT_SECRET;

    if (!clientId || !clientSecret) {
      console.warn("Skipping LIVE Spotify test because credentials are not provided.");
      return;
    }

    // 1. Get Token
    const credentials = Buffer.from(`${clientId}:${clientSecret}`).toString('base64');
    const tokenUrl = 'https://accounts.spotify.com/api/token';
    const tokenResponse = await fetch(tokenUrl, {
      method: 'POST',
      headers: {
        'Authorization': `Basic ${credentials}`,
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: 'grant_type=client_credentials'
    });

    expect(tokenResponse.ok).toBe(true);
    const tokenData = await tokenResponse.json();
    const accessToken = tokenData.access_token;
    expect(accessToken).toBeDefined();

    // 2. Fetch Top 50 Playlist
    const playlistId = "37i9dQZEVXbMDoHDwVN2tF";
    const playlistUrl = `https://api.spotify.com/v1/playlists/${playlistId}/tracks?limit=2`;
    
    const response = await fetch(playlistUrl, {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    expect(response.ok).toBe(true);
    const data = await response.json();
    expect(data.items).toBeDefined();
    expect(data.items.length).toBeGreaterThan(0);
    
    const track = data.items[0].track;
    expect(track).toBeDefined();
    expect(track.name).toBeDefined();
  }, 10000); // 10 second timeout
});
