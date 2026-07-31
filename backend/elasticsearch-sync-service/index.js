require('dotenv').config();
const { PubSub } = require('@google-cloud/pubsub');
const { Client } = require('@elastic/elasticsearch');

const pubsub = new PubSub();
const TOPIC_NAME = process.env.PUBSUB_TOPIC_NAME || 'user-interactions';
const SUBSCRIPTION_NAME = process.env.PUBSUB_SUBSCRIPTION_NAME || 'es-sync-sub';

// Initialize Elasticsearch client
const esClient = new Client({
  node: process.env.ELASTICSEARCH_URL || 'http://localhost:9200',
  auth: {
    username: process.env.ELASTIC_USERNAME || 'elastic',
    password: process.env.ELASTIC_PASSWORD || 'changeme'
  }
});

const ELASTIC_INDEX = process.env.ELASTIC_INDEX || 'interactions_index';

async function initElasticsearch() {
  const indexExists = await esClient.indices.exists({ index: ELASTIC_INDEX });
  if (!indexExists) {
    console.log(`Creating index ${ELASTIC_INDEX}`);
    await esClient.indices.create({ index: ELASTIC_INDEX });
  }
}

async function listenForMessages() {
  const subscription = pubsub.subscription(SUBSCRIPTION_NAME);

  console.log(`Listening for messages on ${SUBSCRIPTION_NAME}...`);

  subscription.on('message', async (message) => {
    try {
      const data = JSON.parse(message.data.toString());
      console.log('Received message:', data);

      // Index or update document in Elasticsearch
      // Using entityId + type + userId as a composite ID for deduplication if needed,
      // or just index as a time-series event stream.
      await esClient.index({
        index: ELASTIC_INDEX,
        document: {
          userId: data.userId,
          type: data.type,
          entityId: data.entityId,
          entityType: data.entityType,
          metadata: data.metadata,
          timestamp: data.timestamp
        }
      });

      // Update the main entity document (Track/Podcast/Audiobook) popularity score in ES
      // This is a simplified version of upserting a popularity counter
      const entityIndex = data.entityType.toLowerCase() + 's'; // e.g. tracks, podcasts
      try {
        await esClient.update({
          index: entityIndex,
          id: data.entityId,
          script: {
            source: 'ctx._source.popularity = (ctx._source.popularity ?: 0) + 1',
            lang: 'painless'
          },
          upsert: {
            id: data.entityId,
            type: data.entityType,
            popularity: 1
          }
        });
        console.log(`Updated popularity for ${data.entityType} ${data.entityId}`);
      } catch (updateError) {
        console.error(`Warning: Failed to update entity popularity: ${updateError.message}`);
      }

      message.ack();
      console.log('Message acknowledged.');
    } catch (error) {
      console.error('Error processing message:', error);
      // Depending on error, we might nack() to retry
      // message.nack();
      message.ack(); // Acknowledge to prevent infinite retry loop on bad payload
    }
  });

  subscription.on('error', error => {
    console.error('Subscription error:', error);
  });
}

async function start() {
  try {
    await initElasticsearch();
    await listenForMessages();
  } catch (error) {
    console.error('Failed to start sync service:', error);
    process.exit(1);
  }
}

start();
