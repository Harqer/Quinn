const express = require('express');

const app = express();
const PORT = 8080;

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Token Endpoint
app.post('/api/token', (req, res) => {
  res.json({ access_token: "mock-access-token", token_type: "Bearer", expires_in: 3600 });
});

// Playlist Tracks Endpoint
app.get('/v1/playlists/37i9dQZEVXbMDoHDwVN2tF/tracks', (req, res) => {
  res.json({
    items: [
      {
        track: {
          name: "Mock Trending Track 1",
          preview_url: "https://mock.com/audio1.mp3",
          duration_ms: 180000,
          artists: [{ name: "Mock Artist 1" }],
          album: {
            images: [{ url: "https://mock.com/cover1.jpg" }]
          }
        }
      },
      {
        track: {
          name: "Mock Trending Track 2",
          preview_url: null, // Should be skipped
          duration_ms: 200000,
          artists: [{ name: "Mock Artist 2" }],
          album: {
            images: [{ url: "https://mock.com/cover2.jpg" }]
          }
        }
      }
    ]
  });
});

const server = app.listen(PORT, () => {
  console.log(`Mock Spotify Server running on port ${PORT}`);
  
  // We need to keep this alive as a background task during the emulator test
});
