---
name: scalable-backend-architecture
description: Design scalable backend architectures using Cloud Run, Cloud SQL, Cloud CDN, and Vertex AI for handling millions of users and interactions.
---

# Scalable Backend Architecture Guidelines

This skill provides architectural patterns for designing highly scalable backends for applications with massive user bases (e.g., millions of users) and large catalogs (e.g., millions of songs/items). It applies to systems that need to handle heavy read traffic, frequent state updates (likes, bookmarks, shares), and personalized recommendations, much like Spotify, but is generalized for any similar domain.

## Core Architectural Pillars

### 1. Global Entry & Edge Caching (Cloud CDN + Load Balancer)
To achieve low latency globally, always place a **Global External Application Load Balancer** in front of your services.
- **Media Delivery**: Serve static assets and media files (MP3s, images) through Cloud CDN. These assets should be highly cacheable at edge nodes, minimizing origin requests and reducing egress costs.
- **Dynamic Content**: API calls for personalized user feeds, interactions, and search should bypass the CDN cache but still benefit from the Load Balancer's global anycast network.

### 2. Stateless Compute Tier (Cloud Run)
Use Serverless compute (Cloud Run) to handle unpredictable traffic spikes without manual scaling.
- **Ingestion Service**: Handles high-frequency write events (likes, bookmarks, shares). It validates requests, authorizes users, and pushes data to the database or message queues.
- **Metadata/Query Service**: Serves track metadata, categories, playlists, and user profiles. 
- **Configuration**: Always configure a minimum number of instances (`min-instances > 0`) in production to prevent cold starts during peak load.

### 3. Relational Database Scaling (Cloud SQL PostgreSQL)
For structured data involving complex relationships (users, playlists, tracks, metadata), use a relational database like PostgreSQL, but scale it properly:
- **Connection Pooling**: Use the Cloud SQL Auth Proxy with built-in connection pooling (or PgBouncer) to prevent connection exhaustion from hundreds of Cloud Run instances.
- **Read Replicas**: Direct heavy read traffic (e.g., fetching catalog lists, searching metadata) to **Read Replicas**. Keep the Primary instance dedicated to writes (interactions, signups).
- **Caching**: Implement a Redis cache (MemoryStore) in front of the database for frequently accessed but rarely changed data (e.g., Global Top 50 playlists, Category lists).

### 4. Recommendation Engine (Vertex AI Vector Search)
Instead of building complex collaborative filtering models from scratch, utilize managed vector databases or recommendation APIs:
- **Event Streaming**: As users interact with content (listen, like, share), the Ingestion Service should asynchronously push these events to a pub/sub topic or directly to Vertex AI Search and Conversation (formerly Retail/Recommendations AI).
- **Semantic Matching**: Use Vertex AI Vector Search to find related items based on metadata embeddings (genre, tempo, user demographics).
- **Personalization**: The backend fetches personalized recommendations from Vertex AI rather than running heavy SQL joins across interaction tables.

### 5. Interaction Tracking & Regional Relevance
- **Regional Popularity**: Cache the most popular content per region at the CDN layer. When a user requests "Trending", the CDN serves the cached regional list.
- **Personalized Overrides**: The frontend should fetch the generic regional list from the CDN, and fetch a smaller, personalized payload from the API. The client or backend can merge these to present a customized feed.

## Implementation Checklist
1. [ ] Deploy Global External HTTP(S) Load Balancer.
2. [ ] Enable Cloud CDN on backend buckets/services serving media.
3. [ ] Configure Cloud SQL with Read Replicas and Cloud SQL Auth Proxy.
4. [ ] Set up Redis for caching static catalog metadata.
5. [ ] Route interaction events (likes/shares) through an Ingestion Service.
6. [ ] Sync catalog and user events to Vertex AI for recommendations.
7. [ ] Ensure Android/Web frontends use standard HTTP requests to the Load Balancer domain rather than direct DB SDK connections.
