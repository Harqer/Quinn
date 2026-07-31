const express = require('express');
const cors = require('cors');
const helmet = require('helmet');

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());

// Dummy data generator for the skeleton endpoints
const generateDummyTracks = (prefix, count) => {
  return Array.from({ length: count }).map((_, i) => ({
    id: `${prefix}-track-${i}`,
    title: `${prefix} Track ${i}`,
    coverUrl: `https://musically-studio.web.app/media/covers/${prefix}-${i}.jpg`,
    album: {
      id: `${prefix}-album-${i}`,
      title: `${prefix} Album ${i}`,
      primaryArtist: {
        id: `artist-${i}`,
        name: `Artist ${i}`
      }
    }
  }));
};

app.get('/api/user/tracks', (req, res) => {
  res.json({ items: generateDummyTracks('user', 10).map(t => ({ track: t })) });
});

app.get('/api/community/tracks', (req, res) => {
  res.json({ items: generateDummyTracks('community', 10).map(t => ({ track: t })) });
});

app.get('/api/playlists', (req, res) => {
  res.json([
    { id: 'pl-1', name: 'Global Top 50', coverUrl: '', description: 'Top 50 tracks globally' },
    { id: 'pl-2', name: 'Viral 50', coverUrl: '', description: 'Most viral tracks' }
  ]);
});

app.get('/api/categories', (req, res) => {
  res.json([
    { id: 'cat-1', name: 'Pop' },
    { id: 'cat-2', name: 'Hip Hop' },
    { id: 'cat-3', name: 'Electronic' }
  ]);
});

app.get('/api/albums', (req, res) => {
  res.json([
    { id: 'al-1', title: 'Greatest Hits', coverUrl: '', releaseDate: '2025-01-01', primaryArtist: { id: 'ar-1', name: 'Superstar' } }
  ]);
});

app.get('/api/podcasts', (req, res) => {
  res.json([
    { id: 'pod-1', title: 'Tech Talk', publisher: 'Techies', coverUrl: '', description: 'All about tech' }
  ]);
});

app.get('/api/audiobooks', (req, res) => {
  res.json([
    { id: 'ab-1', title: 'The Great Story', author: { name: 'Author A' }, narrator: 'Narrator B', coverUrl: '', totalDurationMs: 12000000 }
  ]);
});

// Health check
app.get('/', (req, res) => {
  res.send('metadata-service is running');
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Metadata service listening on port ${PORT}`);
});
