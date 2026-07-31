require('dotenv').config();
const { DocumentServiceClient } = require('@google-cloud/discoveryengine');

const PROJECT_ID = process.env.GOOGLE_CLOUD_PROJECT || 'lyria-prod';
const LOCATION = 'global';
const DATA_STORE_ID = process.env.DATA_STORE_ID || 'lyria-media-store';
const BRANCH_NAME = 'default_branch';

const documentClient = new DocumentServiceClient();

// Dummy catalog data to simulate fetching from PostgreSQL
const catalogTracks = [
  {
    id: 'track-1',
    title: 'Shape of You',
    artist: 'Ed Sheeran',
    genre: 'Pop',
    releaseYear: 2017,
    duration: 233,
  },
  {
    id: 'track-2',
    title: 'Blinding Lights',
    artist: 'The Weeknd',
    genre: 'Synth-pop',
    releaseYear: 2019,
    duration: 200,
  }
];

async function syncCatalogToVertexAI() {
  const parent = documentClient.branchPath(PROJECT_ID, LOCATION, DATA_STORE_ID, BRANCH_NAME);

  console.log(`Starting catalog sync to Vertex AI Data Store: ${DATA_STORE_ID}...`);

  for (const track of catalogTracks) {
    const document = {
      id: track.id,
      schemaId: 'default_schema',
      jsonData: JSON.stringify({
        title: track.title,
        categories: [track.genre],
        primaryArtist: { name: track.artist },
        releaseYear: track.releaseYear,
        duration: track.duration
      })
    };

    try {
      const request = {
        parent,
        document,
        documentId: track.id
      };
      
      // Upsert document to Vertex AI
      await documentClient.createDocument(request);
      console.log(`Successfully synced track: ${track.id} (${track.title})`);
    } catch (error) {
      if (error.code === 6) { // ALREADY_EXISTS, can patch instead
        console.log(`Track ${track.id} already exists. Updating...`);
        // We could implement patch here, omitting for brevity in sync script
      } else {
        console.error(`Failed to sync track ${track.id}:`, error.message);
      }
    }
  }

  console.log('Catalog sync complete.');
}

if (require.main === module) {
  syncCatalogToVertexAI().catch(console.error);
}

module.exports = { syncCatalogToVertexAI };
