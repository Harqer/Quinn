---
name: scalable-backend-architecture
description: Design scalable backend architectures for apps with large user bases (millions of users), social/sharing capabilities, and relational databases. Use this skill when the user asks about system design, scaling, database schema for social features (likes, bookmarks, shares), connection pooling, or event-driven architectures to handle high throughput without overloading primary databases. Make sure to use this whenever a user discusses handling viral traffic, scaling a relational DB, or syncing social metadata to search engines.
---

# Scalable Backend Architecture & System Design

This skill provides a standardized framework for designing production-grade, highly scalable backend architectures. It focuses on applications backed by relational databases that must handle millions of users, social interactions (likes, shares, bookmarks), and complex catalog queries.

## 1. The Core Scaling Problem

When an application grows to millions of users, the most common bottleneck is **Relational Database (SQL) Contention**.
If every user interaction (e.g., clicking "Like" or "Share") maps to a synchronous `INSERT` or `UPDATE` in the primary SQL database, the database will exhaust its connection pool, encounter row locks, and eventually crash during traffic spikes.

### The Standard Solution: Decoupling Writes
Never write high-velocity interaction data synchronously to the primary database. Instead, use an **Event-Driven Ingestion Pipeline**.

## 2. Event-Driven Interaction Pipeline (The "Buffer" Pattern)

When designing a feature that expects high throughput (like upvoting, liking, or tracking views):

1.  **Ingestion Service (Stateless)**: Deploy a lightweight, auto-scaling API (e.g., Google Cloud Run, AWS Lambda) that accepts the HTTP request.
2.  **Message Queue / PubSub**: The ingestion service immediately publishes the event (e.g., `{"userId": 123, "action": "LIKE", "targetId": 456}`) to a high-throughput message queue (Google Cloud Pub/Sub, Apache Kafka, AWS SQS). The API returns `202 Accepted` to the client instantly.
3.  **Batch Worker**: A background worker subscribes to the queue, buffers the events in memory for a short duration (e.g., 5 seconds or 1000 items), and performs a **batch insert** into the primary relational database. This reduces database connections and write operations by orders of magnitude.

## 3. Relational Database Schema for Social Apps

When storing social graphs and interactions in a relational database, follow these indexing and relationship patterns:

-   **Junction Tables**: Use standard many-to-many junction tables for interactions (e.g., `UserLikes(userId, targetId, timestamp)`).
-   **Denormalization for Reads**: While junction tables are correct, counting rows (e.g., `SELECT COUNT(*) FROM UserLikes WHERE targetId = X`) is slow at scale. You must denormalize by keeping a `likesCount` integer on the primary target table (e.g., `Tracks`, `Posts`).
-   **Updating Counters**: Do not increment `likesCount` synchronously. Use the background batch worker mentioned above to update these counters in bulk.

## 4. Search and Metadata Syncing (Elasticsearch)

Relational databases are poor at full-text search, fuzzy matching, and aggregations across hundreds of millions of rows.

-   **Dedicated Search Engine**: Use a dedicated search index (e.g., Elasticsearch, OpenSearch, Algolia) for all user-facing catalog search features.
-   **Change Data Capture (CDC)**: To keep the search index in sync with the primary relational database without adding application logic, use a CDC tool (e.g., Debezium, Google Cloud Datastream) to tail the database's Write-Ahead Log (WAL).
-   **Event Stream to Index**: The CDC tool pushes database row changes to a message queue, which a worker then consumes to upsert documents in Elasticsearch. This ensures the search catalog is eventually consistent (usually within milliseconds) with the source of truth.

## 5. Architectural Checklist for Review

When providing a backend design to a user, ensure you address the following:

-   [ ] **Connection Pooling**: Are database connections pooled effectively? (e.g., PgBouncer for Postgres).
-   [ ] **Read Replicas**: Are read-heavy queries routed to asynchronous read replicas rather than the primary writer?
-   [ ] **Caching**: Are heavily accessed, rarely mutating objects cached in memory? (e.g., Redis / Memcached).
-   [ ] **Stateless Compute**: Are the API servers completely stateless to allow infinite horizontal scaling?
-   [ ] **Event-Driven Fallbacks**: If the database goes down, does the ingestion queue hold the events until it recovers?

## Usage

When a user asks you to design a feature (e.g., "How do I implement a bookmark system for my app with 10 million users?"):
1.  **Do not** suggest a direct REST endpoint doing `INSERT INTO bookmarks...`.
2.  **Do** propose an ingestion API, a Pub/Sub queue, and a worker that handles batching and search synchronization.
3.  Draw out the architecture explicitly referencing these components.
