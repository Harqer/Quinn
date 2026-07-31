#!/bin/bash
set -e

PROJECT_ID="musically-studio"
BUCKET_NAME="${PROJECT_ID}-media"

echo "Creating URL map..."
gcloud compute url-maps create musically-studio-lb \
    --default-service=musically-studio-backend \
    --project=$PROJECT_ID || true

echo "Adding path matcher to route /media/* to the CDN bucket..."
gcloud compute url-maps add-path-matcher musically-studio-lb \
    --default-service=musically-studio-backend \
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
