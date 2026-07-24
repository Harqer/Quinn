#!/bin/bash
# scripts/deploy.sh
# Production Deployment Script for Cloud Run
# This script illustrates how to inject Google Cloud Secret Manager payloads natively into the environment

PROJECT_ID="musically-studio"
SERVICE_NAME="musically-backend"
REGION="us-central1"

echo "Deploying ${SERVICE_NAME} to Cloud Run natively mapped with Secret Manager..."

gcloud run deploy ${SERVICE_NAME} \
  --project=${PROJECT_ID} \
  --region=${REGION} \
  --source . \
  --allow-unauthenticated \
  --update-secrets="GEMINI_API_KEY=GEMINI_API_KEY:latest,SPOTIFY_CLIENT_ID=SPOTIFY_CLIENT_ID:latest,SPOTIFY_CLIENT_SECRET=SPOTIFY_CLIENT_SECRET:latest,REDIS_URL=REDIS_URL:latest,VITE_APP_CHECK_KEY=VITE_APP_CHECK_KEY:latest" \
  --min-instances=1 \
  --max-instances=100 \
  --cpu=2 \
  --memory=1Gi

echo "Deployment complete."
