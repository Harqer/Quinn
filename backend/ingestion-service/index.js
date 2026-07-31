require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const admin = require('firebase-admin');
const { PubSub } = require('@google-cloud/pubsub');
const { UserEventServiceClient } = require('@google-cloud/discoveryengine');

// Initialize Firebase Admin (uses GOOGLE_APPLICATION_CREDENTIALS)
if (!admin.apps.length) {
  admin.initializeApp();
}

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());

// Initialize PubSub
const pubsub = new PubSub();
const TOPIC_NAME = process.env.PUBSUB_TOPIC_NAME || 'user-interactions';

// Initialize Vertex AI Discovery Engine (Recommendations)
const discoveryEngine = new UserEventServiceClient();
const PROJECT_ID = process.env.GOOGLE_CLOUD_PROJECT || 'lyria-prod';
const LOCATION = 'global';
const DATA_STORE_ID = process.env.DATA_STORE_ID || 'lyria-media-store';

// Helper to map our events to Vertex AI event types
const getVertexEventType = (type) => {
  switch (type) {
    case 'LIKE': return 'positive-interaction'; // Standard Vertex Media event type for likes
    case 'BOOKMARK': return 'add-to-cart';      // Treating bookmarks as "saved for later"
    case 'SHARE': return 'share';               // Social share
    default: return 'view-item';
  }
};

// Middleware to verify Firebase ID token
const authenticate = async (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).send('Unauthorized: No token provided');
  }

  const idToken = authHeader.split('Bearer ')[1];
  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    req.user = decodedToken;
    next();
  } catch (error) {
    console.error('Error verifying auth token', error);
    res.status(401).send('Unauthorized: Invalid token');
  }
};

// Main ingestion endpoint
app.post('/api/interactions', authenticate, async (req, res) => {
  const { type, entityId, entityType, metadata } = req.body;
  
  if (!type || !entityId || !entityType) {
    return res.status(400).send('Bad Request: Missing required fields');
  }

  const validTypes = ['LIKE', 'BOOKMARK', 'SHARE'];
  if (!validTypes.includes(type)) {
    return res.status(400).send('Bad Request: Invalid interaction type');
  }

  try {
    const messageData = {
      userId: req.user.uid,
      type,
      entityId,
      entityType, // e.g. 'TRACK', 'PODCAST', 'AUDIOBOOK'
      metadata: metadata || {},
      timestamp: new Date().toISOString()
    };

    const dataBuffer = Buffer.from(JSON.stringify(messageData));
    
    // Publish to Pub/Sub
    const messageId = await pubsub.topic(TOPIC_NAME).publishMessage({ data: dataBuffer });
    
    // Asynchronously forward to Vertex AI Search (Recommendations) without blocking the response
    const parent = discoveryEngine.dataStorePath(PROJECT_ID, LOCATION, DATA_STORE_ID);
    discoveryEngine.writeUserEvent({
      parent,
      userEvent: {
        eventType: getVertexEventType(type),
        userPseudoId: req.user.uid,
        eventTime: { seconds: Math.floor(Date.now() / 1000) },
        documents: [{ id: entityId }]
      }
    }).catch(err => console.error("Vertex AI Sync Error:", err.message));

    console.log(`Message ${messageId} published and forwarded to Vertex AI.`);
    res.status(202).json({ success: true, messageId });
  } catch (error) {
    console.error(`Error publishing message: ${error.message}`);
    res.status(500).send('Internal Server Error');
  }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Ingestion service listening on port ${PORT}`);
});
