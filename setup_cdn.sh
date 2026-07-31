#!/bin/bash
set -e

PROJECT_ID="musically-studio"
BUCKET_NAME="${PROJECT_ID}-media"

echo "Creating media bucket..."
gcloud storage buckets create gs://$BUCKET_NAME --project=$PROJECT_ID --location=us-central1 || true

echo "Making bucket public..."
gcloud storage buckets add-iam-policy-binding gs://$BUCKET_NAME --member=allUsers --role=roles/storage.objectViewer || true

echo "Creating backend bucket with CDN enabled..."
gcloud compute backend-buckets create media-backend-bucket \
    --gcs-bucket-name=$BUCKET_NAME \
    --enable-cdn \
    --project=$PROJECT_ID || true

echo "Creating Serverless NEG for Cloud Run..."
gcloud compute network-endpoint-groups create musically-studio-neg \
    --region=us-central1 \
    --network-endpoint-type=serverless \
    --cloud-run-service=musically-studio \
    --project=$PROJECT_ID || true

echo "Creating backend service for Cloud Run..."
gcloud compute backend-services create musically-studio-backend \
    --global \
    --project=$PROJECT_ID || true

echo "Adding NEG to backend service..."
gcloud compute backend-services add-backend musically-studio-backend \
    --global \
    --network-endpoint-group=musically-studio-neg \
    --network-endpoint-group-region=us-central1 \
    --project=$PROJECT_ID || true

echo "Creating URL map..."
gcloud compute url-maps create musically-studio-lb \
    --default-backend-service=musically-studio-backend \
    --project=$PROJECT_ID || true

echo "Adding path matcher to route /media/* to the CDN bucket..."
gcloud compute url-maps add-path-matcher musically-studio-lb \
    --default-backend-service=musically-studio-backend \
    --path-matcher-name=media-matcher \
    --path-rules="/media/*=media-backend-bucket" \
    --project=$PROJECT_ID || true

echo "Creating target HTTP proxy..."
gcloud compute target-http-proxies create musically-studio-http-proxy \
    --url-map=musically-studio-lb \
    --project=$PROJECT_ID || true

echo "Creating global forwarding rule (Load Balancer IP)..."
gcloud compute forwarding-rules create musically-studio-forwarding-rule \
    --global \
    --target-http-proxy=musically-studio-http-proxy \
    --ports=80 \
    --project=$PROJECT_ID || true

echo "Fetching Load Balancer IP..."
IP_ADDRESS=$(gcloud compute forwarding-rules describe musically-studio-forwarding-rule --global --format="value(IPAddress)" --project=$PROJECT_ID)

echo "Load Balancer successfully configured! IP: $IP_ADDRESS"
